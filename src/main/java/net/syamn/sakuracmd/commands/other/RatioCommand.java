/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.other
 * Created: 2013/02/06 19:33:54
 */
package net.syamn.sakuracmd.commands.other;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.feature.HawkEyeSearcher;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

import org.bukkit.Bukkit;

/**
 * RatioCommand (RatioCommand.java)
 * @author syam(syamn)
 */
public class RatioCommand extends BaseCommand{
    public RatioCommand(){
        bePlayer = false;
        name = "ratio";
        perm = Perms.RATIO;
        argLength = 1;
        usage = "[player] <- check players mined ratio";
    }
    
    public void execute() throws CommandException{
        if (HawkEyeSearcher.isUsing()){
            throw new CommandException("&c現在別の検索タスクが稼働中です。しばらくお待ちください。");
        }
        Util.message(sender, "&7検索しています...");
        HawkEyeSearcher searcher = new HawkEyeSearcher(plugin, sender, args.get(0), 48, true);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, searcher, 1L);
    }
}
