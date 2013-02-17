/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.player
 * Created: 2013/01/08 16:40:25
 */
package net.syamn.sakuracmd.commands.player;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import net.syamn.sakuracmd.SCHelper;
import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.feature.GeoIP;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.sakuracmd.player.PlayerData;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.sakuracmd.player.Power;
import net.syamn.utils.StrUtil;
import net.syamn.utils.TimeUtil;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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

    @Override
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
        String str2 = null;

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
        // lookup ip
        if (SCHelper.getInstance().getConfig().getUseGeoIP()){
            str2 = GeoIP.getInstance().getGeoIpString(str);
            send("&6Last IP  &f: &a" + ((str == null) ? "なし" : str) + ((str2 == null) ? "" : " &7(" + str2 + ")"));
        }else{
            send("&6Last IP  &f: &a" + ((str == null) ? "なし" : str));
        }

        // Powers
        List<Power> powers = getHasPowers(data);
        if (powers.size() > 0){
            send("&6Powers  &f: &a" + StrUtil.join(powers, "&7, &a"));
        }

        // ****** online section *****
        if (target == null || !target.isOnline()){
            return;
        }

        // Gamemode
        send("&eCurrent Gamemode&f: &a" + target.getGameMode().name());
        
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(3);
        Location loc = target.getLocation();
        send("&eLocation&f: &a" + StrUtil.getLocationString(loc, 1) + " &7(yaw=" + nf.format(loc.getYaw()) + ", pitch=" + nf.format(loc.getPitch()) + ")");
    }

    private String getTimeStr(long unixMillis){
        if (unixMillis <= 0) return null;
        return TimeUtil.getReadableTime(TimeUtil.getDateByUnixMillis(unixMillis));
    }

    private List<Power> getHasPowers(PlayerData data){
        ArrayList<Power> ret = new ArrayList<Power>();
        ret.clear();

        if (data == null){
            return ret;
        }

        for (final Power power : Power.values()){
            if (data.hasPower(power)){
                ret.add(power);
            }
        }

        return ret;
    }

    private void send(final String line){
        Util.message(sender, line);
    }
}
