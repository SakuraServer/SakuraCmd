/**
 * SakuraCmd - Package: net.syamn.sakuracmd.migrator
 * Created: 2013/01/09 14:10:30
 */
package net.syamn.sakuracmd.migrator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.utils.LogUtil;
import net.syamn.utils.StrUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * SakuraServerMigrate (SakuraServerMigrate.java)
 * @author syam(syamn)
 */
public class SakuraServerMigrate  implements IMigrate{
    final private SakuraCmd plugin;
    final private String sender;
    
    private File pluginDir;
    private File oldPluginDir;
    
    public SakuraServerMigrate(final SakuraCmd plugin, final CommandSender sender){
        this.plugin = plugin;
        this.sender = sender.getName();
        init();
    }
    
    @Override
    public void init() {
        LogUtil.info("Starting migrate from SakuraServer plugin by " + sender);
        
        this.pluginDir = plugin.getDataFolder();
        this.oldPluginDir = new File("plugins" + File.separator + "SakuraServer" + File.separator);
        
        importPlayerData();
        importOtherFiles();
        
        LogUtil.info("Finished migrate from SakuraServer plugin!");
    }

    @Override
    public void importPlayerData() {
        LogUtil.info("Starting to merge PlayerData..");
        
        final File fromDir = new File(oldPluginDir, "userData");
        final File toDir = new File(pluginDir, "userData");
        
        // target files
        File[] files = fromDir.listFiles();
        if (files == null || files.length == 0){
            LogUtil.warning("Merge from players not found!");
            return;
        }
        
        // make dir if not exist
        if (!toDir.exists()){
            toDir.mkdirs();
        }
        
        int count = 0;
        File toFile;
        BufferedReader br = null;
        PrintWriter pw = null;
        
        List<String> failed = new ArrayList<String>();
        List<String> names = new ArrayList<String>();
        
        YamlConfiguration conf = null;
        int mfmfCount = 0;
        try{
            for (final File file : files){
                if (file.isDirectory()){
                    LogUtil.warning("Skipping directory: " + file.getPath());
                    continue; // skip directory
                }
                
                toFile = new File(toDir, file.getName());
                if (!toFile.exists()){
                    LogUtil.warning("Merging target file not found: " + file.getName());
                    failed.add(file.getName());
                    continue; // skip !exists
                }
                
                // Read YAML
                try {
                    conf = new YamlConfiguration();
                    conf.load(file);
                    
                    // read mfmfCount
                    mfmfCount = conf.getInt("mofCount", 0);
                }catch(Exception ex){
                    LogUtil.warning("Reading Failed(" + file.getName() + "): " + ex.getMessage());
                    failed.add(file.getName());
                    continue;
                }
                
                // Update YAML
                try{
                    conf = new YamlConfiguration();
                    conf.load(toFile);
                    
                    // put mfmfCount
                    ConfigurationSection section = conf.getConfigurationSection("infos");
                    section.set("mofCount", mfmfCount);
                    
                    conf.save(toFile);
                }catch(Exception ex){
                    LogUtil.warning("Update Failed(" + toFile.getName() + "): " + ex.getMessage());
                    failed.add(file.getName());
                    continue;
                }
                
                count++;
                names.add(file.getName());
                if (count % 10 == 0){
                    LogUtil.info("Merged.. " + StrUtil.join(names, " "));
                    names.clear();
                }
            }
            LogUtil.info("Merged.. " + StrUtil.join(names, " "));
        }finally{
            if (br != null){
                try { br.close(); } catch (Exception ignore) {}
            }
            if (pw != null){
                try { pw.close(); } catch (Exception ignore) {}
            }
        }
        if (failed.size() > 0){
            LogUtil.info("Merged failed files (" + failed.size() + "): " + StrUtil.join(failed, " "));
        }
        LogUtil.info("Merge complete! Total " + count +" player data file(s)!");
    }

    @Override
    public void importOtherFiles() {
        return; // nothing to do
    }
}