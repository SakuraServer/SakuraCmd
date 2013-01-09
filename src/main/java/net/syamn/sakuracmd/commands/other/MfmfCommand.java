/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.other
 * Created: 2013/01/09 14:40:54
 */
package net.syamn.sakuracmd.commands.other;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * MfmfCommand (MfmfCommand.java)
 * @author syam(syamn)
 */
public class MfmfCommand extends BaseCommand {
    public MfmfCommand() {
        bePlayer = true;
        name = "mfmf";
        perm = null;
        argLength = 0;
        usage = "<player> <- mfmf!";
    }
    
    @Override
    public void execute() throws CommandException {
        if (args.size() == 0) {
            Util.message(sender, " &aもふもふ...？&7 /もふもふ <プレイヤー名>");
            Util.message(sender, " &aあなたは今までに" + PlayerManager.getPlayer(player).getData().getMofCount() + "回もふもふされました！");
        } else {
            final Player target = Bukkit.getPlayer(args.get(0));
            if (target == null || !target.isOnline()){
                throw new CommandException(" &6もふ...？ 相手が見つからないです...。");
            }
            if (target.equals(sender)){
                throw new CommandException("&c自分をもふもふできません！");
            }
            
            Util.message(target, " &6'" + sender.getName() + "'&aにもふもふされました！");
            Util.message(sender, " &6'" + target.getName() + "'&aをもふもふしました！");
            
            // TODO old sakuraserver code. hook economy, update userData
            /*
            if (Actions.takeMoney(player.getName(), 150)) {
                SakuraPlayer sp = SakuraServer.playerData.get(target);
                
                if (sp == null) {
                    Actions.message(sender, null, "&cエラーが発生しました！相手に一度ログアウトしてもらってください！");
                    return;
                }
                
                Actions.addMoney(target.getName(), 100);
                int added = sp.addMofCount(); // もふもふカウント
                
                Actions.message(null, target, " &6'" + player.getName() + "'&aにもふもふされました！(+100Coin)(" + added + "回目)");
                Actions.message(null, player, " &6'" + target.getName() + "'&aをもふもふしました！&c(-150Coin)");
            }
            else {
                Actions.message(null, target, " &6'" + player.getName() + "'&aにもふもふされました！");
                Actions.message(null, player, " &6'" + target.getName() + "'&aをもふもふしました！");
            }
            */
        }
    }
    
    @Override
    public boolean permission(CommandSender sender) {
        return true;
    }
}
