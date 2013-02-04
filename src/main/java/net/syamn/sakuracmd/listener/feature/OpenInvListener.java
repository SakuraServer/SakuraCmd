/**
 * SakuraCmd - Package: net.syamn.sakuracmd.listener.feature
 * Created: 2013/02/05 2:44:01
 */
package net.syamn.sakuracmd.listener.feature;

import java.util.Locale;

import net.syamn.utils.cb.inv.CBEnderChest;
import net.syamn.utils.cb.inv.CBPlayerInventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * OpenInvListener (OpenInvListener.java)
 */
public class OpenInvListener implements Listener{
    @EventHandler(priority = EventPriority.LOWEST) // LOWEST 
    public void onPlayerJoin(final PlayerJoinEvent event) {
        // Update player inventory
        CBPlayerInventory inventory = CBPlayerInventory.inventories.get(event.getPlayer().getName().toLowerCase(Locale.ENGLISH));
        if (inventory != null) {
            inventory.PlayerGoOnline(event.getPlayer());
        }
        
        // Update player enderchest
        CBEnderChest chest = CBEnderChest.chests.get(event.getPlayer().getName().toLowerCase(Locale.ENGLISH));
        if (chest != null) {
            chest.PlayerGoOnline(event.getPlayer());
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR) // MONITOR
    public void onPlayerQuit(final PlayerQuitEvent event) {
        // Update player inventory
        CBPlayerInventory inventory = CBPlayerInventory.inventories.get(event.getPlayer().getName().toLowerCase(Locale.ENGLISH));
        if (inventory != null) {
            inventory.PlayerGoOffline();
            inventory.InventoryRemovalCheck();
        }
        
        // Update player enderchest
        CBEnderChest chest = CBEnderChest.chests.get(event.getPlayer().getName().toLowerCase(Locale.ENGLISH));
        if (chest != null) {
            chest.PlayerGoOffline();
            chest.InventoryRemovalCheck();
        }
    }

}
