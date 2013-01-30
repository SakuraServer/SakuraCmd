/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.db
 * Created: 2013/01/13 4:05:43
 */
package net.syamn.sakuracmd.commands.db;

import java.util.ArrayList;
import java.util.HashMap;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.sakuracmd.storage.Database;
import net.syamn.utils.TimeUtil;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

import org.apache.commons.lang.RandomStringUtils;

/**
 * RegisterCommand (RegisterCommand.java)
 * @author syam(syamn)
 */
public class RegisterCommand extends BaseCommand{
    public RegisterCommand(){
        bePlayer = true;
        name = "register";
        perm = Perms.REGISTER;
        argLength = 0;
        usage = "<- regist to website";
    }
    
    public void execute() throws CommandException{
        Util.message(sender, "&6登録キーを発行しています...");
        
        final Database db = Database.getInstance();
        if (db == null || !db.isConnected()){
            throw new CommandException("&c現在データベースと接続されていません！");
        }
        
        final int pid = PlayerManager.getPlayer(player).getData().getPlayerID();
        
        // check if already registered
        HashMap<Integer, ArrayList<String>> records = db.read("SELECT * FROM `user_data` WHERE `player_id` = ?", pid);
        if (records != null && records.size() > 0){
            throw new CommandException("&cあなたは既にアカウントを登録しています！");
        }
        
        // generate random 4-chars regist key
        // not use following letters: IL1 il O0o
        final String registKey = RandomStringUtils.random(4, "abcdefghjkmnpqrstuvwxABCDEFGHJKMNPQRSTUVWXYZ23456789");
        
        // expired time
        final int expired = TimeUtil.getCurrentUnixSec().intValue() + (60 * 30); // available for 30 minutes
        
        final boolean success = db.write("REPLACE INTO `regist_key` (`player_id`, `key`, `expired`) VALUES (?, ?, ?)", pid, registKey, expired);
        if (!success){
            throw new CommandException("&c登録に失敗しました！時間を置いてやり直してください！");
        }
        Util.message(sender, "&aあなたの登録キー:&2 " + registKey);
        Util.message(sender, "&a登録用ページ (キー自動入力):&2 ");
        Util.message(sender, "&2  http://mc.sakura-server.net/make_account/" + player.getName() + "/" + registKey + "/");
        Util.message(sender, "&a登録ページから、上記の登録キーを入力して続行してください");
        Util.message(sender, "&aこの登録キーは発行後30分間のみ有効です");
    }
}
