/**
 * SakuraCmd - Package: net.syamn.sakuracmd.listener
 * Created: 2012/12/31 3:18:20
 */
package net.syamn.sakuracmd.listener;

import static net.syamn.sakuracmd.storage.I18n._;

import net.syamn.sakuracmd.SCHelper;
import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.sakuracmd.feature.GeoIP;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.sakuracmd.player.PlayerData;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.sakuracmd.player.Power;
import net.syamn.sakuracmd.player.SakuraPlayer;
import net.syamn.sakuracmd.storage.I18n;
import net.syamn.sakuracmd.worker.AFKWorker;
import net.syamn.sakuracmd.worker.InvisibleWorker;
import net.syamn.utils.Util;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * PlayerListener (PlayerListener.java)
 * @author syam(syamn)
 */
public class PlayerListener implements Listener{
    private SakuraCmd plugin;
    public PlayerListener (final SakuraCmd plugin){
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent event){
        final Player player = event.getPlayer();
        AFKWorker.getInstance().updatePlayer(player);
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent event){
        AFKWorker.getInstance().updatePlayer(event.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDamage(final EntityDamageEvent event){
        if (!(event.getEntity() instanceof Player)){
            return;
        }
        
        final Player player = (Player) event.getEntity();
        final SakuraPlayer sp = PlayerManager.getPlayer(player);
        if (sp.hasPower(Power.GODMODE)){
            event.setDamage(0);
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(final PlayerJoinEvent event){
        final Player player = event.getPlayer();
        final SakuraPlayer sp = PlayerManager.getPlayer(player);
        
        InvisibleWorker.getInstance().sendInvisibleOnJoin(player);
        
        String msg = _(((player.hasPlayedBefore()) ? "joinMessage" : "firstJoinMessage"), I18n.PLAYER, sp.getName());
        if (msg.length() < 1) msg = null;
        event.setJoinMessage(msg);
        
        // Auto vanish player if player has Invisible power
        if (sp.hasPower(Power.INVISIBLE)){
            InvisibleWorker.getInstance().vanish(player, true);
            Util.message(player, "&bあなたは透明モードが有効になっています！");
            event.setJoinMessage(null);
        }
        
        // Use GeoIP if enabled
        if (SCHelper.getInstance().getConfig().getUseGeoIP()){
            GeoIP.getInstance().onPlayerJoin(player);
        }
        
        // Run async
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run(){
                AFKWorker.getInstance().updateTimeStamp(player);
                
                PlayerData data = sp.getData();
                data.updateLastConnection();
                data.setLastIP(player.getAddress().getAddress().getHostAddress());
            }
        });
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(final PlayerLoginEvent event){
        if (!event.getResult().equals(Result.ALLOWED)){
            return;
        }
        
        // Check Lockdown isEnabled
        if (plugin.getServerManager().isLockdown() && !Perms. LOCKDOWN_BYPASS.has(event.getPlayer())){
            event.disallow(Result.KICK_OTHER, _("serverLocked"));
        }
        
        // Add to players list
        PlayerManager.addPlayer(event.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(final PlayerQuitEvent event){
        final Player player = event.getPlayer();
        final SakuraPlayer sp = PlayerManager.getPlayer(player);
        
        String msg = _("quitMessage", I18n.PLAYER, sp.getName());
        if (msg.length() < 1) msg = null;
        event.setQuitMessage(msg);
        
        if (InvisibleWorker.getInstance().isInvisible(player)){
            InvisibleWorker.getInstance().onPlayerQuit(player);
            event.setQuitMessage(null); // hide message of vanished player
        }
        
        PlayerManager.getPlayer(player).getData().updateLastDisconnect();
    }
}
