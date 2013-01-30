/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.other
 * Created: 2013/01/30 0:30:46
 */
package net.syamn.sakuracmd.commands.other;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.utils.exception.CommandException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * WebCommand (WebCommand.java)
 * @author syam(syamn)
 */
public class WebCommand extends BaseCommand {
    public WebCommand() {
        bePlayer = false;
        name = "web";
        perm = null;
        argLength = 1;
        usage = "<- web link commands";
    }
    
    private static final String reset = "\u00A7r";
    
    @Override
    public void execute() throws CommandException {
        String action = args.get(0);
        
        if (action.equalsIgnoreCase("tploc")){
            tploc();
            return;
        }
        
        throw new CommandException("&cUndefined command!");
    }
    
    private void tploc() throws CommandException {
        
    }
    
    @Override
    public boolean permission(CommandSender sender) {
        if (sender instanceof Player){
            return false;
        }
        return true;
    }
}
