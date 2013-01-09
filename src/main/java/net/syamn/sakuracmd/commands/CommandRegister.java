/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands
 * Created: 2012/12/29 6:22:06
 */
package net.syamn.sakuracmd.commands;

import java.util.HashSet;
import java.util.Set;

import net.syamn.sakuracmd.commands.items.*;
import net.syamn.sakuracmd.commands.other.*;
import net.syamn.sakuracmd.commands.player.*;
import net.syamn.sakuracmd.commands.server.*;
import net.syamn.sakuracmd.commands.tp.*;
import net.syamn.sakuracmd.commands.world.*;

/**
 * CommandRegister (CommandRegister.java)
 * @author syam(syamn)
 */
public class CommandRegister {
    private static Set<BaseCommand> getCommands(){
        Set<BaseCommand> cmds = new HashSet<BaseCommand>();
        
        // Item Commands
        cmds.add(new RepairItemCommand());
        cmds.add(new RepairAllCommand());
        
        // Teleport Commands
        cmds.add(new TpHereCommand());
        cmds.add(new TpCommand());
        
        // Server Commands
        cmds.add(new LockdownCommand());
        
        // Player Commands
        cmds.add(new AFKCommand());
        cmds.add(new InvisibleCommand());
        cmds.add(new GodCommand());
        cmds.add(new WhoisCommand());
        
        // World Commands
        cmds.add(new WeatherCommand());
        
        // Other Commands
        cmds.add(new SakuraCmdCommand());
        cmds.add(new ConfirmCommand());
        cmds.add(new MfmfCommand());
        
        return cmds;
    }
    
    public static void registerCommands(final CommandHandler handler){
        Set<BaseCommand> cmds = getCommands();
        
        for (final BaseCommand cmd : cmds){
            handler.registerCommand(cmd);
        }
    }
}
