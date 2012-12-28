/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands
 * Created: 2012/12/28 13:55:10
 */
package net.syamn.sakuracmd.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.utils.LogUtil;
import net.syamn.utils.Util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

/**
 * CommandHandler (CommandHandler.java)
 * @author syam(syamn)
 */
public class CommandHandler implements TabExecutor{
    private final SakuraCmd plugin;
    private Map<String, BaseCommand> commands = new HashMap<String, BaseCommand>();
    
    public CommandHandler(final SakuraCmd plugin){
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args){
        final String commandName = command.getName().toLowerCase(Locale.ENGLISH);
        BaseCommand cmd = commands.get(commandName);
        if (cmd == null){
            Util.message(sender, "&cCommand not found!");
            return true;
        }
        
        // run command
        cmd.run(plugin, sender, commandLabel, args);
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String commandLabel, String[] args){
        final String commandName = command.getName().toLowerCase(Locale.ENGLISH);
        BaseCommand cmd = commands.get(commandName);
        if (cmd == null){
            return null;
        }
        
        // check perms
        if (sender != null && !cmd.permission(sender)){
            return null;
        }
        
        // get tab complete
        return cmd.tabComplete(plugin, sender, commandLabel, args);
    }
    
    public void registerCommand(final BaseCommand bc){
        if (bc.name != null){
            commands.put(bc.name, bc);
        }else{
            LogUtil.warning("Invalid command not registered! " + bc.getClass().getName());
        }
    }
}
