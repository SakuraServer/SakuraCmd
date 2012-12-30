/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands
 * Created: 2012/12/29 6:22:06
 */
package net.syamn.sakuracmd.commands;

import java.util.HashSet;
import java.util.Set;

import net.syamn.sakuracmd.commands.items.*;
import net.syamn.sakuracmd.commands.server.Lockdown;
import net.syamn.sakuracmd.commands.tp.*;

/**
 * CommandRegister (CommandRegister.java)
 * @author syam(syamn)
 */
public class CommandRegister {
    private static Set<BaseCommand> getCommands(){
        Set<BaseCommand> cmds = new HashSet<BaseCommand>();
        
        // Item Commands
        cmds.add(new RepairItem());
        cmds.add(new RepairAll());
        
        // Teleport Commands
        cmds.add(new TpHere());
        cmds.add(new Tp());
        
        // Server Commands
        cmds.add(new Lockdown());
        
        return cmds;
    }
    
    public static void registerCommands(final CommandHandler handler){
        Set<BaseCommand> cmds = getCommands();
        
        for (final BaseCommand cmd : cmds){
            handler.registerCommand(cmd);
        }
    }
}
