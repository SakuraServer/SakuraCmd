/**
 * SakuraCmd - Package: net.syamn.sakuracmd.feature
 * Created: 2013/02/11 4:51:37
 */
package net.syamn.sakuracmd.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.sakuracmd.enums.FileLog;
import net.syamn.sakuracmd.enums.PartyStatus;
import net.syamn.sakuracmd.feature.SpecialItem;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.utils.LogUtil;
import net.syamn.utils.StrUtil;
import net.syamn.utils.TimeUtil;
import net.syamn.utils.Util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

/**
 * HardEndManager (HardEndManager.java)
 * @author syam(syamn)
 */
public class HardEndManager {
    private static HardEndManager instance = null;

    private SakuraCmd plugin;
    private PartyStatus status = PartyStatus.WAITING;
    private boolean openParty = false;

    private Map<String, Boolean> members = new HashMap<String, Boolean>();
    public Set<String> invited = new HashSet<String>();

    private int timeOpened = -1;
    private int timeStarted = -1;
    private int timeUpdate = -1;

    private TimeCheckTask task;
    private int taskID = -1;

    private HardEndManager(final SakuraCmd plugin){
        this.plugin = plugin;
        this.task = new TimeCheckTask();
        this.taskID = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, this.task, 20, 20).getTaskId();
    }
    public static HardEndManager getInstance(){
        return instance;
    }
    public static HardEndManager createInstance(final SakuraCmd plugin){
        instance = new HardEndManager(plugin);
        return instance;
    }
    public static void dispose(){
        if (instance != null){
            instance.onDispose();
        }
        instance = null;
    }

    public void load(final FileConfiguration conf){
        ConfigurationSection cs = conf.getConfigurationSection("HardEndData");
        if (cs == null) return;

        this.status = StrUtil.isMatches(PartyStatus.values(), cs.getString("Status", "WAITING"));
        if (this.status == null) this.status = PartyStatus.WAITING;
        this.openParty = cs.getBoolean("IsOpenParty", false);

        this.timeOpened = cs.getInt("TimeOpened", -1);
        this.timeStarted = cs.getInt("TimeStarted", -1);
        this.timeUpdate = cs.getInt("TimeUpadte", -1);

        ConfigurationSection cs2 = cs.getConfigurationSection("Members");
        if (cs2 == null) return;

        this.members.clear();
        for (final String key : cs2.getKeys(false)){
            this.members.put(key, cs2.getBoolean(key, false));
        }
    }
    public void save(final FileConfiguration conf){
        ConfigurationSection cs = conf.createSection("HardEndData");

        cs.set("Status", this.status.name());
        cs.set("IsOpenParty", this.openParty);

        cs.set("TimeOpened", this.timeOpened);
        cs.set("TimeStarted", this.timeStarted);
        cs.set("TimeUpadte", this.timeUpdate);

        if (this.members.isEmpty()) return;

        ConfigurationSection cs2 = cs.createSection("Members");
        for (final Map.Entry<String, Boolean> entry : members.entrySet()){
            cs2.set(entry.getKey(), entry.getValue().booleanValue());
        }
    }

    private void onDispose(){
        if (taskID != -1){
            Bukkit.getScheduler().cancelTask(taskID);
            taskID = -1;
        }
    }

    public void openParty(final boolean open, final Player sender){
        if (!PartyStatus.WAITING.equals(status)){
            throw new IllegalStateException("Party status must be waiting");
        }
        checkWorld();

        status = PartyStatus.OPENING;
        timeOpened = TimeUtil.getCurrentUnixSec().intValue();
        openParty = open;
        invited.clear();

        members.clear();
        addMember(sender.getName(), true);

        String typemsg = (open) ? "&bオープンパーティ" : "&cクローズパーティ";
        Util.broadcastMessage(" &7\"&f" + (PlayerManager.getPlayer(sender).getName()) + "&7\" &dがハードエンドの" + typemsg + "&dを開きました！");
        if (open){
            Util.broadcastMessage(" &dこの討伐パーティには受付所から参加登録することができます！");
        }else{
            Util.broadcastMessage(" &dこの討伐パーティはリーダーからの招待が必要です！");
        }
        FileLog.HARD_END.log("Opened " + ((openParty) ? "open" : "close") + " party by " + sender.getName());
    }

    public void startParty(){
        if (!PartyStatus.OPENING.equals(status)){
            throw new IllegalStateException("Party status must be opening");
        }
        if (members.size() < getMinPlayers()){
            throw new IllegalStateException("Too few party members (" + members.size() + "<" + getMinPlayers() + ")");
        }
        if (members.size() > getMaxPlayers()){
            throw new IllegalStateException("Too many party members (" + members.size() + ">" + getMaxPlayers() + ")");
        }

        final World world = checkWorld();
        for (final Player player : world.getPlayers()){
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation(), TeleportCause.PLUGIN);
        }

        // announce
        List<String> names = new ArrayList<String>(members.size());
        for (final Map.Entry<String, Boolean> entry : members.entrySet()){
            if (entry.getValue()){
                names.add("&6" + entry.getKey());
            }else{
                names.add("&3" + entry.getKey());
            }
        }
        Util.broadcastMessage(" &dハードエンド討伐チャレンジが開始されました！");
        Util.broadcastMessage(" &dパーティメンバー: " + StrUtil.join(names, "&7, "));
        FileLog.HARD_END.log("Party start, members(" + members.size() + "): " + Util.stripColors(StrUtil.join(names, ", ")));

        // update status
        status = PartyStatus.STARTING;
        timeStarted = TimeUtil.getCurrentUnixSec().intValue();
        invited.clear();

        // teleport players
        final Location to = world.getSpawnLocation().clone();
        final Block baseBlock = to.getBlock().getRelative(BlockFace.DOWN, 1);
        Block block; // check ground
        for (int x = baseBlock.getX() - 1; x <= baseBlock.getX() + 1; x++) {
            for (int z = baseBlock.getZ() - 1; z <= baseBlock.getZ() + 1; z++) {
                block = baseBlock.getWorld().getBlockAt(x, baseBlock.getY(), z);
                if (block.getType() != Material.OBSIDIAN) {
                    block.setType(Material.OBSIDIAN);
                }
            }
        }
        Player member;
        for (final String name : members.keySet()){
            member = Bukkit.getPlayerExact(name);
            if (member == null || !member.isOnline()){
                continue;
            }
            member.teleport(world.getSpawnLocation(), TeleportCause.PLUGIN);
        }
    }

    public void dragonKilled(){
        if (!PartyStatus.STARTING.equals(status)){
            //throw new IllegalStateException("Party status must be starting");
            LogUtil.warning("Party status must be starting");
            return;
        }

        message("&aハードエンドドラゴン討伐おめでとうございます！");
        FileLog.HARD_END.log("Dragon killed, party closes");
        cleanup();
        this.timeUpdate = TimeUtil.getCurrentUnixSec().intValue();
    }

    public List<ItemStack> getDropItems(){
        List<ItemStack> ret = new ArrayList<>();

        Random ran = new Random();

        // ender crystallizer
        for (int i = 0; i <= ran.nextInt(2); i++){ // 0, 1
            ItemStack crystal = new ItemStack(Material.BLAZE_POWDER, 1);
            final int remain = (ran.nextInt(10) >= 7) ? 2 : 0; // 2(30%) or 1(70%)
            crystal = SpecialItem.createSpecialItem(crystal, SpecialItem.Type.CRYSTAL, remain, TimeUtil.getCurrentUnixSec().intValue() + 2592000); // 30days
            ret.add(crystal);
        }

        return ret;
    }

    private World checkWorld(){
        final World w = Bukkit.getWorld(Worlds.hard_end);
        if (w == null){
            throw new IllegalStateException("World " + Worlds.hard_end + " is not loaded!");
        }
        return w;
    }

    private void timeup(){
        if (status == PartyStatus.WAITING){
            throw new IllegalStateException("status must not be waiting");
        }

        if (status == PartyStatus.OPENING){
            message("&cこのパーティは一定時間以内に開始されなかったため削除されました！");
            cleanup();
            Util.broadcastMessage("&cハードエンド討伐パーティは一定時間以内に開始されなかったため削除されました");
        }
        else if (status == PartyStatus.STARTING){
            message("&c時間切れで討伐に失敗しました");
            
            final World world = Bukkit.getWorld(Worlds.hard_end);
            
            // move players
            Player p;
            for (final String name : members.keySet()){
                p = Bukkit.getPlayerExact(name);
                if (p != null && p.isOnline() && p.getWorld().getName().equals(Worlds.hard_end)){
                    p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation(), TeleportCause.PLUGIN);
                }
            }
            
            // clear mobs
            for (final Entity ent : world.getEntities()){
                if (ent == null || (ent instanceof Player)/* || (ent instanceof EnderDragon)*/){ // remove dragon
                    continue;
                }
                if ((ent instanceof TNTPrimed)|| (ent instanceof LivingEntity)){
                    ent.remove();
                }
            }
            
            cleanup();
            Util.broadcastMessage("&cハードエンド討伐は時間切れで失敗しました");
            this.timeUpdate = TimeUtil.getCurrentUnixSec().intValue();
        }
    }

    public void cleanup(){
        this.status = PartyStatus.WAITING;
        this.invited.clear();
        //this.members.clear(); // don't clear membersmap
        this.timeStarted = this.timeOpened = -1;
    }

    public void message(final String msg){
        Player p;
        for (final String name : members.keySet()){
            p = Bukkit.getPlayerExact(name);
            if (p != null && p.isOnline()){
                Util.message(p, msg);
            }
        }
    }

    /* getter/setter */
    public void addMember(final String playerName, final boolean leader){
        members.put(playerName.toLowerCase(Locale.ENGLISH), leader);
    }
    public void removeMember(final String playerName){
        members.remove(playerName.toLowerCase(Locale.ENGLISH));
    }

    public boolean isMember(final Player player){
        return isMember(player.getName());
    }
    public boolean isMember(final String playerName){
        if (members == null) return false;
        return members.containsKey(playerName.toLowerCase(Locale.ENGLISH));
    }

    public boolean isLeader(final Player player){
        return isLeader(player.getName());
    }
    public boolean isLeader(final String playerName){
        if (members == null || !members.containsKey(playerName.toLowerCase(Locale.ENGLISH)))
            return false;

        return members.get(playerName.toLowerCase(Locale.ENGLISH));
    }

    public void setLeader(final String playerName, final boolean leader){
        if (!isMember(playerName)){
            throw new IllegalStateException("player " + playerName + " must be member!");
        }
        members.put(playerName.toLowerCase(Locale.ENGLISH), leader);
    }

    public Map<String, Boolean> getMembersMap(){
        if (status == PartyStatus.WAITING){
            return null;
        }
        return Collections.unmodifiableMap(this.members);
    }

    public PartyStatus getStatus(){
        return this.status;
    }

    public boolean isOpenParty(){
        return openParty;
    }

    public int getMinPlayers(){
        return plugin.getWorker().getConfig().getHardendMinPlayers();
    }
    public int getMaxPlayers(){
        return plugin.getWorker().getConfig().getHardendMaxPlayers();
    }

    public int getTimeHours(){
        return plugin.getWorker().getConfig().getHardendTimeLimitHours();
    }
    public int getRemainSeconds(){
        if (status != PartyStatus.STARTING){
            throw new IllegalStateException("status must be starting");
        }

        return (timeStarted + getTimeHours() * 60 * 60) - TimeUtil.getCurrentUnixSec().intValue();
    }

    public int getTimeOpenedMinutes(){
        return plugin.getWorker().getConfig().getHardendTimeLimitOpenedMinutes();
    }
    public int getRemainOpenedSeconds(){
        if (status != PartyStatus.OPENING){
            throw new IllegalStateException("status must be opening");
        }

        return (timeOpened + getTimeOpenedMinutes() * 60) - TimeUtil.getCurrentUnixSec().intValue();
    }

    public int getCooldownHours(){
        return plugin.getWorker().getConfig().getHardendCooldownHours();
    }
    public int getRemainCooldownSeconds(){
        if (status != PartyStatus.WAITING){
            throw new IllegalStateException("status must be waiting");
        }

        if (this.timeUpdate < 0){
            return 0;
        }
        return (this.timeUpdate + getCooldownHours() * 60 * 60) - TimeUtil.getCurrentUnixSec().intValue();
    }

    // call async
    class TimeCheckTask implements Runnable{
        @Override
        public void run() {
            // do nothing when waiting status
            if (status == PartyStatus.WAITING){
                return;
            }

            if (status == PartyStatus.OPENING){
                int remain = getRemainOpenedSeconds();

                if (remain <= 0){
                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                        @Override public void run(){
                            timeup();
                        }
                    }, 0L);
                    return;
                }
                else if (remain == 30 || remain <= 10){
                    sendNotify(" &f[&c+&f] &6この討伐パーティはあと " + remain + "秒 で登録取消されます");
                }
                else if (remain % 60 == 0){
                    int min = remain / 60;
                    if (min == 10 || min <= 5){
                        sendNotify(" &f[&c+&f] &6この討伐パーティはあと " + min + "分 で登録取消されます");
                    }
                }
            }
            else if (status == PartyStatus.STARTING){
                int remain = getRemainSeconds();

                if (remain <= 0){
                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                        @Override public void run(){
                            timeup();
                        }
                    }, 0L);
                    return;
                }
                else if (remain == 30 || remain <= 10){
                    sendNotify(" &f[&c+&f] &6ハードエンド討伐制限時間まであと " + remain + "秒 です");
                }
                else if (remain % 60 == 0 && remain <= 3600){ // 60 mins = 3600 secs
                    int min = remain / 60;
                    if (min % 10 == 0 || min <= 5){
                        sendNotify(" &f[&c+&f] &6ハードエンド討伐制限時間まであと " + min + "分 です");
                    }
                }
                else if (remain % 3600 == 0){
                    sendNotify(" &f[&c+&f] &6ハードエンド討伐制限時間まであと " + (remain / 3600) + "時間 です");
                }
            }
        }

        private void sendNotify(final String msg){
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override public void run(){
                    message(msg);
                }
            }, 0L);
        }
    }
}
