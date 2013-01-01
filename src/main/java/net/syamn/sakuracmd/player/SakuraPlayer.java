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
    
    /* *** Status ******* */
    private boolean isAfk = false;
    
    public SakuraPlayer(final Player player){
        this.player = player;
        this.data = new PlayerData(player.getName());
    }
    
    public Player getPlayer(){
        return this.player;
    }
    
    public SakuraPlayer setPlayer(final Player player){
        initStatus();
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
    
    public void initStatus(){
        this.isAfk = false;
    }
    
    /* *** Status getter/setter */
    public boolean isAfk(){
        return this.isAfk;
    }
    public void setAfk(final boolean afk){
        this.isAfk = afk;
    }
}
