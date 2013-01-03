/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.server
 * Created: 2012/12/31 3:06:48
 */
package net.syamn.sakuracmd.commands.server;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.manager.ServerManager;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

/**
 * LockdownCommand (LockdownCommand.java)
 * @author syam(syamn)
 */
public class LockdownCommand extends BaseCommand{
    public LockdownCommand(){
        bePlayer = false;
        name = "lockdown";
        perm = Perms.LOCKDOWN;
        argLength = 0;
        usage = "<- lockdown this server";
    }
    
    public void execute() throws CommandException{
        ServerManager serv = plugin.getServerManager();
        if (!serv.isLockdown()){
            serv.setLockdown(true);
            Util.broadcastMessage("&cこのサーバは5秒後にロックダウンされます！");
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
                @Override
                public void run() {
                    for (final Player player : Bukkit.getOnlinePlayers()){
                        if (!Perms.LOCKDOWN_BYPASS.has(player)){
                            player.kickPlayer("サーバがロックダウンされました！");
                        }
                        Util.broadcastMessage("&aこのサーバはロックダウンされました！");
                    }
                }
            }, 5 * 20L);
        }
        else{
            serv.setLockdown(false);
            Util.broadcastMessage("&aこのサーバのロックダウンは解除されました！");
        }
    }
}
