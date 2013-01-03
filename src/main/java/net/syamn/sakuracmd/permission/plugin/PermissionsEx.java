/**
 * SakuraCmd - Package: net.syamn.sakuracmd.permission.plugin
 * Created: 2013/01/04 0:28:18
 */
package net.syamn.sakuracmd.permission.plugin;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;

import net.syamn.sakuracmd.permission.IPermissionPlugin;
import net.syamn.sakuracmd.permission.Perms;

/**
 * PermissionsEx (PermissionsEx.java)
 * @author syam(syamn)
 */
public class PermissionsEx implements IPermissionPlugin{
    private final PermissionManager pex;
    
    public PermissionsEx(final PermissionManager pex){
        this.pex = pex;
    }

    @Override
    public String getPrefix(Player player) {
        final PermissionUser user = pex.getUser(player);
        String prefix = "";
        
        if (user != null){
            prefix = user.getPrefix();
            if (prefix == null) prefix = "";
        }else{
            for (final PermissionGroup group : pex.getUser(player).getGroups()){
                if ((prefix = group.getPrefix()) != null && !prefix.isEmpty()){
                    break;
                }
            }
        }
        return prefix;
    }

    @Override
    public String getSuffix(Player player) {
        final PermissionUser user = pex.getUser(player);
        String suffix = "";
        
        if (user != null){
            suffix = user.getSuffix();
            if (suffix == null) suffix = "";
        }else{
            for (final PermissionGroup group : pex.getUser(player).getGroups()){
                if ((suffix = group.getSuffix()) != null && !suffix.isEmpty()){
                    break;
                }
            }
        }
        return suffix;
    }

    @Override
    public Set<Player> getPlayers(String groupName) {
        PermissionUser[] users = pex.getUsers(groupName);
        if (users == null) return null;
        
        final Set<Player> players = new HashSet<Player>();
        Player player = null;
        for (final PermissionUser user : users){
            player = Bukkit.getPlayerExact(user.getName());
            if (player == null){
                continue;
            }
            players.add(player);
        }
        return players;
    }

    @Override
    public String getGroupName(Player player) {
        int top = Integer.MAX_VALUE;
        PermissionGroup topGroup = null;
        for (final PermissionGroup group : pex.getUser(player).getGroups()){
            final int rank = group.getRank();
            if (rank > top){
                top = rank;
                topGroup = group;
            }
        }
        if (topGroup == null){
            return null;
        }else{
            return topGroup.getName();
        }
    }

    @Override
    public boolean isInGroup(Player player, String groupName) {
        for (final PermissionGroup group : pex.getUser(player).getGroups()){
            if (groupName.equalsIgnoreCase(group.getName())){
                return true;
            }
        }
        return false;
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
        
        return pex.has((Player)sender, node);
    }
}
