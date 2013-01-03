/**
 * SakuraCmd - Package: net.syamn.sakuracmd.permission
 * Created: 2013/01/04 0:06:06
 */
package net.syamn.sakuracmd.permission;

import java.util.Set;

import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.sakuracmd.permission.plugin.PermissionsEx;
import net.syamn.utils.LogUtil;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;

/**
 * PermissionManager (PermissionManager.java)
 * @author syam(syamn)
 */
public class PermissionManager {
    private static PermissionManager instance = null;
    private static PermissionType permType;
    private static IPermissionPlugin permPlugin;
    
    /**
     * Get Instance
     * @return
     */
    public static PermissionManager getInstance(){
        if (instance == null){
            synchronized (PermissionManager.class) {
                if (instance == null){
                    instance = new PermissionManager();
                }
            }
        }
        return instance;
    }
    
    public static void setupPermissions(final SakuraCmd plugin){
        final String selected = plugin.getConfigs().getPermissionCtrl();
        boolean found = true;
        
        if ("permissionsex".equalsIgnoreCase(selected) || "pex".equalsIgnoreCase(selected)){
            Plugin testPex = plugin.getServer().getPluginManager().getPlugin("PermissionsEx");
            if (testPex == null) testPex = plugin.getServer().getPluginManager().getPlugin("permissionsex");
            if (testPex == null){
                LogUtil.warning("Selected PermissionsEx for permission control, but NOT found this plugin!");
                return;
            }
            
            try{
                setPEX(ru.tehkode.permissions.bukkit.PermissionsEx.getPermissionManager());
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }else{
            found = false;
        }
        
        if (!found){
            LogUtil.warning("Valid permissions name not selected!");
        }else{
            LogUtil.info("Using " + permType.name() + " for permission control.");
        }
    }
    
    public PermissionType getPermType(){
        return permType;
    }
    
    public static boolean setPEX(final ru.tehkode.permissions.PermissionManager pex){
        if (!PermissionType.PEX.equals(permType)){
            permType = PermissionType.PEX;
            permPlugin = new PermissionsEx(pex);
            LogUtil.info("Successfully linked with PermissionsEx!");
            return true;
        }else{
            return false;
        }
    }
    
    
    /* ********** */
    public static String getPrefix(final Player player){
        return permPlugin.getPrefix(player);
    }
    public static String getSuffix(final Player player){
        return permPlugin.getSuffix(player);
    }
    
    public Set<Player> getPlayers(final String groupName){
        return permPlugin.getPlayers(groupName);
    }
    public static String getGroupName(final Player player){
        return permPlugin.getGroupName(player);
    }
    public static boolean isInGroup(final Player player, final String groupName){
        return permPlugin.isInGroup(player, groupName);
    }
    
    public static boolean hasPerm(final Permissible sender, final Perms perm){
        return permPlugin.hasPerm(sender, perm);
    }
    public static boolean hasPerm(final Permissible sender, final String node){
        return permPlugin.hasPerm(sender, node);
    }
}
