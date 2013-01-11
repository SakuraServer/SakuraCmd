/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.tp
 * Created: 2012/12/29 8:30:44
 */
package net.syamn.sakuracmd.commands.tp;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.sakuracmd.player.SakuraPlayer;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

/**
 * TpCommand (TpCommand.java)
 * @author syam(syamn)
 */
public class TpCommand extends BaseCommand{
    public TpCommand(){
        bePlayer = true;
        name = "tp";
        perm = Perms.TP;
        argLength = 1;
        usage = "[player] <- tp ";
    }
    
    public void execute() throws CommandException{
        final Player target = Bukkit.getPlayer(args.get(0));
        if (target == null || !target.isOnline()){
            throw new CommandException("&cプレイヤーが見つかりません！");
        }
        final SakuraPlayer sp = PlayerManager.getPlayer(target);
        
        player.teleport(target, TeleportCause.COMMAND);
        
        Util.message(sender, "&aプレイヤー " + sp.getName() + "&a にテレポートしました！");
    }
}
