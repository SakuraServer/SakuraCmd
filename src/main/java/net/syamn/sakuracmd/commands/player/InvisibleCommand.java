/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.player
 * Created: 2013/01/04 13:55:35
 */
package net.syamn.sakuracmd.commands.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.sakuracmd.worker.InvisibleWorker;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

/**
 * InvisibleCommand (InvisibleCommand.java)
 * @author syam(syamn)
 */
public class InvisibleCommand extends BaseCommand{
    public InvisibleCommand(){
        bePlayer = false;
        name = "invisible";
        perm = Perms.INVISIBLE;
        argLength = 0;
        usage = "<- toggle your invisible status";
    }
    
    public void execute() throws CommandException{
        if (args.size() == 0 && !isPlayer){
            throw new CommandException("&cプレイヤー名を指定してください！");
        }
        
        final Player target = (args.size() > 0) ? Bukkit.getPlayer(args.get(0)) : player;
        if (target == null || !target.isOnline()){
            throw new CommandException("&cプレイヤーが見つかりません！");
        }
        
        InvisibleWorker worker = InvisibleWorker.getInstance();
        
        if (worker.isInvisible(target)){
            worker.reappear(target);
            if (!sender.equals(target)){
                Util.message(sender, "&a" + target.getName() + " の透明モードを解除しました");
            }
            Util.message(target, "&aあなたの透明モードは解除されました");
        }else{
            worker.vanish(target, false);
            if (!sender.equals(target)){
                Util.message(sender, "&c" + target.getName() + " を透明モードにしました");
            }
            Util.message(target, "&cあなたは透明モードになりました");
        }
    }
}
