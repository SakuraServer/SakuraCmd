/**
 * SakuraCmd - Package: net.syamn.sakuracmd.worker
 * Created: 2013/01/12 18:45:12
 */
package net.syamn.sakuracmd.worker;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.sakuracmd.player.PlayerData;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.sakuracmd.player.Power;
import net.syamn.sakuracmd.player.SakuraPlayer;
import net.syamn.sakuracmd.utils.plugin.SakuraCmdUtil;
import net.syamn.utils.TimeUtil;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * FlymodeWorker (FlymodeWorker.java)
 * @author syam(syamn)
 */
public class FlymodeWorker {
    private final ConcurrentHashMap<String, Integer> flymodePlayers = new ConcurrentHashMap<String, Integer>();
    
    private static FlymodeWorker instance;
    
    public static FlymodeWorker getInstance(){
        if (instance == null){
            synchronized (FlymodeWorker.class) {
                if (instance == null){
                    instance = new FlymodeWorker();
                }
            }
        }
        return instance;
    }
    public static void dispose(){
        instance = null;
    }
    
    public void enableFlymode(final SakuraPlayer sp, final int minute){
        int expired = TimeUtil.getCurrentUnixSec().intValue() + (minute * 60);
        
        sp.addPower(Power.FLYMODE);
        sp.getData().setFlymodeTime(expired);
        SakuraCmdUtil.changeFlyMode(sp.getPlayer(), true);
    }
    public  void disableFlymode(final String name){
        flymodePlayers.remove(name);
        
        PlayerData data = PlayerManager.getDataIfOnline(name);
        if (data == null){
            data = PlayerManager.getData(name);
        }
        
        data.removePower(Power.FLYMODE);
        data.setFlymodeTime(0);
        SakuraCmdUtil.changeFlyMode(Bukkit.getPlayerExact(name), false);
    }
    
    public void restoreFlymode(final SakuraPlayer sp){
        if (sp == null || sp.getPlayer() == null){
            return;
        }
        
        int expired = sp.getData().getFlymodeTime();
        int remain = expired - TimeUtil.getCurrentUnixSec().intValue();
        
        FlymodeWorker worker = new FlymodeWorker(sp.getPlayer(), remain);
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(SakuraCmd.getInstance(), worker, 20L, 20L);
        worker.setThreadId(task.getTaskId());
    }
    
    class FlymodeTask implements Runnable{
        @Override
        public void run() {
            int curr = TimeUtil.getCurrentUnixSec().intValue();
            for (Entry<String, Integer> entry : flymodePlayers.entrySet()){
                int remain = entry.getValue() - curr;
                
                if (remain <= 0){
                    disableFlymode(entry.getKey());
                }else if (remain <= 10){
                    
                }
            }
        }
    }
}
