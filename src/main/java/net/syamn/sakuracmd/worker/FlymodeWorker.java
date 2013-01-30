/**
 * SakuraCmd - Package: net.syamn.sakuracmd.worker
 * Created: 2013/01/12 18:45:12
 */
package net.syamn.sakuracmd.worker;

import static org.mockito.Matchers.contains;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.sakuracmd.manager.Worlds;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.sakuracmd.player.PlayerData;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.sakuracmd.player.Power;
import net.syamn.sakuracmd.player.SakuraPlayer;
import net.syamn.sakuracmd.utils.plugin.SakuraCmdUtil;
import net.syamn.utils.LogUtil;
import net.syamn.utils.TimeUtil;
import net.syamn.utils.Util;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * FlymodeWorker (FlymodeWorker.java)
 * @author syam(syamn)
 */
public class FlymodeWorker {
    private final ConcurrentHashMap<String, Integer> flymodePlayers = new ConcurrentHashMap<String, Integer>();
    
    private static FlymodeWorker instance;
    private SakuraCmd plugin;
    private FlymodeTask task;
    
    private FlymodeWorker(){
        this.task = new FlymodeTask();
    }
    
    public static FlymodeWorker getInstance(){
        if (instance == null){
            synchronized (FlymodeWorker.class) {
                if (instance == null){
                    instance = new FlymodeWorker();
                    instance.plugin = SakuraCmd.getInstance();
                }
            }
        }
        return instance;
    }
    public static void dispose(){
        if (instance != null && instance.flymodePlayers != null && instance.flymodePlayers.size() > 0){
            for (final String name : instance.flymodePlayers.keySet()){
                final Player p = Bukkit.getPlayerExact(name);
                if (p != null && p.isOnline()){
                    instance.changeFlyMode(p, false);
                    Util.message(p, "&cあなたの飛行権はプラグイン更新のため一時的に停止されました");
                }
            }
        }
        
        instance.plugin = null;
        instance = null;
    }
    
    public void onPluginEnabled(){
        if (flymodePlayers.size() > 0){
            int now = TimeUtil.getCurrentUnixSec().intValue();
            for (final Map.Entry<String, Integer> entry : flymodePlayers.entrySet()){
                if ((entry.getValue() - now) <= 0){
                    continue;
                }
                
                final Player player = Bukkit.getPlayerExact(entry.getKey());
                if (player != null && player.isOnline()){
                    changeFlyMode(player, true);
                    Util.message(player, "&aプラグインが有効になり、あなたの飛行権が復活しました");
                }
            }
        }
    }
    
    public FlymodeTask getTask(){
        return this.task;
    }
    
    public void enableFlymode(final SakuraPlayer sp, final int minute){
        if (sp == null || sp.getPlayer() == null){
            throw new IllegalArgumentException("player must not be null!");
        }
        
        int expired = TimeUtil.getCurrentUnixSec().intValue() + (minute * 60);
        flymodePlayers.put(sp.getPlayer().getName(), expired);
        sp.addPower(Power.FLYMODE);
        
        changeFlyMode(sp.getPlayer(), true);
    }
    
    public void disableFlymode(final String name){
        if (flymodePlayers.containsKey(name)){
            flymodePlayers.remove(name);
        }
        
        PlayerData data = PlayerManager.getDataIfOnline(name);
        if (data == null){
            data = PlayerManager.getData(name);
        }
        
        data.removePower(Power.FLYMODE);
        changeFlyMode(Bukkit.getPlayerExact(name), false);
        //SakuraCmdUtil.changeFlyMode(Bukkit.getPlayerExact(name), false);
    }
    
    public void checkRestoreFlymode(final SakuraPlayer sp){
        if (sp == null || sp.getPlayer() == null){
            throw new IllegalArgumentException("player must not be null!");
        }
        
        final Player player = sp.getPlayer();
        
        if (!flymodePlayers.containsKey(player.getName())){ // don't check permission
            sp.removePower(Power.FLYMODE);
            changeFlyMode(player, false);
            return;
        }
        // player has flymode power
        
        changeFlyMode(player, true);
    }
    
    public String getRemainTime(final String name){
        if (!flymodePlayers.containsKey(name)){
            throw new IllegalArgumentException(name + " is not in flymode players map!");
        }
        
        final int remain = flymodePlayers.get(name) - TimeUtil.getCurrentUnixSec().intValue();
        if (remain <= 0){
            return "0秒";
        }
        return TimeUtil.getReadableTimeBySecond(remain);
    }
    public String getRemainTime(final Player player){
        return getRemainTime(player.getName());
    }
    
    public void changeFlyMode(final Player player, final boolean enable){
        if (player == null){
            return;
        }
        
        if (enable){
            if (Worlds.isFlyAllowed(player.getWorld().getName())){
                SakuraCmdUtil.changeFlyMode(player, true);
            }
        }else{
            if (!player.getGameMode().equals(GameMode.CREATIVE) && !PlayerManager.getPlayer(player).hasPower(Power.FLY)){
                SakuraCmdUtil.changeFlyMode(player, false);
            }
        }
    }
    
    // call async
    class FlymodeTask implements Runnable{
        @Override
        public void run() {
            // don't run when flymodePlayers.size == 0
            if (flymodePlayers.size() == 0){
                return;
            }
            
            int curr = TimeUtil.getCurrentUnixSec().intValue();
            for (final Entry<String, Integer> entry : flymodePlayers.entrySet()){
                final String name = entry.getKey();
                int remain = entry.getValue() - curr;
                
                if (remain <= 0){
                    flymodePlayers.remove(name);
                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                        @Override public void run(){
                            disableFlymode(name);
                        }
                    }, 0L);
                    sendNotify(name, "&a飛行モードが終了しました！");
                    LogUtil.info("Player " + name + " is expired flying mode!");
                    SakuraCmdUtil.sendlog("&6" + name + " の飛行権限が期限切れで終了しました");
                }
                else if (remain <= 5 || remain == 10 || remain == 30 || remain == 60){
                    sendNotify(name, " &f[&c+&f] &6あと &b" + remain + "秒 &6で飛行モードが終了します");
                }
            }
        }
        
        private void sendNotify(final String name, final String msg){
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override public void run(){
                    final Player p = Bukkit.getPlayerExact(name);
                    if (p != null && p.isOnline()){
                        Util.message(p, msg);
                    }
                }
            }, 0L);
        }
    }
    
    public Map<String, Integer> getFlymodePlayers(){
        return Collections.unmodifiableMap(flymodePlayers);
    }
    public void addFlymodePlayersMap(final String name, final Integer expire){
        flymodePlayers.put(name.trim(), expire);
    }
    public int getPlayersCount(){
        return flymodePlayers.size();
    }
}
