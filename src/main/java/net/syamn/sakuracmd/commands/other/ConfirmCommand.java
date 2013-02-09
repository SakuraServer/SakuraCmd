/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.other
 * Created: 2013/01/09 0:48:13
 */
package net.syamn.sakuracmd.commands.other;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.utils.exception.CommandException;
import net.syamn.utils.queue.ConfirmQueue;

import org.bukkit.command.CommandSender;

/**
 * ConfirmCommand (ConfirmCommand.java)
 * @author syam(syamn)
 */
public class ConfirmCommand extends BaseCommand{
    public ConfirmCommand(){
        bePlayer = false;
        name = "confirm";
        perm = null;
        argLength = 0;
        usage = "<- confirm commands";
    }

    @Override
    public void execute() throws CommandException{
        boolean ran = ConfirmQueue.getInstance().confirmQueue(sender);
        if (!ran) {
            throw new CommandException("&cあなたの実行待ちコマンドはありません！");
        }
    }

    @Override
    public boolean permission(CommandSender sender){
        return true; // same as super class
    }
}
