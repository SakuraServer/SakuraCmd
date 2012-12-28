/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.items
 * Created: 2012/12/29 6:11:48
 */
package net.syamn.sakuracmd.commands.items;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.syamn.sakuracmd.Perms;
import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.utils.Util;

/**
 * RepairAll (RepairAll.java)
 * @author syam(syamn)
 */
public class RepairAll extends BaseCommand{
    public RepairAll(){
        bePlayer = true;
        name = "repairall";
        perm = Perms.REPAIRALL;
        argLength = 0;
        usage = "repair your all items";
    }
    
    public void execute(){
        Player target = player;
        
        for (final ItemStack item : player.getInventory().getContents()){
            if (item != null && false){ // TODO add repairable check
                item.setDurability((short) 0);
            }
        }
        for (final ItemStack item : player.getInventory().getArmorContents()){
            if (item != null){
                item.setDurability((short) 0);
            }
        }
        
        Util.message(target, "&aあなたの全アイテムが修復されました");
    }
}
