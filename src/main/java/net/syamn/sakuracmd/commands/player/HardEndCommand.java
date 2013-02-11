/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.player
 * Created: 2013/02/12 2:51:27
 */
package net.syamn.sakuracmd.commands.player;

import java.util.Locale;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.enums.PartyStatus;
import net.syamn.sakuracmd.feature.HardEndManager;
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
 * HardEndCommand (HardEndCommand.java)
 * @author syam(syamn)
 */
public class HardEndCommand extends BaseCommand{
    public HardEndCommand(){
        bePlayer = true;
        name = "hardend";
        perm = Perms.HARD_END;
        argLength = 1;
        usage = "[action] <- hard end commands";
    }

    private HardEndManager mgr = null;
    
    @Override
    public void execute() throws CommandException{
        mgr = HardEndManager.getInstance();
        if (mgr == null){
            throw new CommandException("&c現在ハードエンドは利用できません");
        }
        
        final String action = args.remove(0).trim().toLowerCase(Locale.ENGLISH);
       
        if (action.equals("ready")){
            ready();
        }
        else if (action.equals("start")){
            start();
        }
        else if (action.equals("join")){
            join();
        }
        
        if (args.size() == 0 && !isPlayer){
            throw new CommandException("&cプレイヤー名を指定してください！");
        }

        final Player target = (args.size() > 0) ? Bukkit.getPlayer(args.get(0)) : player;
        if (target == null || !target.isOnline()){
            throw new CommandException("&cプレイヤーが見つかりません！");
        }
        final SakuraPlayer sp = PlayerManager.getPlayer(target);

        if (sp.hasPower(Power.GODMODE)){
            sp.removePower(Power.GODMODE);
            if (!sender.equals(target)){
                Util.message(sender, "&3" + sp.getName() + " &3の無敵モードを解除しました");
            }
            Util.message(target, "&3あなたの無敵モードは解除されました");
            SakuraCmdUtil.sendlog(sender, sp.getName() + "&3 が無敵モードを解除しました");
        }
        else{
            sp.addPower(Power.GODMODE);
            if (!sender.equals(target)){
                Util.message(sender, "&3" + sp.getName() + " &3を無敵モードにしました");
            }
            Util.message(target, "&3あなたは無敵モードになりました");
            SakuraCmdUtil.sendlog(sender, sp.getName() + "&3 が無敵モードになりました");
        }
    }
    
    private void ready() throws CommandException{
        if (mgr.getStatus() != PartyStatus.WAITING){
            throw new CommandException("&c現在既にパーティが作成、または開始されています");
        }
        
        Boolean open = null;
        if (args.size() > 0){
            if (args.get(0).equalsIgnoreCase("open")){
                open = true;
            }else if(args.get(0).equalsIgnoreCase("close")){
                open = false;
            }
        }
        if (open == null){
            throw new CommandException("&cパーティの種類を open または close で指定してください！");
        }
        
        mgr.openParty(open, player);
    }
    
    private void start() throws CommandException{
        if (mgr.getStatus() != PartyStatus.OPENING){
            throw new CommandException("&c現在パーティが開始待機中ではありません！");
        }
        if (mgr.isLeader(player)){
            throw new CommandException("&cあなたはパーティリーダーではありません！リーダーのみが実行できます！");
        }
        
        Player p;
        for (final String name : mgr.getMembersMap().keySet()){
            p = Bukkit.getPlayerExact(name);
            if (p == null || !p.isOnline()){
                mgr.removeMember(name);
                mgr.message("&cプレイヤー &6" + name + " &cはオフラインのため、パーティから自動削除されました");
            }
        }
        
        if (mgr.getMembersMap().size() < mgr.getMinPlayers()){
            throw new CommandException("&c開始可能なパーティメンバー数に達していません！" + mgr.getMinPlayers() + "人必要です！");
        }
        
        mgr.startParty();
    }
    
    private void join() throws CommandException{
        if (mgr.getStatus() != PartyStatus.OPENING){
            throw new CommandException("&c現在パーティが開始待機中ではありません！");
        }
        if (mgr.isMember(player)){
            throw new CommandException("&cあなたは既にこのパーティに参加しています！");
        }
        if (!mgr.isOpenParty()){
            throw new CommandException("&cクローズパーティのため、パーティリーダーの招待が必要です！");
        }
        mgr.addMember(player.getName(), false);
        mgr.message(" " + PlayerManager.getPlayer(player).getName() + " &dがこのパーティに参加しました！");
    }
}