/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.player
 * Created: 2013/01/23 6:45:44
 */
package net.syamn.sakuracmd.commands.tp;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.sakuracmd.player.SakuraPlayer;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

/**
 * RideCommand (RideCommand.java)
 * @author syam(syamn)
 */
public class RideCommand extends BaseCommand{
    public RideCommand(){
        bePlayer = true;
        name = "ride";
        perm = Perms.RIDE;
        argLength = 0;
        usage = "[player] <- ride player ";
    }

    @Override
    public void execute() throws CommandException{
        // eject entity
        if (args.size() < 1){
            if (player.getVehicle() == null){
                throw new CommandException("&c対象のプレイヤー名を入力してください！");
            }else{
                player.getVehicle().eject();
                Util.message(sender, "&aエンティティから降りました！");
                return;
            }
        }

        final Player target = Bukkit.getPlayer(args.get(0));
        if (target == null || !target.isOnline()){
            throw new CommandException("&cプレイヤーが見つかりません！");
        }
        final SakuraPlayer sp = PlayerManager.getPlayer(target);

        final Entity cur = player.getVehicle();
        if (cur != null){
            if (cur instanceof Player && ((Player) cur).equals(target)){
                throw new CommandException("&c既に同じプレイヤーに乗っています！");
            }else{
                cur.eject();
            }
        }

        player.teleport(target, TeleportCause.COMMAND);
        if (target.getPassenger() != null){
            target.eject();
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable(){
            @Override public void run(){
                target.setPassenger(player);
                Util.message(sender, "&aプレイヤー " + sp.getName() + "&a に乗りました！");
            }
        }, 5L);
    }
}
