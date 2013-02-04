/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.player
 * Created: 2013/02/05 1:42:15
 */
package net.syamn.sakuracmd.commands.player;

import java.util.HashMap;
import java.util.Locale;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.sakuracmd.utils.plugin.SakuraCmdUtil;
import net.syamn.utils.cb.inv.CBPlayerInventory;
import net.syamn.utils.cb.inv.OfflineInvManager;
import net.syamn.utils.exception.CommandException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * OpenInvCommand (OpenInvCommand.java)
 * @author syam(syamn)
 */
public class OpenInvCommand extends BaseCommand{
    private static HashMap<Player, String> historyMap = new HashMap<Player, String>();
    
    public OpenInvCommand(){
        bePlayer = true;
        name = "openinv";
        perm = Perms.OPENINV;
        argLength = 0;
        usage = "[name] <- toggle your invisible status";
    }
    
    public void execute() throws CommandException{
        // get opened history
        String history = historyMap.get(player);
        if (args.size() == 0 && (history == null || history == "")){
            throw new CommandException("&cプレイヤー名を指定してください！");
        }
        
        // choose target
        String targetName = null;
        if (args.size() > 0){
            targetName = args.get(0);
        }else{
            targetName = history;
        }
        
        boolean online = true;
        Player target = Bukkit.getPlayerExact(targetName);
        
        if (target == null){
            target = Bukkit.getPlayer(targetName);
        }
        if (target == null){
            target = new OfflineInvManager().loadPlayer(targetName);
            online = false;
        }
        if (target == null){
            throw new CommandException("&cプレイヤー " + targetName + " が見つかりません！");
        }
        
        // self check -- removed
        /*
        if (target.equals(player)){
            throw new CommandException("&c自分のインベントリは開けません！");
        }
        */
        
        // save history
        historyMap.put(player, target.getName());
        
        // create
        CBPlayerInventory inv = CBPlayerInventory.inventories.get(target.getName().toLowerCase(Locale.ENGLISH));
        if (inv == null){
            inv = new CBPlayerInventory(target, online);
            CBPlayerInventory.inventories.put(target.getName().toLowerCase(Locale.ENGLISH), inv);
        }
        
        // open
        player.openInventory(inv.getBukkitInventory());
        
        // logg
        SakuraCmdUtil.sendlog(sender, PlayerManager.getPlayer(player).getName() + "&6 が &7"
                        + (online ? (PlayerManager.getPlayer(target).getName()) : target.getName()) + " &6のインベントリを開きました");
    }
}
