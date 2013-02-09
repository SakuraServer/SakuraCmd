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
    }
    public static EndResetListener getInstance(){
        return instance;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(final EntityDeathEvent event) {
        Entity entity = event.getEntity();
        
        if (!(entity instanceof EnderDragon)) return;
        
        World world = entity.getWorld();
        if (world.getEnvironment() != Environment.THE_END) return;
        
        EndResetWorker worker = EndResetWorker.getInstance();
        if (worker == null) return;
        
        String worldName = world.getName();
        if (worker.dontHandle.contains(worldName)) return;
        
        worker.reg.add(worldName);
        worker.save = true;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChunkLoad(final ChunkLoadEvent event) {
        if (event.getWorld().getEnvironment() != Environment.THE_END) return;
        
        EndResetWorker worker = EndResetWorker.getInstance();
        if (worker == null) return;
        
        World world = event.getWorld();
        String worldName = world.getName();
        HashMap<String, Long> worldMap;
        
        if (worker.resetchunks.containsKey(worldName)){
            worldMap = worker.resetchunks.get(worldName);
        } else {
            worldMap = new HashMap<String, Long>();
            worker.resetchunks.put(worldName, worldMap);
        }

        Chunk chunk = event.getChunk();
        int x = chunk.getX();
        int z = chunk.getZ();
        String hash = x + "/" + z;
        long cv = worker.cvs.get(worldName);

        if (worldMap.containsKey(hash)) {
            if (worldMap.get(hash) != cv) {
                for (Entity e : chunk.getEntities()){
                    e.remove();
                }
                
                world.regenerateChunk(x, z);
                worldMap.put(hash, cv);
                worker.save = true;
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
        if (!worker.cvs.containsKey(worldName)) {
            worker.cvs.put(worldName, Long.MIN_VALUE);
            worker.save = true;
        }
    }
}
