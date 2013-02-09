/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.other
 * Created: 2013/01/10 7:39:41
 */
package net.syamn.sakuracmd.commands.other;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

import org.bukkit.command.CommandSender;

/**
 * ColorsCommand (ColorsCommand.java)
 * @author syam(syamn)
 */
public class ColorsCommand extends BaseCommand {
    public ColorsCommand() {
        bePlayer = false;
        name = "colors";
        perm = null;
        argLength = 0;
        usage = "<- see bukkit colors list";
    }

    private static final String rst = "\u00A7r";

    @Override
    public void execute() throws CommandException {
        Util.message(sender, "&aカラー/フォーマットコードリスト: ");
        sender.sendMessage(" \u00A70 &0 \u00A71 &1 \u00A72 &2 \u00A73 &3 \u00A74 &4 \u00A75 &5 \u00A76 &6 \u00A77 &7");
        sender.sendMessage(" \u00A78 &8 \u00A79 &9 \u00A7a &a \u00A7b &b \u00A7c &c \u00A7d &d \u00A7e &e \u00A7f &f");
        sender.sendMessage(" \u00A7m &m " + rst + "\u00A7n &n " + rst + "\u00A7l &l " + rst + "\u00A7k &k" + rst + " \u00A7o &o " + rst + "\u00A7r &r(reset)");
    }

    @Override
    public boolean permission(CommandSender sender) {
        return true;
    }
}
