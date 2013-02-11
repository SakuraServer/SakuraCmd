/**
 * SakuraCmd - Package: net.syamn.sakuracmd.feature
 * Created: 2013/02/11 4:51:37
 */
package net.syamn.sakuracmd.feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.syamn.sakuracmd.enums.PartyStatus;
import net.syamn.sakuracmd.manager.Worlds;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.utils.StrUtil;
import net.syamn.utils.Util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

/**
 * HardEndManager (HardEndManager.java)
 * @author syam(syamn)
 */
public class HardEndManager {
    //private final ConcurrentHashMap<Player, Long> playerTimeStamp = new ConcurrentHashMap<Player, Long>();
    //private final ConcurrentHashMap<Player, Long> afkPlayers = new ConcurrentHashMap<Player, Long>();

    //private final HardEndManager afkChecker;
    private static HardEndManager instance = new HardEndManager();
    
    private PartyStatus status = PartyStatus.WAITING;
    private boolean openParty = false;
    
    private Map<String, Boolean> members = new HashMap<String, Boolean>();
    public Set<String> invited = new HashSet<String>();
    
    private int minPlayers = 1;

    private HardEndManager(){
        //this.afkChecker = new HardEndManager();
    }
    public static HardEndManager getInstance(){
        return instance;
    }
    public static HardEndManager createInstance(){
        instance = new HardEndManager();
        return instance;
    }
    public static void dispose(){
        instance = null;
    }
    
    public void openParty(final boolean open, final Player sender){
        if (!PartyStatus.WAITING.equals(status)){
            throw new IllegalStateException("Party status must be waiting");
        }
        checkWorld();
        
        status = PartyStatus.OPENING;
        openParty = open;
        invited.clear();
        
        addMember(sender.getName(), true);
        
        String typemsg = (open) ? "&bオープンパーティ" : "&cクローズパーティ";
        Util.broadcastMessage(" &7\"&f" + (PlayerManager.getPlayer(sender).getName()) + "&7\" &dがハードエンドの" + typemsg + "&dを開きました！");
        if (open){
            Util.broadcastMessage(" &dこの討伐パーティには受付所から参加登録することができます！");
        }else{
            Util.broadcastMessage(" &dこの討伐パーティはリーダーからの招待が必要です！");
        }
    }
    
    public void startParty(){
        if (!PartyStatus.OPENING.equals(status)){
            throw new IllegalStateException("Party status must be opening");
        }
        if (members.size() < minPlayers){
            throw new IllegalStateException("Too few party members (" + members.size() + "<" + minPlayers + ")");
        }
        
        final World world = checkWorld();
        
        for (final Player player : world.getPlayers()){
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation(), TeleportCause.PLUGIN);
        }        
        
        
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
        
        status = PartyStatus.STARTING;
        invited.clear();
        
        Player member;
        for (final String name : members.keySet()){
            member = Bukkit.getPlayerExact(name);
            if (member == null || !member.isOnline()){
                continue;
            }
            member.teleport(world.getSpawnLocation(), TeleportCause.PLUGIN);
        }
    }
    
    private World checkWorld(){
        final World w = Bukkit.getWorld(Worlds.hard_end);
        if (w == null){
            throw new IllegalStateException("World " + Worlds.hard_end + " is not loaded!");
        }
        return w;
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
        return this.minPlayers;
    }
}
