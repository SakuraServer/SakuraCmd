/**
 * SakuraCmd - Package: net.syamn.sakuracmd
 * Created: 2013/01/04 13:02:22
 */
package net.syamn.sakuracmd;

import net.syamn.sakuracmd.feature.GeoIP;
import net.syamn.sakuracmd.permission.PermissionManager;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.sakuracmd.storage.ConfigurationManager;
import net.syamn.sakuracmd.storage.Database;
import net.syamn.sakuracmd.storage.I18n;
import net.syamn.sakuracmd.utils.plugin.DynmapHandler;
import net.syamn.sakuracmd.utils.plugin.SakuraCmdUtil;
import net.syamn.sakuracmd.worker.AFKWorker;
import net.syamn.sakuracmd.worker.InvisibleWorker;
import net.syamn.utils.LogUtil;
import net.syamn.utils.queue.ConfirmQueue;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
    private Database database;
    private int afkTaskID = -1;
    private boolean isEnableEcon = false;
    
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
        afkTaskID = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(
                this.plugin, AFKWorker.getInstance().getAfkChecker(), 0, config.getAfkCheckIntervalInSec() * 20).getTaskId();
        InvisibleWorker.createInstance();
       
        PermissionManager.setupPermissions(plugin); // init permission
        
        // dynmap
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
            @Override
            public void run(){
                DynmapHandler.createInstance();
            }
        }, 20L);
        
        // coloring tab list
        for (final Player p  : plugin.getServer().getOnlinePlayers()){
            SakuraCmdUtil.changeTabColor(p);
        }
        
        // queue, create instance
        ConfirmQueue.getInstance();
        
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
        
        this.database = new Database(plugin);
        database.createStructure();
        
        // database
        //database = new Database(this);
        //database.createStructure();
        
        init();
    }
    
    public void disableAll(){
        if (afkTaskID != -1){
            plugin.getServer().getScheduler().cancelTask(afkTaskID);
            afkTaskID = -1;
        }
        AFKWorker.dispose();
        InvisibleWorker.dispose();
        
        if (DynmapHandler.getInstance() != null){
            DynmapHandler.getInstance().deactivate();
        }
        DynmapHandler.dispose();
        ConfirmQueue.dispose();
        GeoIP.dispose();
    }
    
    /**
     * プラグインをリロードする
     */
    public synchronized void reload(){
        disableAll();
        System.gc();
        init();
        
        try {
            I18n.setCurrentLanguage(config.getLanguage());
        } catch (Exception ex) {
            LogUtil.warning("An error occured while trying to load the language file!");
            ex.printStackTrace();
        }
    }
    
    // Economy getter/setter
    public void setEnableEcon(final boolean enable){
        this.isEnableEcon = enable;
    }
    public boolean isEnableEcon(){
        return this.isEnableEcon;
    }
    
    // Database getter
    public Database getDB(){
        return this.database;
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
