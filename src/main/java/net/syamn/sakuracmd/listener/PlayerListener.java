/**
 * SakuraCmd - Package: net.syamn.sakuracmd.listener
 * Created: 2012/12/31 3:18:20
 */
package net.syamn.sakuracmd.listener;

import net.syamn.sakuracmd.Perms;
import net.syamn.sakuracmd.SakuraCmd;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

/**
 * PlayerListener (PlayerListener.java)
 * @author syam(syamn)
 */
public class PlayerListener implements Listener{
    
    private SakuraCmd plugin;
    public PlayerListener (final SakuraCmd plugin){
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(final PlayerLoginEvent event){
        if (!event.getResult().equals(Result.ALLOWED)){
            return;
        }
        if (plugin.getServerManager().isLockdown() && !Perms. LOCKDOWN_BYPASS.has(event.getPlayer())){
            event.disallow(Result.KICK_OTHER, "Server Locked by administration");
        }
    }
}
