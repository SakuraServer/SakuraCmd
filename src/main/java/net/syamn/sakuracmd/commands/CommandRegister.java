/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands
 * Created: 2012/12/29 6:22:06
 */
package net.syamn.sakuracmd.commands;

import java.util.HashSet;
import java.util.Set;

import net.syamn.sakuracmd.commands.db.MailCommand;
import net.syamn.sakuracmd.commands.db.PasswordCommand;
import net.syamn.sakuracmd.commands.db.RegisterCommand;
import net.syamn.sakuracmd.commands.items.RepairAllCommand;
import net.syamn.sakuracmd.commands.items.RepairItemCommand;
import net.syamn.sakuracmd.commands.other.AdminCommand;
import net.syamn.sakuracmd.commands.other.ColorsCommand;
import net.syamn.sakuracmd.commands.other.ConfirmCommand;
import net.syamn.sakuracmd.commands.other.MfmfCommand;
import net.syamn.sakuracmd.commands.other.SakuraCmdCommand;
import net.syamn.sakuracmd.commands.other.WebCommand;
import net.syamn.sakuracmd.commands.player.AFKCommand;
import net.syamn.sakuracmd.commands.player.FlyCommand;
import net.syamn.sakuracmd.commands.player.FlymodeCommand;
import net.syamn.sakuracmd.commands.player.GamemodeCommand;
import net.syamn.sakuracmd.commands.player.GodCommand;
import net.syamn.sakuracmd.commands.player.InvisibleCommand;
import net.syamn.sakuracmd.commands.player.NoPickupCommand;
import net.syamn.sakuracmd.commands.player.OpenEnderCommand;
import net.syamn.sakuracmd.commands.player.OpenInvCommand;
import net.syamn.sakuracmd.commands.player.SpecChestCommand;
import net.syamn.sakuracmd.commands.player.WhoisCommand;
import net.syamn.sakuracmd.commands.server.LockdownCommand;
import net.syamn.sakuracmd.commands.tp.BackCommand;
import net.syamn.sakuracmd.commands.tp.RideCommand;
import net.syamn.sakuracmd.commands.tp.TpCommand;
import net.syamn.sakuracmd.commands.tp.TpHereCommand;
import net.syamn.sakuracmd.commands.tp.TplocCommand;
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
        cmds.add(new RideCommand());
        cmds.add(new TplocCommand());
        
        // Server Commands
        cmds.add(new LockdownCommand());
        
        // Player Commands
        cmds.add(new AFKCommand());
        cmds.add(new InvisibleCommand());
        cmds.add(new GodCommand());
        cmds.add(new WhoisCommand());
        cmds.add(new GamemodeCommand());
        cmds.add(new FlyCommand());
        cmds.add(new FlymodeCommand());
        cmds.add(new OpenInvCommand());
        cmds.add(new OpenEnderCommand());
        cmds.add(new SpecChestCommand());
        cmds.add(new NoPickupCommand());
        
        // World Commands
        cmds.add(new WeatherCommand());
        
        // Database Commands
        cmds.add(new RegisterCommand());
        cmds.add(new PasswordCommand());
        cmds.add(new MailCommand());
        
        // Other Commands
        cmds.add(new AdminCommand());
        cmds.add(new SakuraCmdCommand());
        cmds.add(new ConfirmCommand());
        cmds.add(new MfmfCommand());
        cmds.add(new ColorsCommand());
        cmds.add(new WebCommand());
        
        return cmds;
    }
    
    public static void registerCommands(final CommandHandler handler){
        Set<BaseCommand> cmds = getCommands();
        
        for (final BaseCommand cmd : cmds){
            handler.registerCommand(cmd);
        }
    }
}
