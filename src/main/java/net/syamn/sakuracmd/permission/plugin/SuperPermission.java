/**
 * SakuraCmd - Package: net.syamn.sakuracmd.permission.plugin
 * Created: 2013/01/04 3:15:06
 */
package net.syamn.sakuracmd.permission.plugin;

import java.util.Set;

import net.syamn.sakuracmd.exception.NotSupportedException;
import net.syamn.sakuracmd.permission.IPermissionPlugin;
import net.syamn.sakuracmd.permission.Perms;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

/**
 * SuperPermission (SuperPermission.java)
 * @author syam(syamn)
 */
public class SuperPermission implements IPermissionPlugin{
    public SuperPermission(){
        
    }

    @Override
    public String getPrefix(Player player) {
        return "";
    }

    @Override
    public String getSuffix(Player player) {
        return "";
    }

    @Override
    public Set<Player> getPlayers(String groupName) throws NotSupportedException {
        throw new NotSupportedException("To use this functionality you need a Permission Plugin");
    }

    @Override
    public String getGroupName(Player player) throws NotSupportedException {
        throw new NotSupportedException("To use this functionality you need a Permission Plugin");
    }

    @Override
    public boolean isInGroup(Player player, String groupName) throws NotSupportedException {
        throw new NotSupportedException("To use this functionality you need a Permission Plugin");
    }

    @Override
    public boolean hasPerm(Permissible sender, Perms perm) {
        return this.hasPerm(sender, perm.getNode());
    }

    @Override
    public boolean hasPerm(Permissible sender, String node) {
        if(!(sender instanceof Player)){
            return true;
        }
        
        return sender.hasPermission(node);
    }
}
