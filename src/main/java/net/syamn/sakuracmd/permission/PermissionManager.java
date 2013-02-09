/**
 * SakuraCmd - Package: net.syamn.sakuracmd.permission
 * Created: 2013/01/04 0:06:06
 */
package net.syamn.sakuracmd.permission;

import java.util.Set;

import net.syamn.sakuracmd.SCHelper;
import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.sakuracmd.exception.NotSupportedException;
import net.syamn.sakuracmd.permission.plugin.PermissionsEx;
import net.syamn.sakuracmd.permission.plugin.SuperPermission;
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
    private static IPermissionPlugin permPlugin = null;

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
        final String selected = SCHelper.getInstance().getConfig().getPermissionCtrl();
        boolean found = true;

        if ("permissionsex".equalsIgnoreCase(selected) || "pex".equalsIgnoreCase(selected)){
            Plugin testPex = plugin.getServer().getPluginManager().getPlugin("PermissionsEx");
            if (testPex == null) testPex = plugin.getServer().getPluginManager().getPlugin("permissionsex");
            if (testPex == null){
                LogUtil.warning("Selected PermissionsEx for permission control, but NOT found this plugin!");
            }

            try{
                setPEX(ru.tehkode.permissions.bukkit.PermissionsEx.getPermissionManager());
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        else if ("superperms".equalsIgnoreCase(selected)){
            setSuperPerms();
        }
        else{
            found = false;
        }

        if (found && permPlugin == null){
            setSuperPerms();
        }

        // デフォルトではSuperPermsを使用
        if (!found){
            LogUtil.warning("Valid permissions name not selected!");
            setSuperPerms();
        }

        LogUtil.info("Using " + permType.name() + " for permission control.");
    }

    public PermissionType getPermType(){
        return permType;
    }

    private static void setPEX(final ru.tehkode.permissions.PermissionManager pex){
        if (!PermissionType.PEX.equals(permType)){
            permType = PermissionType.PEX;
            permPlugin = new PermissionsEx(pex);
            LogUtil.info("Successfully linked with PermissionsEx!");
        }
    }
    private static void setSuperPerms(){
        if (!PermissionType.SUPERPERMS.equals(permType)){
            permType = PermissionType.SUPERPERMS;
            permPlugin = new SuperPermission();
            LogUtil.info("Successfully linked with SuperPerms!");
        }
    }



    /* ********** */
    public static String getPrefix(final Player player){
        return permPlugin.getPrefix(player);
    }
    public static String getSuffix(final Player player){
        return permPlugin.getSuffix(player);
    }

    public Set<Player> getPlayers(final String groupName) throws NotSupportedException{
        return permPlugin.getPlayers(groupName);
    }
    public static String getGroupName(final Player player) throws NotSupportedException{
        return permPlugin.getGroupName(player);
    }
    public static boolean isInGroup(final Player player, final String groupName) throws NotSupportedException{
        return permPlugin.isInGroup(player, groupName);
    }

    public static boolean hasPerm(final Permissible sender, final Perms perm){
        return permPlugin.hasPerm(sender, perm);
    }
    public static boolean hasPerm(final Permissible sender, final String node){
        return permPlugin.hasPerm(sender, node);
    }
}
