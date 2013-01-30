/**
 * SakuraCmd - Package: net.syamn.sakuracmd.storage
 * Created: 2013/01/30 9:39:16
 */
package net.syamn.sakuracmd.storage;

import java.io.File;
import java.util.Map;

import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.sakuracmd.worker.FlymodeWorker;
import net.syamn.utils.LogUtil;
import net.syamn.utils.file.FileStructure;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * ServerData (ServerData.java)
 * @author syam(syamn)
 */
public class ServerData {
    final private static String fileName = "serverData.yml";
    
    private FileConfiguration conf;
    private File pluginDir;
    
    private final SakuraCmd plugin;
    
    /**
     * Constructor
     * @param plugin
     */
    public ServerData(final SakuraCmd plugin){
        this.plugin = plugin;
        this.pluginDir = plugin.getDataFolder();
    }
    
    /**
     * Load and restore configuration
     * @param initialLoad
     * @throws Exception
     */
    public void loadRestore(){
        try{
            FileStructure.createDir(pluginDir);
            
            final File file = new File(pluginDir, fileName);
            if (!file.exists()){
                LogUtil.info(fileName + " is not found! Skipping restore server data!");
                return;
            }
            
            conf = new YamlConfiguration();
            conf.load(file);
        }catch (Exception ex){
            LogUtil.warning("Could not restore server data!");
            ex.printStackTrace();
            return;
        }
        
        // restore data
        restoreData();
    }
    
    private void restoreData(){
        restoreFlymode();
    }
    private void restoreFlymode(){
        final Object obj = conf.get("FlymodeMap");
        if (obj == null){
            return;
        }
        
        MemorySection table = (MemorySection) obj;
        int i = 0;
        for (final String name : table.getKeys(false)){
            final int expire = conf.getInt("FlymodeMap." + name, -1);
            if (expire < 0){
                continue;
            }
            FlymodeWorker.getInstance().addFlymodePlayersMap(name, expire);
            i++;
        }
        FlymodeWorker.getInstance().onPluginEnabled();
        LogUtil.info("Restored " + i + " player(s) flying mode data!");
    }
    
    
    /**
     * Save configuraiton
     * @throws Exception
     */
    public void save(){
        conf = new YamlConfiguration();
        
        buildSaveData();
        
        try{
            FileStructure.createDir(pluginDir);
            conf.save(new File(pluginDir, fileName));
        }catch (Exception ex){
            LogUtil.warning("Could not save server data!");
            ex.printStackTrace();
        }
    }
    private void buildSaveData(){
        buildFlymode();
    }
    private void buildFlymode(){
        final Map<String, Integer> players = FlymodeWorker.getInstance().getFlymodePlayers();
        if (players.size() > 0){
            ConfigurationSection cs = conf.createSection("FlymodeMap");
            int i = 0;
            for (final Map.Entry<String, Integer> entry : players.entrySet()){
                cs.set(entry.getKey(), entry.getValue().intValue());
                i++;
            }
            
            LogUtil.info("Saved " + i + " player(s) flying mode data!");
        }
    }
}
