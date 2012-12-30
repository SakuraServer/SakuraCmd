/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.tp
 * Created: 2012/12/29 8:17:40
 */
package net.syamn.sakuracmd.commands.tp;

import net.syamn.sakuracmd.Perms;
import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

/**
 * TpHereCommand (TpHereCommand.java)
 * @author syam(syamn)
 */
public class TpHereCommand extends BaseCommand{
    public TpHereCommand(){
        bePlayer = true;
        name = "tphere";
        perm = Perms.TPHERE;
        argLength = 1;
        usage = "[player] <- tp ";
    }
    
    public void execute() throws CommandException{
        final Player target = Bukkit.getPlayer(args.get(0));
        if (target == null || !target.isOnline()){
            throw new CommandException("&cプレイヤーが見つかりません！");
        }
        
        target.teleport(player, TeleportCause.COMMAND);
        
        Util.message(target, "&aプレイヤー " + sender.getName() + " があなたをテレポートしました");
        Util.message(sender, "&aプレイヤー " + target.getName() + " をあなたにテレポートしました");
    }
}
