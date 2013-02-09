/**
 * SakuraCmd - Package: net.syamn.sakuracmd.listener.feature
 * Created: 2013/02/05 2:44:01
 */
package net.syamn.sakuracmd.listener.feature;

import java.util.Locale;

import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.sakuracmd.player.Power;
import net.syamn.sakuracmd.player.SakuraPlayer;
import net.syamn.utils.Util;
import net.syamn.utils.cb.inv.CBEnderChest;
import net.syamn.utils.cb.inv.CBPlayerInventory;
import net.syamn.utils.cb.inv.ChestUtil;

import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!Action.RIGHT_CLICK_BLOCK.equals(event.getAction()) || Result.DENY.equals(event.useInteractedBlock())){
            return;
        }

        final Player player = event.getPlayer();
        final SakuraPlayer sp = PlayerManager.getPlayer(player);

        if (event.getClickedBlock().getType() == org.bukkit.Material.ENDER_CHEST && sp.hasPower(Power.SPEC_CHEST)) {
            event.setCancelled(true);
            event.getPlayer().openInventory(event.getPlayer().getEnderChest());
        }
        else if (event.getClickedBlock().getState() instanceof Chest && sp.hasPower(Power.SPEC_CHEST)) {
            final int x = event.getClickedBlock().getX();
            final int y = event.getClickedBlock().getY();
            final int z = event.getClickedBlock().getZ();

            // open
            ChestUtil.openChestInventory(player, x, y, z, false);
            event.setCancelled(true);

            if (ChestUtil.isBlockedChest(player, x, y, z)) {
                Util.message(player, "&6ブロックチェストを開きました");
            }
        }
    }
}
