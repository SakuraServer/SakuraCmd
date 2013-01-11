/**
 * SakuraCmd - Package: net.syamn.sakuracmd.listener
 * Created: 2013/01/06 17:52:27
 */
package net.syamn.sakuracmd.listener;

import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.utils.Util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.PortalType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * EntityListener (EntityListener.java)
 * @author syam(syamn)
 */
public class EntityListener implements Listener{
    private SakuraCmd plugin;
    public EntityListener (final SakuraCmd plugin){
        this.plugin = plugin;
    }
    
    //@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDamage(final EntityDamageEvent event){
        // check GodMode -> moved to PlayerListener
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreatureSpawn(final CreatureSpawnEvent event) {
        // スポナー制限
        if (SpawnReason.SPAWNER.equals(event.getSpawnReason())) {
            final Entity e = event.getEntity();
            switch (e.getType()) {
                case PIG:
                case COW:
                case CHICKEN:
                case ZOMBIE:
                    event.setCancelled(true);
                    return;
                default:
                    break;
            }
            if (EntityType.ZOMBIE.equals(e.getType())) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void targetCancelOnGoldEquipment(final EntityTargetLivingEntityEvent event) {
        final Entity ent = event.getEntity();
        final LivingEntity targetEnt = event.getTarget();
        
        if (!targetEnt.getType().equals(EntityType.PLAYER)) {
            return;
        }
        if (ent.getLastDamageCause() != null && ent.getLastDamageCause().getCause().equals(DamageCause.ENTITY_ATTACK)) {
            return;
        }
        
        final Player player = (Player) targetEnt;
        //final ItemStack hold = player.getItemInHand(); // skip inHand item check
        final ItemStack[] armors = player.getInventory().getArmorContents();
        
        // アーマーが一つでも null または AIR ならリターン
        for (int i = 0; i < armors.length; ++i) {
            if (armors[i].getType() == null || armors[i].getType() == Material.AIR) {
                return;
            }
        }
        
        // アーマーが金装備ならターゲット拒否
        if (armors[0].getType() == Material.GOLD_BOOTS && armors[1].getType() == Material.GOLD_LEGGINGS && armors[2].getType() == Material.GOLD_CHESTPLATE && armors[3].getType() == Material.GOLD_HELMET) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityCreatePortal(final EntityCreatePortalEvent event) {
        if (event.getPortalType() == PortalType.NETHER && event.getEntityType() == EntityType.PLAYER) {
            Player player = (Player) event.getEntity();
            // メインワールド以外ならキャンセル
            if (player.getWorld() != Bukkit.getWorld("new")) {
                String loc = player.getWorld().getName() + ":" + player.getLocation().getBlockX() + "," + player.getLocation().getBlockY() + "," + player.getLocation().getBlockZ();
                //Actions.executeCommandOnConsole("kick " + player.getName() + " ネザーポータル設置違反 at " + loc);
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerFallDamage(final EntityDamageEvent event){
        if (!DamageCause.FALL.equals(event.getCause()) || !(event.getEntity() instanceof Player)){
            return;
        }
        final Player player = (Player) event.getEntity();
        
        Block check = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        
        if (check.getType() == Material.AIR && check.getRelative(BlockFace.UP).getType() == Material.AIR){
            check = check.getRelative(BlockFace.DOWN);
            //Actions.message(null, player, "Shifted down block");//debug
        }
        //Actions.message(null, player, "*: "+check.getType().name());//debug
        switch (check.getType()){
            case SPONGE:
                //Vector vect = new Vector(0D, 2.0D, 0D);
                //player.setVelocity(player.getVelocity().add(vect));
                event.setCancelled(true);
                event.setDamage(0);
                Util.message(player, "&a(◜▿‾ *)");
                // set vector
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
                    public void run(){
                        if (player != null){
                            player.setVelocity(player.getVelocity().add(new Vector(0D, 2.0D, 0D)));
                        }
                    }
                }, 0L);
                break;
            case LEAVES:
                event.setCancelled(true);
                event.setDamage(0);
                Util.message(player, "&2(◜▿‾ *)");
                break;
            default: break;
        }
    }
}
