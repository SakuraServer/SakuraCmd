/**
 * SakuraCmd - Package: net.syamn.sakuracmd
 * Created: 2013/01/04 13:02:22
 */
package net.syamn.sakuracmd;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.syamn.sakuracmd.permission.PermissionManager;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.sakuracmd.utils.plugin.DynmapHandler;
import net.syamn.sakuracmd.worker.AFKWorker;
import net.syamn.sakuracmd.worker.InvisibleWorker;
import net.syamn.utils.LogUtil;

/**
 * SCHelper (SCHelper.java)
 * @author syam(syamn)
 */
public class SCHelper {
    private static long mainThreadID;
    private static long pluginStarted;
    private static SCHelper instance = new SCHelper();
    
    public static SCHelper getInstance(){
        return instance;
    }
    public static void dispose(){
        instance = null;
    }
    
    private SakuraCmd plugin;
    private ConfigurationManager config;
    
    /**
     * プラスグインの初期化時と有効化時に呼ばれる
     */
    private void init(){
        // loadconfig
        try {
            config.loadConfig(true);
        } catch (Exception ex) {
            LogUtil.warning(SakuraCmd.logPrefix + "an error occured while trying to load the config file.");
            ex.printStackTrace();
        }
        
        // worker
        AFKWorker.getInstance();
        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(
                this.plugin, AFKWorker.getInstance().getAfkChecker(), 0, config.getAfkCheckIntervalInSec() * 20);
        InvisibleWorker.createInstance();
       
        PermissionManager.setupPermissions(plugin); // init permission
        
        // dynmap
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
            @Override
            public void run(){
                DynmapHandler.createInstance();
            }
        }, 20L);
        
        // Mapping already online players
        PlayerManager.clearAll();
        for (final Player player : Bukkit.getOnlinePlayers()){
            PlayerManager.addPlayer(player);
        }
    }
    
    public void setMainPlugin(final SakuraCmd plugin){
        mainThreadID = Thread.currentThread().getId();
        this.plugin = plugin;
        this.config = new ConfigurationManager(plugin);
        
        // database
        //database = new Database(this);
        //database.createStructure();
        
        init();
    }
    
    /**
     * プラグインをリロードする
     */
    public synchronized void reload(){
        AFKWorker.dispose();
        InvisibleWorker.dispose();
        
        if (DynmapHandler.getInstance() != null){
            DynmapHandler.getInstance().deactivate();
        }
        DynmapHandler.dispose();
        
        System.gc();
        init();
    }
    
    /**
     * 設定マネージャを返す
     *
     * @return ConfigurationManager
     */
    public ConfigurationManager getConfig() {
        return config;
    }
}
