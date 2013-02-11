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
import net.syamn.utils.StrUtil;
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
        else if (action.equals("invite")){
            invite();
        }
        else{
            throw new CommandException("&c不正なサブコマンドです！");
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
        if (!mgr.isLeader(player)){
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
        if (!mgr.isOpenParty() && !mgr.invited.contains(player.getName().toLowerCase(Locale.ENGLISH))){
            throw new CommandException("&cクローズパーティのため、パーティリーダーの招待が必要です！");
        }
        
        if (!mgr.isOpenParty()){
            mgr.invited.remove(player.getName().toLowerCase(Locale.ENGLISH));
        }
        
        mgr.addMember(player.getName(), false);
        mgr.message(" " + PlayerManager.getPlayer(player).getName() + " &dがこのパーティに参加しました！");
    }
    
    private void invite() throws CommandException{
        if (mgr.getStatus() != PartyStatus.OPENING){
            throw new CommandException("&c現在パーティが開始待機中ではありません！");
        }
        if (!mgr.isLeader(player)){
            throw new CommandException("&cあなたはパーティリーダーではありません！リーダーのみが実行できます！");
        }
        
        if (args.size() < 1){
            throw new CommandException("&cパーティに招待するプレイヤーを入力してください！");
        }
        
        Player p = Bukkit.getPlayer(args.get(0).trim());
        if (p == null || !p.isOnline()){
            throw new CommandException("&cプレイヤー " + args.get(0).trim() + " が見つかりません！");
        }
        
        mgr.invited.add(p.getName().toLowerCase(Locale.ENGLISH));
        Util.message(p, " " + PlayerManager.getPlayer(player).getName() + " &dがあなたにパーティ招待を送信しました！");
        Util.message(p, " &6/hardend join &dコマンドで招待を受諾し参加します！");
        
        Util.message(sender, " " + PlayerManager.getPlayer(p).getName() + " &dにパーティ招待を送信しました！");
    }
    
    /*
    private void permCheck() throws CommandException{
        if (!Perms.HARD_END.has(sender)){
            throw new CommandException("&c権限がありません！");
        }
    }
    */
}