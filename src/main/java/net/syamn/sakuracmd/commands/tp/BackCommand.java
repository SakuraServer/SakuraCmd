/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.tp
 * Created: 2013/01/12 17:19:03
 */
package net.syamn.sakuracmd.commands.tp;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.sakuracmd.player.SakuraPlayer;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

/**
 * BackCommand (BackCommand.java)
 * @author syam(syamn)
 */
public class BackCommand extends BaseCommand{
    public BackCommand(){
        bePlayer = false;
        name = "back";
        perm = Perms.BACK;
        argLength = 0;
        usage = "[player] <- back to previous location";
    }
    
    public void execute() throws CommandException{
        if (args.size() == 0 && !isPlayer){
            throw new CommandException("&cプレイヤー名を指定してください！");
        }
        
        final Player target = (args.size() > 0) ? Bukkit.getPlayer(args.get(0)) : player;
        if (target == null || !target.isOnline()){
            throw new CommandException("&cプレイヤーが見つかりません！");
        }
        final SakuraPlayer sp = PlayerManager.getPlayer(target);
        
        final Location prev = sp.getData().getLastLocation();
        if (prev == null){
            throw new CommandException("&c戻る座標が見つかりません！");
        }
        
        target.teleport(prev, TeleportCause.PLUGIN);
        if (!sender.equals(target)){
            Util.message(sender, "&3" + sp.getName() + " &3を1つ前の座標にテレポートさせました");
        }
        // no message send to target here
    }
}
