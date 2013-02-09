/**
 * SakuraCmd - Package: net.syamn.sakuracmd.listener.feature
 * Created: 2013/02/09 13:30:05
 */
package net.syamn.sakuracmd.listener.feature;

import java.util.HashMap;

import net.syamn.sakuracmd.worker.EndResetWorker;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;

/**
 * EndResetListener (EndResetListener.java)
 * @author syam(syamn)
 */
public class EndResetListener implements Listener{
    private static EndResetListener instance = null;

    public EndResetListener(){
        instance = this;
        EndResetWorker worker = EndResetWorker.getInstance();
        if (worker == null){
            throw new IllegalStateException("EndResetWorker must not be null!");
        }
        worker.callWorldLoad();
    }
    public static EndResetListener getInstance(){
        return instance;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(final EntityDeathEvent event) {
        if (!(event.getEntity() instanceof EnderDragon)) return;
        final Entity entity = event.getEntity();

        final World world = entity.getWorld();
        if (world.getEnvironment() != Environment.THE_END) return;

        final String wname = world.getName();
        EndResetWorker worker = EndResetWorker.getInstance();
        if (worker == null) return;

        if (worker.getWorldDataMap().containsKey(wname)){
            worker.getWorldDataMap().get(wname).updateLastReset();
            worker.updateSaveFlag();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChunkLoad(final ChunkLoadEvent event) {
        if (event.getWorld().getEnvironment() != Environment.THE_END) return;

        EndResetWorker worker = EndResetWorker.getInstance();
        if (worker == null) return;

        final World world = event.getWorld();
        final String wname = world.getName();
        HashMap<String, Long> worldMap;

        if (worker.getResetChunkMap().containsKey(wname)){
            worldMap = worker.getResetChunks(wname);
        } else {
            worldMap = new HashMap<String, Long>();
            worker.putResetChunks(wname, worldMap);
        }

        final Chunk chunk = event.getChunk();
        final int x = chunk.getX();
        final int z = chunk.getZ();
        final String hash = x + "/" + z;

        long cv = worker.getCvs(wname);

        if (worldMap.containsKey(hash)) {
            if (worldMap.get(hash) != cv) {
                for (Entity e : chunk.getEntities()){
                    e.remove();
                }

                world.regenerateChunk(x, z);
                worldMap.put(hash, cv);

                worker.updateSaveFlag();
            }
        } else{
            worldMap.put(hash, cv);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWorldLoad(final WorldLoadEvent event) {
        World world = event.getWorld();
        if (world.getEnvironment() != Environment.THE_END) return;

        EndResetWorker worker = EndResetWorker.getInstance();
        if (worker == null) return;

        String worldName = world.getName();
        if (!worker.getCvsMap().containsKey(worldName)) {
            worker.putCvs(worldName, Long.MIN_VALUE);
        }
    }
}
