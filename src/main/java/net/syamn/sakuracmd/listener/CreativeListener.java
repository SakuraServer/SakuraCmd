/**
 * SakuraCmd - Package: net.syamn.sakuracmd.listener
 * Created: 2013/01/11 7:05:53
 */
package net.syamn.sakuracmd.listener;

import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.sakuracmd.manager.Worlds;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.utils.Util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * CreativeListener (CreativeListener.java)
 * @author syam(syamn)
 */
public class CreativeListener implements Listener{
    private SakuraCmd plugin;
    public CreativeListener (final SakuraCmd plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlaceEvent(final BlockPlaceEvent event) {
        final Block block = event.getBlock();
        if (!block.getWorld().getName().equals(Worlds.creative)) {
            return;
        }
        
        final Player player = event.getPlayer();
        
        // クリエイティブでのTNT設置制限
        if (block.getType() == Material.TNT && !Perms.BYPASS_CREATIVE_TNT.has(player)){
            Util.message(player, "&cこのワールドでTNTは設置できません");
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreatureSpawn(final CreatureSpawnEvent event) {
        final Entity ent = event.getEntity();
        if (ent.getWorld().getName().equals(Worlds.creative)) {
            switch (event.getSpawnReason()) {
                case SPAWNER:
                case SPAWNER_EGG:
                case CUSTOM:
                case SLIME_SPLIT:
                case LIGHTNING:
                case JOCKEY:
                    break;
                default:
                    event.setCancelled(true);
                    break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerItemHeldEvent(final PlayerItemHeldEvent event) {
        final Player player = event.getPlayer();
        if (!player.getWorld().getName().equals(Worlds.creative) || Perms.BYPASS_CREATIVE_ITEM.has(player)) {
            return;
        }

        final Inventory inv = player.getInventory();
        final ItemStack item = inv.getItem(event.getNewSlot());
        if (item == null) return;
        if (isNotAllowedItem(item.getType())) {
            // event.getPlayer().setItemInHand(null);
            inv.setItem(event.getNewSlot(), null);
            Util.message(player, "&cこのアイテムは使用できません！");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!player.getWorld().getName().equals(Worlds.creative) || Perms.BYPASS_CREATIVE_ITEM.has(player)) {
            return;
        }

        if (event.getMaterial() == null) return;
        if (isNotAllowedItem(event.getMaterial())) {
            event.setCancelled(true);
            event.setUseInteractedBlock(Result.DENY);
            event.setUseItemInHand(Result.DENY);
            player.setItemInHand(null);

            player.kickPlayer("Try to use banned item!");
        }
    }

    private boolean isNotAllowedItem(final Material mat) {
        if (mat == null) return false;

        switch (mat) {
            case EXP_BOTTLE:
            case SNOW_BALL:
            case MONSTER_EGG:
            case POTION:
            case FIREBALL:
            case ENDER_CHEST:
            case ENDER_PEARL:
            case EYE_OF_ENDER:
                return true;
            default:
                return false;
        }
    }
}