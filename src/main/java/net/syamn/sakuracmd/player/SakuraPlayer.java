/**
 * SakuraCmd - Package: net.syamn.sakuracmd.player
 * Created: 2013/01/01 22:27:55
 */
package net.syamn.sakuracmd.player;

import net.syamn.sakuracmd.ConfigurationManager;
import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.sakuracmd.worker.AFKWorker;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.entity.Player;

/**
 * SakuraPlayer (SakuraPlayer.java)
 * @author syam(syamn)
 */
public class SakuraPlayer {
    private final ConfigurationManager config;
    
    private Player player;
    private PlayerData data;
    
    /* *** Status ******* */
    public SakuraPlayer(final Player player){
        this.player = player;
        this.data = new PlayerData(player.getName());
        
        this.config = SakuraCmd.getInstance().getConfigs();
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
    
    public String getName(){
        if (player == null){
            throw new IllegalStateException("Null Player!");
        }
        
        if (config.getUseNamePrefix()){
            throw new NotImplementedException();//TODO
        }else{
            return (config.getUseDisplayname()) ? player.getDisplayName() : player.getName();
        }
    }
    
    public PlayerData getData(){
        return data;
    }
    
    public void initStatus(){
        //this.isAfk = false;
    }
    
    /* *** Status getter/setter */
    public boolean isAfk(){
        return AFKWorker.getInstance().isAfk(this.player);
    }
}
