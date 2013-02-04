/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.player
 * Created: 2013/02/05 5:00:16
 */
package net.syamn.sakuracmd.commands.player;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.sakuracmd.player.Power;
import net.syamn.sakuracmd.player.SakuraPlayer;
import net.syamn.sakuracmd.utils.plugin.SakuraCmdUtil;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * SpecChestCommand (SpecChestCommand.java)
 * @author syam(syamn)
 */
public class SpecChestCommand extends BaseCommand{
    public SpecChestCommand(){
        bePlayer = false;
        name = "specchest";
        perm = Perms.SPECCHEST;
        argLength = 0;
        usage = "[player] <- toggle special chest mode";
    }

    public void execute() throws CommandException{
        if (args.size() == 0 && !isPlayer){
            throw new CommandException("&cプレイヤー名を指定してください！");
        }
        
        final Player target = (args.size() > 0) ? Bukkit.getPlayer(args.get(0)) : player;
        if (target == null || !target.isOnline()){
            throw new CommandException("&cプレイヤーが見つかりません！");
        }
        final SakuraPlayer sp = PlayerManager.getPlayer(target);
        
        // self-check
        if (!sender.equals(target) && !Perms.SPECCHEST_OTHER.has(sender)){
            throw new CommandException("&c他人のチェスト拡張モードを変更する権限がありません！");
        }
        
        if (sp.hasPower(Power.SPEC_CHEST)){
            // Remove power
            sp.removePower(Power.SPEC_CHEST);
            
            if (!sender.equals(target)){
                Util.message(sender, "&3" + sp.getName() + " &3のチェスト拡張モードを解除しました");
            }
            Util.message(target, "&3あなたのチェスト拡張モードは解除されました");
            SakuraCmdUtil.sendlog(sender, sp.getName() + " &6のチェスト拡張モードは解除されました");
        }else{
            // Add power
            sp.addPower(Power.SPEC_CHEST);
            
            if (!sender.equals(target)){
                Util.message(sender, "&3" + sp.getName() + " &3をチェスト拡張モードにしました");
            }
            Util.message(target, "&3あなたはチェスト拡張モードになりました");
            SakuraCmdUtil.sendlog(sender, sp.getName() + " &6がチェスト拡張モードになりました");
        }
    }
}
