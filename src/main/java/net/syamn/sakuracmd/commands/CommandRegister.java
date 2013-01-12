/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands
 * Created: 2012/12/29 6:22:06
 */
package net.syamn.sakuracmd.commands;

import java.util.HashSet;
import java.util.Set;

import net.syamn.sakuracmd.commands.items.RepairAllCommand;
import net.syamn.sakuracmd.commands.items.RepairItemCommand;
import net.syamn.sakuracmd.commands.other.ColorsCommand;
import net.syamn.sakuracmd.commands.other.ConfirmCommand;
import net.syamn.sakuracmd.commands.other.MfmfCommand;
import net.syamn.sakuracmd.commands.other.SakuraCmdCommand;
import net.syamn.sakuracmd.commands.player.AFKCommand;
import net.syamn.sakuracmd.commands.player.FlyCommand;
import net.syamn.sakuracmd.commands.player.GamemodeCommand;
import net.syamn.sakuracmd.commands.player.GodCommand;
import net.syamn.sakuracmd.commands.player.InvisibleCommand;
import net.syamn.sakuracmd.commands.player.WhoisCommand;
import net.syamn.sakuracmd.commands.server.LockdownCommand;
import net.syamn.sakuracmd.commands.tp.BackCommand;
import net.syamn.sakuracmd.commands.tp.TpCommand;
import net.syamn.sakuracmd.commands.tp.TpHereCommand;
import net.syamn.sakuracmd.commands.world.WeatherCommand;

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
        cmds.add(new BackCommand());
        
        // Server Commands
        cmds.add(new LockdownCommand());
        
        // Player Commands
        cmds.add(new AFKCommand());
        cmds.add(new InvisibleCommand());
        cmds.add(new GodCommand());
        cmds.add(new WhoisCommand());
        cmds.add(new GamemodeCommand());
        cmds.add(new FlyCommand());
        
        // World Commands
        cmds.add(new WeatherCommand());
        
        // Other Commands
        cmds.add(new SakuraCmdCommand());
        cmds.add(new ConfirmCommand());
        cmds.add(new MfmfCommand());
        cmds.add(new ColorsCommand());
        
        return cmds;
    }
    
    public static void registerCommands(final CommandHandler handler){
        Set<BaseCommand> cmds = getCommands();
        
        for (final BaseCommand cmd : cmds){
            handler.registerCommand(cmd);
        }
    }
}
