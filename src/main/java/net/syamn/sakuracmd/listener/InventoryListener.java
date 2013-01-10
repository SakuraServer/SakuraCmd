/**
 * SakuraCmd - Package: net.syamn.sakuracmd.listener
 * Created: 2013/01/11 4:42:38
 */
package net.syamn.sakuracmd.listener;

import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.utils.LogUtil;
import net.syamn.utils.Util;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * InventoryListener (InventoryListener.java)
 * @author syam(syamn)
 */
public class InventoryListener implements Listener{
    private SakuraCmd plugin;
    public InventoryListener (final SakuraCmd plugin){
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onInventoryClick(final InventoryClickEvent event) {
        final ItemStack item = event.getCurrentItem();
        if (item == null) return;
        
        switch (item.getType()) {
            case MOB_SPAWNER:
            case MONSTER_EGG:
                boolean flag = false;
                for (final Enchantment e : item.getEnchantments().keySet()) {
                    item.removeEnchantment(e);
                    flag = true;
                }
                if (flag) {
                    Player player = (Player) event.getWhoClicked();
                    LogUtil.info("Player " + player.getName() + " clicked item has invalid enchant! Removed! item: " + item.getType().name());
                    Util.message(player, "&cクリックしたアイテムの不正なエンチャントを削除しました！");
                }
                break;
            default: break;
        }
    }
}
