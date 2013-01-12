/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.player
 * Created: 2013/01/12 17:49:00
 */
package net.syamn.sakuracmd.commands.player;

import net.syamn.sakuracmd.SCHelper;
import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.manager.Worlds;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.sakuracmd.player.Power;
import net.syamn.sakuracmd.player.SakuraPlayer;
import net.syamn.sakuracmd.utils.plugin.SakuraCmdUtil;
import net.syamn.utils.Util;
import net.syamn.utils.economy.EconomyUtil;
import net.syamn.utils.exception.CommandException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * FlymodeCommand (FlymodeCommand.java)
 * @author syam(syamn)
 */
public class FlymodeCommand extends BaseCommand{
    public FlymodeCommand(){
        bePlayer = true;
        name = "flymode";
        perm = Perms.FLYMODE;
        argLength = 0;
        usage = "<- buy flymode";
    }

    public void execute() throws CommandException{
        if (player.getWorld().getName().equals(Worlds.main_world)){
            throw new CommandException("&cこのワールドでは飛行が許可されていません！");
        }
        if (player.getLocation().getY() > 257 || player.getLocation().getY() < 0){
            throw new CommandException("&cあなたの座標からこのコマンドは使えません！");
        }
        
        // pay cost
        if (!SCHelper.getInstance().isEnableEcon()){
            throw new CommandException("&c経済システムが動作していないため使えません！");
        }
        
        final SakuraPlayer sp = PlayerManager.getPlayer(player);
        
        double cost = 5000.0D; //TODO configuable
        boolean paid = EconomyUtil.takeMoney(player, cost);
        if (!paid){
            throw new CommandException("&cお金が足りません！ " + cost + "Coin必要です！");
        }
        
        int minute = 30; //TODO configuable
        
        
        
        
        // self-check
        if (!sender.equals(target) && !Perms.FLY_OTHER.has(sender)){
            throw new CommandException("&c他人の飛行モードを変更する権限がありません！");
        }
        
        if (sp.hasPower(Power.FLY)){
            // Remove fly power
            sp.removePower(Power.FLY);
            SakuraCmdUtil.changeFlyMode(target, false);
            
            if (!sender.equals(target)){
                Util.message(sender, "&3" + sp.getName() + " &3の無敵モードを解除しました");
            }
            Util.message(target, "&3あなたの飛行モードは解除されました");
        }else{
            // Add fly power
            sp.addPower(Power.FLY);
            SakuraCmdUtil.changeFlyMode(target, true);
            
            if (!sender.equals(target)){
                Util.message(sender, "&3" + sp.getName() + " &3を飛行モードにしました");
            }
            Util.message(target, "&3あなたは飛行モードになりました");
        }
    }
}