/**
 * SakuraCmd - Package: net.syamn.sakuracmd.listener.feature
 * Created: 2013/01/31 18:45:49
 */
package net.syamn.sakuracmd.listener.feature;

import net.syamn.sakuracmd.manager.Worlds;
import net.syamn.utils.LogUtil;
import net.syamn.utils.StrUtil;
import net.syamn.utils.Util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.PortalType;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.PortalCreateEvent.CreateReason;

/**
 * PortalEventListener (PortalEventListener.java)
 * @author syam(syamn)
 */
public class PortalEventListener implements Listener{
    // *** Entity teleport with portal events - Start ***
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPortal(final PlayerPortalEvent event) {
        final Location toLoc = getToLocation(event.getPlayer(), event.getFrom());
        if (toLoc == null){
            event.setCancelled(true);
            return;
        }
        event.useTravelAgent(false);
        event.setTo(toLoc);
        //Util.message(event.getPlayer(), StrUtil.getLocationString(toLoc));
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityPortal(final EntityPortalEvent event) {
        final Location toLoc = getToLocation(event.getEntity(), event.getFrom());
        if (toLoc == null){
            event.setCancelled(true);
            return;
        }
        event.useTravelAgent(false);
        event.setTo(toLoc);
    }
    
    private Location getToLocation(Entity ent, Location from){
        if (ent == null || from == null){
            return null;
        }
        
        final Environment fromEnv = from.getWorld().getEnvironment();
        if (Environment.THE_END.equals(fromEnv)) {
            return null;
        }
        
        final int x = from.getBlockX(); // x, z = fix value
        final int z = from.getBlockZ();
        
        World world = null;
        if (Environment.NORMAL.equals(fromEnv)) {
            world = Bukkit.getWorld(Worlds.main_nether); // goto nether
        } else if (Environment.NETHER.equals(fromEnv)) {
            world = Bukkit.getWorld(Worlds.main_world); // goto main
        }
        
        if (world == null){
            return null;
        }
        
        final int y = getFirstPortalY(world, x, z);
        if (y < 0) {
            if (ent instanceof Player){
                Util.message((Player)ent, "&c" + world.getName() + "のxz座標(" + x + "," + z + ")にポータルが見つかりません！");
            }
            return null;
        }
        
        final Location ploc = ent.getLocation().clone();
        ploc.setWorld(world);
        ploc.setX(x + 0.5D);
        ploc.setY(y);
        ploc.setZ(z + 0.5D);
        
        return ploc;
    }
    private int getFirstPortalY(final World w, final int x, final int z) {
        if (!w.isChunkLoaded(x, z) && w.loadChunk(x, z, false)) {
            return -1;
        }
        for (int y = 2; y < 256; y++) { // don't check y=0,1
            if (w.getBlockAt(x, y, z).getTypeId() == 90) {
                return y;
            }
        }
        return -1;
    }
    // *** Entity teleport with portal events - End ***
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPortalCreate(final PortalCreateEvent event) {
        if (CreateReason.OBC_DESTINATION.equals(event.getReason())) {
            event.setCancelled(true);
            LogUtil.info("Portal auto-create event cancelled on World " + event.getWorld().getName());
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityCreatePortal(final EntityCreatePortalEvent event) {
        if (event.getPortalType() == PortalType.NETHER && event.getEntityType() == EntityType.PLAYER) {
            final Player player = (Player) event.getEntity();
            // メインワールド以外ならキャンセル
            if (!player.getWorld().getName().equals(Worlds.main_world)) {
                event.setCancelled(true);
                String loc = player.getWorld().getName() + ":" + player.getLocation().getBlockX() + "," + player.getLocation().getBlockY() + "," + player.getLocation().getBlockZ();
                //Actions.executeCommandOnConsole("kick " + player.getName() + " ネザーポータル設置違反 at " + loc);
            }
        }
    }
}
