/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.player
 * Created: 2013/02/05 7:12:03
 */
package net.syamn.sakuracmd.commands.player;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.sakuracmd.player.Power;
import net.syamn.sakuracmd.player.SakuraPlayer;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * NoPickupCommand (NoPickupCommand.java)
 * @author syam(syamn)
 */
public class NoPickupCommand extends BaseCommand{
    public NoPickupCommand(){
        bePlayer = false;
        name = "nopickup";
        perm = Perms.NO_PICKUP;
        argLength = 0;
        usage = "[player] <- toggle no pickup mode";
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
        
        // self-check
        if (!sender.equals(target) && !Perms.NO_PICKUP_OTHER.has(sender)){
            throw new CommandException("&c他人のアイテム無視モードを変更する権限がありません！");
        }
        
        if (sp.hasPower(Power.NO_PICKUP)){
            // Remove power
            sp.removePower(Power.NO_PICKUP);
            
            if (!sender.equals(target)){
                Util.message(sender, "&3" + sp.getName() + " &3のアイテム無視モードを解除しました");
            }
            Util.message(target, "&3あなたのアイテム無視モードは解除されました");
        }else{
            // Add power
            sp.addPower(Power.NO_PICKUP);
            
            if (!sender.equals(target)){
                Util.message(sender, "&3" + sp.getName() + " &3をアイテム無視モードにしました");
            }
            Util.message(target, "&3あなたはアイテム無視モードになりました");
        }
    }
}