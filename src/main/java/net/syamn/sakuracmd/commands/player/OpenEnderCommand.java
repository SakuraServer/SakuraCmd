/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.player
 * Created: 2013/02/05 4:27:05
 */
package net.syamn.sakuracmd.commands.player;

import java.util.HashMap;
import java.util.Locale;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.sakuracmd.utils.plugin.SakuraCmdUtil;
import net.syamn.utils.cb.inv.CBEnderChest;
import net.syamn.utils.cb.inv.OfflineInvManager;
import net.syamn.utils.exception.CommandException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * OpenEnderCommand (OpenEnderCommand.java)
 */
public class OpenEnderCommand extends BaseCommand {
    private static HashMap<Player, String> historyMap = new HashMap<Player, String>();

    public OpenEnderCommand(){
        bePlayer = true;
        name = "openender";
        perm = Perms.OPENENDER;
        argLength = 0;
        usage = "[name] <- open others enderchest";
    }

    @Override
    public void execute() throws CommandException{
        // get opened history
        String history = historyMap.get(player);
        if (args.size() == 0 && (history == null || history == "")){
            throw new CommandException("&cプレイヤー名を指定してください！");
        }

        // choose target
        String targetName = null;
        if (args.size() > 0){
            targetName = args.get(0);
        }else{
            targetName = history;
        }

        boolean online = true;
        Player target = Bukkit.getPlayerExact(targetName);

        if (target == null){
            target = Bukkit.getPlayer(targetName);
        }
        if (target == null){
            target = new OfflineInvManager().loadPlayer(targetName);
            online = false;
        }
        if (target == null){
            throw new CommandException("&cプレイヤー " + targetName + " が見つかりません！");
        }

        // save history
        historyMap.put(player, target.getName());

        // create
        CBEnderChest inv = CBEnderChest.chests.get(target.getName().toLowerCase(Locale.ENGLISH));
        if (inv == null){
            inv = new CBEnderChest(target, online);
            CBEnderChest.chests.put(target.getName().toLowerCase(Locale.ENGLISH), inv);
        }

        // open
        player.openInventory(inv.getBukkitInventory());

        // logg
        SakuraCmdUtil.sendlog(sender, PlayerManager.getPlayer(player).getName() + "&6 が &7"
                + (online ? (PlayerManager.getPlayer(target).getName()) : target.getName()) + " &6のエンダーチェストを開きました");
    }
}