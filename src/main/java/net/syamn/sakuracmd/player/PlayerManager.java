/**
 * SakuraCmd - Package: net.syamn.sakuracmd.player
 * Created: 2013/01/01 23:18:45
 */
package net.syamn.sakuracmd.player;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

/**
 * PlayerManager (PlayerManager.java)
 * @author syam(syamn)
 */
public class PlayerManager {
    private static ConcurrentHashMap<String, SakuraPlayer> players = new ConcurrentHashMap<String, SakuraPlayer>();
    
    public static SakuraPlayer addPlayer(final Player player){
        SakuraPlayer sPlayer = players.get(player.getName());
        
        if (sPlayer != null){
            sPlayer.setPlayer(player);
        }else{
            sPlayer = new SakuraPlayer(player);
            players.put(player.getName(), sPlayer);
        }
        
        return sPlayer;
    }
    
    public static void remove(String playerName){
        players.remove(playerName);
    }
    
    public static void clearAll(){
        players.clear();
    }
    
    public static SakuraPlayer getPlayer(final String playerName){
        return players.get(playerName);
    }
    
    public static PlayerData getData(final String playerName){
        final SakuraPlayer sp = players.get(playerName);
        if (sp != null){
            return sp.getData();
        }
        
        return PlayerData.getDataIfExists(playerName);
    }
    
    public static PlayerData getDataIfOnline(final String playerName){
        final SakuraPlayer sp = players.get(playerName);
        if (sp != null && sp.getPlayer() != null && sp.getPlayer().isOnline()){
            return sp.getData();
        }
        return null;
    }
}
