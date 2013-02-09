/**
 * SakuraCmd - Package: net.syamn.sakuracmd.listener
 * Created: 2013/01/12 19:19:24
 */
package net.syamn.sakuracmd.listener;

import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.sakuracmd.manager.Worlds;
import net.syamn.utils.Util;

import org.bukkit.Chunk;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

/**
 * EndListener (EndListener.java)
 * @author syam(syamn)
 */
public class EndListener implements Listener{
    private SakuraCmd plugin;
    public EndListener (final SakuraCmd plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDeath(final EntityDeathEvent event) {
        if (event.getEntity().getType() == EntityType.ENDER_DRAGON && event.getEntity().getKiller() != null) {
            int normal_end_DragonExp = 10000;
            int hard_end_DragonExp = 30000;

            if (event.getEntity().getWorld().getName().equals(Worlds.main_end)) {
                event.setDroppedExp(normal_end_DragonExp);
                Util.broadcastMessage("&6" + event.getEntity().getKiller().getName() + " &bさんがドラゴンを倒しました！", true);
                Util.broadcastMessage("&aエンドワールドは6時間後に自動再生成されます！", false);
                //Util.worldcastMessage(event.getEntity().getWorld(), "&aメインワールドに戻るには&f /spawn &aコマンドを使ってください！");
            } else if (event.getEntity().getWorld().getName().equals("hard_end")) {
                event.setDroppedExp(hard_end_DragonExp);
                Util.broadcastMessage("&6" + event.getEntity().getKiller().getName() + " &bさんがハードエンドでドラゴンを倒しました！", true);
                //Util.worldcastMessage(event.getEntity().getWorld(), "&aメインワールドに戻るには&f /spawn &aコマンドを使ってください！", false);
            }

            //Actions.log("End.log", "Player " + event.getEntity().getKiller().getName() + " killed the EnderDragon at world " + event.getEntity().getWorld().getName());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityCreatePortal(final EntityCreatePortalEvent event) {
        if (!event.getEntityType().equals(EntityType.ENDER_DRAGON)) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChunkUnload(final ChunkUnloadEvent event) {
        if (event.getWorld().getEnvironment() != Environment.THE_END) {
            return;
        }

        final Chunk chunk = event.getChunk();
        for (final Entity entity : chunk.getEntities()) {
            if (entity.getType() == EntityType.ENDER_DRAGON || entity.getType() == EntityType.COMPLEX_PART) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
