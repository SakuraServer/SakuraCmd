/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.db
 * Created: 2013/01/14 0:22:43
 */
package net.syamn.sakuracmd.commands.db;

import java.util.ArrayList;
import java.util.HashMap;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.sakuracmd.storage.Database;
import net.syamn.sakuracmd.utils.plugin.Encrypter;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;
import net.syamn.utils.queue.ConfirmQueue;
import net.syamn.utils.queue.Queueable;
import net.syamn.utils.queue.QueuedCommand;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.entity.Player;

/**
 * PasswordCommand (PasswordCommand.java)
 * @author syam(syamn)
 */
public class PasswordCommand extends BaseCommand implements Queueable{
    public PasswordCommand(){
        bePlayer = true;
        name = "password";
        perm = Perms.PASSWORD;
        argLength = 0;
        usage = "<- reset website password";
    }
    
    public void execute() throws CommandException{
        final Database db = Database.getInstance();
        if (db == null || !db.isConnected()){
            throw new CommandException("&c現在データベースと接続されていません！");
        }
        
        ConfirmQueue.getInstance().addQueue(sender, this, null, 15);
        Util.message(sender, "&cウェブページのパスワードリセットを行います！");
        Util.message(sender, "&cランダム文字列で今のパスワードが上書きされます！");
        Util.message(sender, "&c本当に実行しますか？ &a/confirm&c コマンドで続行します。");
    }
    
    @Override
    public void executeQueue(QueuedCommand queued) {
        final Player player = (Player) queued.getSender();
        
        Util.message(player, "&6パスワードをリセットしています...");
        
        final Database db = Database.getInstance();
        if (db == null || !db.isConnected()){
            Util.message(player, "&c現在データベースと接続されていません！");
            return;
        }
        
        final String pname = player.getName();
        int pid = -1;
        
        // check if already registered
        HashMap<Integer, ArrayList<String>> records = db.read("SELECT `player_id` FROM `user_data` WHERE `player_name` = ?", pname);
        if (records == null || records.size() <= 0){
            Util.message(player, "&cアカウントが存在しません！ /register コマンドで登録できます！");
            return;
        }else{
            pid = Integer.parseInt(records.get(1).get(0));
        }
        
        // generate random 8-chars password, not use following letters: IL1 il O0o
        final String newPass = RandomStringUtils.random(8, "abcdefghjkmnpqrstuvwxABCDEFGHJKMNPQRSTUVWXYZ23456789");
        final String encrypted = Encrypter.getHash(newPass, Encrypter.ALG_SHA512);
        
        final boolean success = db.write("UPDATE `user_data` SET `password` = ? WHERE `player_id` = ?", encrypted, pid);
        if (!success){
            Util.message(player, "&cリセットに失敗しました！時間を置いてやり直してください！");
            return;
        }
        Util.message(sender, "&aあなたのパスワードはリセットされました！");
        Util.message(sender, "&aログイン後、ユーザーCPからパスワードを変更できます！");
        Util.message(sender, "&a新パスワード:&2 " + newPass);
    }
}
