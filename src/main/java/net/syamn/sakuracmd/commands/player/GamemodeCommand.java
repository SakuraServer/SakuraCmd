/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.player
 * Created: 2013/01/12 0:04:44
 */
package net.syamn.sakuracmd.commands.player;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.sakuracmd.player.SakuraPlayer;
import net.syamn.utils.StrUtil;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * GamemodeCommand (GamemodeCommand.java)
 * @author syam(syamn)
 */
public class GamemodeCommand extends BaseCommand{
    public GamemodeCommand(){
        bePlayer = false;
        name = "gamemode";
        perm = Perms.GAMEMODE;
        argLength = 0;
        usage = "[player] <- toggle gamemode";
    }

    @Override
    public void execute() throws CommandException{
        if (args.size() == 0 && !isPlayer){
            throw new CommandException("&cプレイヤー名を指定してください！");
        }

        final Player target = (args.size() > 0) ? Bukkit.getPlayer(args.remove(0)) : player;
        if (target == null || !target.isOnline()){
            throw new CommandException("&cプレイヤーが見つかりません！");
        }
        final SakuraPlayer sp = PlayerManager.getPlayer(target);

        // self-check
        if (!sender.equals(target) && !Perms.GAMEMODE_OTHER.has(sender)){
            throw new CommandException("&c他人のゲームモードを変更する権限がありません！");
        }

        GameMode toMode = null;
        if (args.size() < 1){
            toMode = (target.getGameMode().equals(GameMode.SURVIVAL)) ? GameMode.CREATIVE : GameMode.SURVIVAL;
        }else{
            toMode = StrUtil.isMatches(GameMode.values(), args.get(0));
            if (toMode == null){
                throw new CommandException("&c有効なゲームモードを指定してください: survival, creative, adventure");
            }
        }

        target.setGameMode(toMode);

        if (!sender.equals(target)){
            Util.message(sender, sp.getName() + "&3 のゲームモードを " + toMode.name() + " に変更しました！");
        }
        Util.message(target, "&3あなたのゲームモードは " + toMode.name() + " に変更されました！");
    }
}