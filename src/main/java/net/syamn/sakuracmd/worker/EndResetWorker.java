/**
 * SakuraCmd - Package: net.syamn.sakuracmd.worker
 * Created: 2013/02/09 5:24:32
 */
package net.syamn.sakuracmd.worker;

import static org.mockito.Matchers.contains;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.scheduler.BukkitScheduler;

import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.sakuracmd.listener.feature.EndResetListener;
import net.syamn.sakuracmd.serial.endreset.EndResetWorld;
import net.syamn.utils.LogUtil;
import net.syamn.utils.Util;

/**
 * EndResetWorker (EndResetWorker.java)
 * @author syam(syamn)
 */
public class EndResetWorker{
    private static EndResetWorker instance = null;
    public static EndResetWorker getInstance(){
        return instance;
    }
    public static void dispose(){
        instance.onDispose();
        instance = null;
    }
    public static void createInstance(final SakuraCmd plugin){
        instance = new EndResetWorker();
        instance.plugin  = plugin;
        instance.init();
    }
    
    //--
    private SakuraCmd plugin;
    private File endResetData = new File(plugin.getDataFolder(), "endResetData.sav");
    private boolean enabled = false;
    private List<Integer> tasks = new ArrayList<Integer>();
    
    // data start
    public final HashMap<String, HashMap<String, Long>> resetchunks = new HashMap<String, HashMap<String, Long>>();
    public final HashMap<String, Long> cvs = new HashMap<String, Long>();

    public final HashSet<String> reg = new HashSet<String>();

    public final HashMap<String, Integer> pids = new HashMap<String, Integer>();
    public long inactiveMin = 1200;
    public boolean save = false;
    private final AtomicBoolean saveLock = new AtomicBoolean(false);

    public final HashSet<String> dontHandle = new HashSet<String>();
    public final HashMap<String, EndResetWorld> forceReset = new HashMap<String, EndResetWorld>();
    public final HashMap<String, Short> dragonAmount = new HashMap<String, Short>();

    public final HashMap<String, RegenThread> threads = new HashMap<String, RegenThread>();
    public final HashMap<String, Long> suspendedTasks = new HashMap<String, Long>();
    // data end
    
    private void init(){
        if (!endResetData.exists()){
            // file not exists, check directory
            plugin.getDataFolder().mkdir();
        }else{
            // file exists, load data
            ObjectInputStream in = null;
            try{
                in = new ObjectInputStream(new FileInputStream(endResetData));;
                
                int fileVersion;
                Object[] sa = null;
                try {
                    Object o = in.readObject();
                    if (o == null || !(o instanceof Object[])) {
                        LogUtil.warning("Could not read EndReset save data!");
                        return;
                    }
                    sa = (Object[]) o;
                    fileVersion = (Integer) sa[0];
                } catch (OptionalDataException ex) {
                    fileVersion = in.readInt();
                }
                
                // load data
                RegenThread regenThread;
                World world;
                long tr;
                
                for (Entry<String, HashMap<String, Long>> e : ((HashMap<String, HashMap<String, Long>>) in.readObject()).entrySet()){
                    resetchunks.put(e.getKey(), e.getValue());
                }
                for (Entry<String, Long> e : ((HashMap<String, Long>) in.readObject()).entrySet()){
                    cvs.put(e.getKey(), e.getValue());
                }
                for (String regen : (HashSet<String>) in.readObject()){
                    reg.add(regen);
                }
                
                inactiveMin = in.readLong();
                
                for (String dh : (HashSet<String>) in.readObject()){
                    dontHandle.add(dh);
                }
                for (Entry<String, EndResetWorld> e : ((HashMap<String, EndResetWorld>) in.readObject()).entrySet()){
                    forceReset.put(e.getKey(), e.getValue());
                }
                for (Entry<String, Short> e : ((HashMap<String, Short>) in.readObject()).entrySet()){
                    dragonAmount.put(e.getKey(), e.getValue());
                }
                for (Entry<String, Long> e : ((HashMap<String, Long>) in.readObject()).entrySet()) {
                    String worldName = e.getKey();
                    reg.add(worldName);
                    world = plugin.getServer().getWorld(worldName);
                    if (world != null) {
                        tr = e.getValue();
                        regenThread = new RegenThread(worldName, world.getFullTime() + tr);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, regenThread, tr);
                    }
                }
            }
            catch(Exception ex){
                LogUtil.warning("Could not read EndReset save data!");
                ex.printStackTrace();
            }
            finally{
                if (in != null){ try{ in.close(); }catch(Exception ignore){} }
            }
            
            BukkitScheduler scheduler = Bukkit.getScheduler();
            tasks.add(scheduler.runTaskTimer(plugin, new SaveThread(), 36000L, 36000L).getTaskId());
            tasks.add(scheduler.runTaskTimer(plugin, new ForceThread(), 20L, 72000L).getTaskId());
            tasks.add(scheduler.runTaskTimer(plugin, new TickThread(), 1L, 1L).getTaskId());
        }
        
        for (World world : Bukkit.getServer().getWorlds()){
            if (world.getEnvironment() != Environment.THE_END) continue;
            EndResetListener.getInstance().onWorldLoad(new WorldLoadEvent(world));
        }
    }
    
    private void onDispose(){
        for (int taskID : tasks){
            Bukkit.getScheduler().cancelTask(taskID);
        }
        tasks.clear();
        
        if (save){
            new SaveThread().run();
        }
    }
    
    private void regen(World world){
        String worldName = world.getName();
        
        long cv = cvs.get(worldName) + 1;
        if (cv == Long.MAX_VALUE) cv = Long.MIN_VALUE;
        cvs.put(worldName, cv);
        
        EndResetListener listener = EndResetListener.getInstance();
        if (listener == null){
            throw new IllegalStateException("EndResetListener is null!");
        }
        
        for (Chunk chunk : world.getLoadedChunks()){
            listener.onChunkLoad(new ChunkLoadEvent(chunk, false));
        }

        short amount;
        if (dragonAmount.containsKey(worldName)){
            amount = dragonAmount.get(worldName);
        }else{
            amount = 1;
        }
        
        if (amount > 1) {
            amount--;
            Location loc = world.getSpawnLocation();
            loc.setY(world.getMaxHeight() - 1);
            for (short i = 0; i < amount; i++){
                world.spawnEntity(loc, EntityType.ENDER_DRAGON);
            }
        }
        
        save = true;
        Util.broadcastMessage("&c[SakuraServer] &dエンドワールド'&6" + worldName + "&d'はリセットされました！");
    }
    
    private class TickThread implements Runnable{
        @Override
        public void run(){
            int pc;
            int pid;
            BukkitScheduler scheduler = Bukkit.getScheduler();
            for (World world : Bukkit.getServer().getWorlds()) {
                if (world.getEnvironment() != Environment.THE_END) continue;
                
                String worldName = world.getName();
                if (!reg.contains(worldName)) continue;
                pc = world.getPlayers().size();
                if (pc < 1 && !pids.containsKey(worldName)) {
                    long tr;
                    if (!suspendedTasks.containsKey(worldName))
                        tr = inactiveMin;
                    else {
                        tr = suspendedTasks.get(worldName);
                        suspendedTasks.remove(worldName);
                    }
                    RegenThread regenThread = new RegenThread(worldName, world.getFullTime() + tr);
                    pids.put(worldName, scheduler.scheduleSyncDelayedTask(plugin, regenThread, tr));
                    threads.put(worldName, regenThread);
                } else if (pc > 0 && pids.containsKey(worldName)) {
                    pid = pids.get(worldName);
                    scheduler.cancelTask(pid);
                    pids.remove(worldName);
                    suspendedTasks.put(worldName, threads.get(worldName).getRemainingDelay());
                    threads.remove(worldName);
                }
            }
        }
    }
    
    public class RegenThread implements Runnable{
        private final String worldName;
        private final long toRun;

        public RegenThread(String worldName, long toRun) {
            this.worldName = worldName;
            this.toRun = toRun;
        }

        @Override
        public void run() {
            if (!pids.containsKey(worldName)) return;
            World world = Bukkit.getServer().getWorld(worldName);
            if (world != null) regen(world);
            reg.remove(worldName);
            pids.remove(worldName);
            threads.remove(this);
        }

        long getRemainingDelay() {
            World world = Bukkit.getServer().getWorld(worldName);
            if (world == null) return -1;
            return toRun - world.getFullTime();
        }
    }
    
    private class ForceThread implements Runnable {
        @Override
        public void run() {
            if (forceReset.isEmpty()) return;
            
            long now = System.currentTimeMillis() * 1000;
            EndResetWorld vw;
            Server server = Bukkit.getServer();
            for (Entry<String, EndResetWorld> e : forceReset.entrySet()) {
                vw = e.getValue();
                if (vw.lastReset + vw.hours >= now) {
                    regen(server.getWorld(e.getKey()));
                    vw.lastReset = now;
                    save = true;
                }
            }
        }
    }
    
    private class SaveThread implements Runnable {
        @Override
        public void run() {
            if (!save) return;
            save = false;
            
            while (!saveLock.compareAndSet(false, true)){
                continue;
            }
            
            try {
                if (!endResetData.exists()) endResetData.createNewFile();
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(endResetData));

                out.writeInt(0); // file version
                out.writeObject(resetchunks);
                out.writeObject(cvs);
                out.writeObject(reg);
                out.writeLong(inactiveMin);
                out.writeObject(dontHandle);
                out.writeObject(forceReset);
                out.writeObject(dragonAmount);
                out.writeObject(suspendedTasks);

                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new AsyncSaveThread(out));
            } catch (Exception ex) {
                saveLock.set(false);
                LogUtil.warning("Cannot write end reset save data!");
                ex.printStackTrace();
            }
        }
    }
    
    private class AsyncSaveThread implements Runnable {
        private final ObjectOutputStream out;

        private AsyncSaveThread(ObjectOutputStream out) {
            this.out = out;
        }

        @Override
        public void run() {
            try {
                out.flush();
                out.close();
            } catch (Exception ex) {
                LogUtil.warning("Cannot write end reset save data!");
                ex.printStackTrace();
            } finally {
                saveLock.set(false);
            }
        }
    }
    
    
}
