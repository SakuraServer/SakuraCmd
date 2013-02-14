/**
 * SakuraCmd - Package: net.syamn.sakuracmd.enums
 * Created: 2013/02/13 19:52:09
 */
package net.syamn.sakuracmd.signs;

import java.util.Locale;

import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.exception.SignException;

import org.bukkit.entity.Player;


/**
 * HardEndSign (HardEndSign.java)
 * @author syam(syamn)
 */
public class HardEndSign extends BaseSign{
    public HardEndSign() {
        super("HardEnd");
    }
    
    private static final String mainCommand = "hardend";
    
    @Override
    protected void onSignInteract(final Player player, final ISign sign, final SakuraCmd plugin) throws SignException{
        final String[] line2 = sign.getLine(1).trim().toLowerCase(Locale.ENGLISH).split(" ");
        
        //player.performCommand(mainCommand + line2);
        final BaseCommand cmd = plugin.getCommandHandler().getCommand(mainCommand);
        if (cmd == null){
            throw new SignException("&cコマンドが読み込まれていません。管理人にご連絡ください。");
        }
        
        cmd.run(plugin, player, mainCommand, line2, true);
    }
}
