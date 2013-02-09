/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.db
 * Created: 2013/01/19 17:11:44
 */
package net.syamn.sakuracmd.commands.db;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.sakuracmd.player.PlayerData;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.sakuracmd.player.SakuraPlayer;
import net.syamn.sakuracmd.storage.Database;
import net.syamn.utils.StrUtil;
import net.syamn.utils.TimeUtil;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * MailCommand (MailCommand.java)
 * @author syam(syamn)
 */
public class MailCommand extends BaseCommand{
    public MailCommand(){
        bePlayer = true;
        name = "mail";
        perm = Perms.MAIL;
        argLength = 1;
        usage = "[list/#ID/send [name] [text]] <- mail related commands";
    }

    Database db = null;

    @Override
    public void execute() throws CommandException{
        db = Database.getInstance();
        if (db == null || !db.isConnected()){
            throw new CommandException("&c現在データベースと接続されていません！");
        }

        final String action = args.remove(0);

        if (action.equalsIgnoreCase("send")){
            // send
            send();
        }
        else if (action.equalsIgnoreCase("list")){
            // list
            list();
        }
        else if (StrUtil.isInteger(action)){
            // readID
            readID(Integer.valueOf(action));
        }
        else{
            // undefined sub-command
            throw new CommandException("&c不明なサブコマンドです！ /mail [list/#ID/send [name] [text]]");
        }
    }

    private void send() throws CommandException{
        if (args.size() < 2){
            throw new CommandException("&c引数が足りません！ /mail send [name] [message]");
        }

        // get to data
        final String toName = args.remove(0);
        final PlayerData toData  = PlayerManager.getData(toName);
        if (toData == null || toData.getPlayerID() <= 0){
            throw new CommandException("&cプレイヤー " + toName + " が見つかりません！");
        }
        final int toId = toData.getPlayerID();

        // get from data
        final SakuraPlayer sp = PlayerManager.getPlayer(player);
        final int fromId = sp.getData().getPlayerID();

        if (toId == fromId){ // self check
            throw new CommandException("&c自分には送信できません！");
        }

        // check and update
        final String body = StrUtil.join(args, " ");
        if (body.length() > 2000){
            throw new CommandException("&c本文が2000文字を超えています！");
        }
        final String ip = player.getAddress().getAddress().getHostAddress();
        final int date = TimeUtil.getCurrentUnixSec().intValue();

        final String query =
                "INSERT INTO `mail_data` (`author_id`, `author_ip`, `to_id`, `date`, `msg_text`, `ingame`) "
                        + "VALUES (?, ?, ?, ?, ?, ?)";
        final boolean res = db.write(query, fromId, ip, toId, date, body, 1);

        if (!res){
            throw new CommandException("&c送信に失敗しました！時間を置いてやり直してください！");
        }

        Util.message(sender, "&a" + toData.getPlayerName() + " へメールを送信しました！");

        // notify
        final Player tPlayer = Bukkit.getPlayerExact(toData.getPlayerName());
        if (tPlayer != null && tPlayer.isOnline()){
            Util.message(tPlayer, sp.getName() + " &aがあなたにメールを送信しました！ &7/mail list &aで確認できます！");
        }

        // send notify mail via web api
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable(){
            @Override
            public void run(){
                try{ new URL("http://127.0.0.1/api/notify.php?type=mail&toID=" + toId + "&fromName=" + player.getName()).getContent(); }
                catch (Exception ignore){}
            }
        });
        /*
        URLConnection con = url.openConnection();
        con.setDoOutput(true);

        OutputStream os = con.getOutputStream();
        String post = "type=mail&toID=1&fromName=syamn";
        PrintStream ps = new PrintStream(os);
        ps.print(post);
        ps.close();
        os.close();

        OutputStreamWriter ow = new OutputStreamWriter(con.getOutputStream());
        BufferedWriter bw = new BufferedWriter(ow);
        bw.write("type=mail" + "\n" + "toID=1" + "\n" + "fromName=syamn");
        bw.close();
        ow.close();
         */
    }

    private void list() throws CommandException{
        final SakuraPlayer sp = PlayerManager.getPlayer(player);
        int pid = sp.getData().getPlayerID();
        final String query = "SELECT `msg_id`, msg_from.player_name AS sender, `date`, `msg_text` "
                + "FROM `mail_data` LEFT JOIN `user_id` AS msg_from ON mail_data.author_id = msg_from.player_id "
                + "WHERE mail_data.to_id = ? AND mail_data.`read` = 0 AND mail_data.`deleted` = 0";
        HashMap<Integer, ArrayList<String>> records = db.read(query, pid);

        if (records == null || records.size() <= 0){
            throw new CommandException("&7あなたへの未読メッセージはありません");
        }

        String ts, line, body;
        for (ArrayList<String> row : records.values()){
            body = row.get(3).replace("\n", "");
            if (body.length() > 15){
                body = body.substring(0, 13) + "&7..";
            }
            ts = TimeUtil.getReadableTime(TimeUtil.getDateByUnixSeconds(Long.valueOf(row.get(2))));

            line = "&2#&a" + row.get(0) + "&f: &6" + row.get(1) + "&f: &7" + ts + "&f: " + body;
            Util.message(sender, line);
        }
    }

    private void readID(final int msgid) throws CommandException{
        final SakuraPlayer sp = PlayerManager.getPlayer(player);
        int pid = sp.getData().getPlayerID();

        String query = "SELECT msg_from.player_name AS sender, `date`, `msg_title`, `msg_text`, `read` "
                + "FROM `mail_data` LEFT JOIN `user_id` AS msg_from ON mail_data.author_id = msg_from.player_id "
                + "WHERE mail_data.msg_id = ? AND mail_data.to_id = ? AND mail_data.`deleted` = 0";
        HashMap<Integer, ArrayList<String>> records = db.read(query, msgid, pid);

        if (records == null || records.size() != 1){
            throw new CommandException("&7メッセージIDが不正です");
        }
        ArrayList<String> row = records.get(1);

        final String ts = TimeUtil.getReadableTime(TimeUtil.getDateByUnixSeconds(Long.valueOf(row.get(1))));
        final String title = (row.get(2) == null) ? "&7(なし)" : row.get(2);
        final String[] body = row.get(3).split("\n");

        Util.message(sender, "&6 === ID: " + msgid + " &6=== ");
        Util.message(sender, " &e　送信者: &a" + row.get(0));
        Util.message(sender, " &e受信日時: &a" + ts);
        Util.message(sender, " &e　　件名: &a" + title);
        for (String line : body){
            Util.message(sender, "&7 -> &f" + line);
        }

        // update read status
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable(){
            @Override
            public void run(){
                final String query = "UPDATE `mail_data` SET `read` = 1 WHERE `msg_id` = ?";
                db.write(query, msgid);
            }
        });
    }
}
