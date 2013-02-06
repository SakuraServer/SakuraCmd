/**
 * SakuraCmd - Package: net.syamn.sakuracmd.manager
 * Created: 2013/01/11 0:26:26
 */
package net.syamn.sakuracmd.manager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;

/**
 * Worlds (Worlds.java)
 * @author syam(syamn)
 */
public class Worlds {
    public static String main_world = "new";
    public static String main_nether = "new_nether";
    public static String main_end = "new_the_end";
    
    public static String skylands = "skylands";
    public static String entrance = "entrance";
    public static String creative = "creative";
    
    public static String flaggame = "flaggame";
    
    public static boolean isNormalResource(final String worldName){
        if (!worldName.contains("resource")){
            return false;
        }
        final World world = Bukkit.getWorld(worldName);
        return (world != null && world.getEnvironment().equals(Environment.NORMAL));
    }
    
    public static boolean isResource(final String worldName){
        return worldName.contains("resource");
    }
    
    public static boolean isFlyAllowed(final String worldName){
        return worldName.equals(main_world);
    }
    
    public static List<String> getNormalWorlds(){
        List<String> ret = new ArrayList<String>();
        
        // Add mains
        ret.add(main_world);
        //ret.add(main_nether);
        //ret.add(main_end);
        ret.add(skylands);
        
        // Add resources
        for (final World w : Bukkit.getWorlds()){
            if (isResource(w.getName())){ // isNormalResource
                ret.add(w.getName());
            }
        }
        
        return ret;
    }
}
