/**
 * SakuraCmd - Package: net.syamn.sakuracmd.player
 * Created: 2013/01/01 22:27:55
 */
package net.syamn.sakuracmd.player;

import org.bukkit.entity.Player;

/**
 * SakuraPlayer (SakuraPlayer.java)
 * @author syam(syamn)
 */
public class SakuraPlayer {
    private Player player;
    private PlayerData data;
    
    public SakuraPlayer(final Player player){
        this.player = player;
        this.data = new PlayerData(player.getName());
    }
    
    public Player getPlayer(){
        return this.player;
    }
    
    public SakuraPlayer setPlayer(final Player player){
        this.player = player;
        // Validate player instance
        if (!player.getName().equalsIgnoreCase(this.data.getPlayerName())){
            throw new IllegalStateException("Wrong player instance! Player: " + player.getName() + " Data: " + this.data.getPlayerName());
        }
        return this;
    }
    
    public PlayerData getData(){
        return data;
    }
}
