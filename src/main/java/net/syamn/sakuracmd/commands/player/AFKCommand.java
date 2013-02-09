/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.player
 * Created: 2013/01/03 23:19:36
 */
package net.syamn.sakuracmd.commands.player;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.sakuracmd.worker.AFKWorker;
import net.syamn.sakuracmd.worker.InvisibleWorker;
import net.syamn.utils.StrUtil;
import net.syamn.utils.exception.CommandException;

/**
 * AFKCommand (AFKCommand.java)
 * @author syam(syamn)
 */
public class AFKCommand extends BaseCommand{
    public AFKCommand(){
        bePlayer = true;
        name = "afk";
        perm = Perms.AFK;
        argLength = 0;
        usage = "[message] <- toggle your AFK status";
    }

    @Override
    public void execute() throws CommandException{
        if (InvisibleWorker.getInstance().isInvisible(player)){
            throw new CommandException("&cあなたは透明モードが有効になっています！");
        }

        AFKWorker worker = AFKWorker.getInstance();
        if (worker.isAfk(player)){
            worker.setOnline(player);
        }else{
            if (args.size() > 0){
                worker.setAfk(player, StrUtil.join(args, " "));
            }else{
                worker.setAfk(player);
            }
        }
    }
}
