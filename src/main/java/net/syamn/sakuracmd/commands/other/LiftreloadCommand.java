/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.other
 * Created: 2013/05/04 4:43:04
 */
package net.syamn.sakuracmd.commands.other;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

/**
 * LiftreloadCommand (LiftreloadCommand.java)
 * @author syam(syamn)
 */
public class LiftreloadCommand extends BaseCommand {
    public LiftreloadCommand() {
        bePlayer = true;
        name = "liftreload";
        perm = Perms.LIFT_RELOAD;
        argLength = 0;
        usage = "<- reload Lift plugin";
    }
    
    @Override
    public void execute() throws CommandException{
        Util.executeCommandOnConsole("plm reload Lift");
        Util.message(sender, "&aLiftプラグインをリロードしました");
    }
}
