/**
 * SakuraCmd - Package: net.syamn.sakuracmd.migrator
 * Created: 2013/01/08 19:55:10
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

/**
 * AdminCmdMigrate (AdminCmdMigrate.java)
 * @author syam(syamn)
 */
public class AdminCmdMigrate implements IMigrate{
    final private SakuraCmd plugin;
    final private String sender;
    
    private File pluginDir;
    private File adminCmdDir;
    
    public AdminCmdMigrate(final SakuraCmd plugin, final CommandSender sender){
        this.plugin = plugin;
        this.sender = sender.getName();
        init();
    }
    
    @Override
    public void init() {
        LogUtil.info("Starting migrate from AdminCmd plugin by " + sender);
        
        this.pluginDir = plugin.getDataFolder();
        this.adminCmdDir = new File("plugins" + File.separator + "AdminCmd" + File.separator);
        
        importPlayerData();
        importOtherFiles();
        
        LogUtil.info("Finished migrate from AdminCmd plugin!");
    }

    @Override
    public void importPlayerData() {
        LogUtil.info("Starting to migrate PlayerData..");
        
        final File fromDir = new File(adminCmdDir, "userData");
        final File toDir = new File(pluginDir, "userData");
        
        // target files
        File[] files = fromDir.listFiles();
        if (files == null || files.length == 0){
            LogUtil.warning("Migrate target players not found!");
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
        
        List<String> names = new ArrayList<String>();
        try{
            for (final File file : files){
                if (file.isDirectory()){
                    LogUtil.warning("Skipping directory: " + file.getPath());
                    continue; // skip directory
                }
                
                toFile = new File(toDir, file.getName());
                if (toFile.exists()){
                    LogUtil.warning("Skipping exist file: " + file.getPath());
                    continue; // skip already exists
                }
                
                try{
                    br = new BufferedReader(new FileReader(file));
                    pw = new PrintWriter(new BufferedWriter(new FileWriter(toFile)));
                    
                    String line;
                    while ((line = br.readLine()) != null){
                        if (line.indexOf("!!") != -1){
                            continue;
                        }
                        pw.println(line);
                    }
                    
                    pw.close();
                    br.close();
                }catch (Exception ex){
                    LogUtil.warning("Convert Failed(" + file.getName() + "): " + ex.getMessage());
                    continue; // skip directory
                }
                
                count++;
                names.add(file.getName());
                if (count % 10 == 0){
                    LogUtil.info("Converted.. " + StrUtil.join(names, " "));
                    names.clear();
                }
            }
            LogUtil.info("Converted.. " + StrUtil.join(names, " "));
        }finally{
            if (br != null){
                try { br.close(); } catch (Exception ignore) {}
            }
            if (pw != null){
                try { pw.close(); } catch (Exception ignore) {}
            }
        }
        
        LogUtil.info("Migrated " + count + " player data file(s)!");
    }

    @Override
    public void importOtherFiles() {
        return; // nothing to do
    }
}
