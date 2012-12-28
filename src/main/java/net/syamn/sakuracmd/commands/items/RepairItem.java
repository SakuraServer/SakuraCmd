/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.items
 * Created: 2012/12/29 8:01:37
 */
package net.syamn.sakuracmd.commands.items;

import net.syamn.sakuracmd.Perms;
import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.utils.ItemUtil;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * RepairItem (RepairItem.java)
 * @author syam(syamn)
 */
public class RepairItem extends BaseCommand{
    public RepairItem(){
        bePlayer = false;
        name = "repairitem";
        perm = Perms.REPAIRALL;
        argLength = 0;
        usage = "[player] <- repair your item";
    }
    
    public void execute() throws CommandException{
        if (args.size() == 0 && !isPlayer){
            throw new CommandException("&cプレイヤー名を指定してください！");
        }
        
        final Player target = (args.size() > 0) ? Bukkit.getPlayer(args.get(0)) : player;
        if (target == null || !target.isOnline()){
            throw new CommandException("&cプレイヤーが見つかりません！");
        }
        
        final ItemStack item = target.getItemInHand();
        final String iname = (item == null) ? "null" : item.getType().name();
        
        if (item != null && ItemUtil.repairable(item.getTypeId())){
            item.setDurability((short) 0);
        }else{
            throw new CommandException("&cアイテム " + iname + " は修復できません！");
        }
        
        if (!sender.equals(target)){
            Util.message(sender, "&a" + target.getName() + " のアイテム " + iname + " が修復されました");
        }
        Util.message(target, "&aあなたのアイテム " + iname + " が修復されました");
    }
}
