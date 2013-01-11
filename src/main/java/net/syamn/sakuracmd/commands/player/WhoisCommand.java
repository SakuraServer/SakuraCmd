/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.player
 * Created: 2013/01/08 16:40:25
 */
package net.syamn.sakuracmd.commands.player;

import net.syamn.sakuracmd.SCHelper;
import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.feature.GeoIP;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.sakuracmd.player.PlayerData;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.utils.TimeUtil;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * WhoisCommand (WhoisCommand.java)
 * @author syam(syamn)
 */
public class WhoisCommand extends BaseCommand{
    public WhoisCommand(){
        bePlayer = false;
        name = "whois";
        perm = Perms.WHOIS;
        argLength = 0;
        usage = "[player] <- check player information";
    }

    public void execute() throws CommandException{
        if (args.size() == 0 && !isPlayer){
            throw new CommandException("&cプレイヤー名を指定してください！");
        }
        
        final String targetName = (args.size() > 0) ? args.get(0).trim() : player.getName();
        final PlayerData data  = PlayerManager.getData(targetName);
        
        if (data == null){
            throw new CommandException("&cプレイヤー " + targetName + " が見つかりません！");
        }
        
        final Player target = Bukkit.getPlayerExact(targetName);
        final boolean self = (sender.equals(target));
        
        String str = null;
        
        // ***** header *****
        send("&b========== &2" + ((target != null) ? PlayerManager.getPlayer(target).getName() : targetName) + " &b==========");
        
        // Login
        str = getTimeStr(data.getLastConnection());
        send("&6Last Join&f: &a" + ((str == null) ? "なし" : str));
        
        // Quit
        str = getTimeStr(data.getLastDisconnect());
        send("&6Last Quit&f: &a" + ((str == null) ? "なし" : str));
        
        // Last IP
        str = data.getLastIP();
        send("&6Last IP  &f: &a" + ((str == null) ? "なし" : str));
        
        // Lookup IP
        if (SCHelper.getInstance().getConfig().getUseGeoIP() && str != null){
            str = GeoIP.getInstance().getGeoIpString(str);
            send("&6IP Location &f: &a" + ((str == null) ? "不明" : str));
        }
       
        // ****** online section *****
        if (target == null || !target.isOnline()){
            return;
        }
        
        // Gamemode
        send("&eCurrent Gamemode&f: &a" + target.getGameMode().name());
    }
    
    private String getTimeStr(long unixMillis){
        if (unixMillis <= 0) return null;
        return TimeUtil.getReadableTime(TimeUtil.getDateByUnixMillis(unixMillis));
    }
    
    private void send(final String line){
        Util.message(sender, line);
    }
}
