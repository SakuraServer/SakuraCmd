/**
 * SakuraCmd - Package: net.syamn.sakuracmd.listener.feature
 * Created: 2013/01/12 17:29:04
 */
package net.syamn.sakuracmd.listener.feature;

import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.sakuracmd.player.SakuraPlayer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

/**
 * BackLocationListener (BackLocationListener.java)
 * @author syam(syamn)
 */
public class BackLocationListener implements Listener{
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(final PlayerTeleportEvent event){
        if (event.getCause() != TeleportCause.PLUGIN && event.getCause() != TeleportCause.COMMAND){
            return;
        }
        setLastLocation(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerRespawn(final PlayerRespawnEvent event){
        setLastLocation(event.getPlayer());
    }

    private void setLastLocation(final Player player){
        if (player == null || !player.isOnline()){
            return;
        }
        final SakuraPlayer sp = PlayerManager.getPlayer(player);
        sp.getData().setLastLocation(player.getLocation());
    }
}
