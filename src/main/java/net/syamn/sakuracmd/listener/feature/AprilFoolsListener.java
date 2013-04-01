/**
 * SakuraCmd - Package: net.syamn.sakuracmd.listener.feature
 * Created: 2013/04/01 0:47:20
 */
package net.syamn.sakuracmd.listener.feature;

import java.util.Random;
import net.syamn.utils.Util;

import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * AprilFoolsListener (AprilFoolsListener.java)
 * @author syam(syamn)
 */
public class AprilFoolsListener implements Listener{
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent event){
        final Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        Block clicked = event.getClickedBlock();
        if (clicked != null && clicked.getType() == Material.CAKE_BLOCK){
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 30 * 20, 0)); // 30 secs
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30 * 20, 0)); // 30 secs
            
            if (player.getHealth() >= 2){
                player.damage(1);
            }
            Util.message(player, "&a Happy April Fools!");
        }
    }
    
    Random rnd = new Random();
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event){
        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        
        if (player.getItemInHand() != null && player.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)){
            return;
        }
        
        if (block.getType() == Material.EMERALD_ORE || block.getType() == Material.DIAMOND_ORE || block.getType() == Material.GOLD_ORE){
            int ran = rnd.nextInt(35);
            EntityType type = null;
            
            if (ran == 0) type = EntityType.BAT;
            else if (ran == 1) type = EntityType.CHICKEN;
            else if (ran == 2) type = EntityType.MUSHROOM_COW;
            else if (ran == 3) type = EntityType.OCELOT;
            else if (ran == 4) type = EntityType.SHEEP;
            else if (ran == 5) type = EntityType.PIG;
            else if (ran == 6) type = EntityType.SQUID;
            else if (ran == 7) type = EntityType.SNOWMAN;
            else if (ran == 8) type = EntityType.WOLF;
            
            else if (ran == 9) type = EntityType.ZOMBIE;
            else if (ran == 10) type = EntityType.SILVERFISH;
            else if (ran == 11) type = EntityType.SLIME;
            
            if (type == null) return;
            block.getWorld().createExplosion(block.getLocation(), (float) 0.0, false);
            
            Entity ent = block.getWorld().spawnEntity(block.getLocation(), type);
            if (ent instanceof LivingEntity){
                LivingEntity lent = (LivingEntity) ent;
                lent.setHealth(lent.getMaxHealth());
                
                lent.setCustomName(player.getName());
                lent.setCustomNameVisible(true);
                
                player.setHealth(player.getMaxHealth());
            }
            Util.message(player, "&a Its your " + type.getName() + "! Happy April Fools!");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileHit(final ProjectileHitEvent event){
        final Projectile ent = event.getEntity();
        if (ent.getShooter() == null || !(ent.getShooter() instanceof Player)){
            return;
        }
        if (ent.getWorld().getEnvironment().equals(Environment.THE_END)){
            return;
        }
        
        final Player player = (Player) ent.getShooter();
        
        if (ent instanceof Snowball){
            player.teleport(ent, TeleportCause.PLUGIN);
            Util.message(player, "&a Teleported! Happy April Fools!");
        }
        else if (ent instanceof Arrow){
            if (rnd.nextInt(3) == 0){
                EntityType type = (rnd.nextBoolean()) ? EntityType.CREEPER : EntityType.ENDERMAN;
                
                ent.getWorld().createExplosion(ent.getLocation(), (float) 0.0, false);
                LivingEntity spawned = (LivingEntity) ent.getWorld().spawnEntity(ent.getLocation(), type);
                spawned.setHealth(spawned.getMaxHealth());
                if (spawned instanceof Creeper) ((Creeper) spawned).setPowered(rnd.nextBoolean());
                
                spawned.setCustomName(player.getName());
                spawned.setCustomNameVisible(true);
                
                Util.message(player, "&a Its your " + type.getName() + "! Happy April Fools!");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDeath(final EntityDeathEvent event){
        if (event.getEntity().isCustomNameVisible()){
            event.setDroppedExp(0);
            event.getDrops().clear();
        }
    }
}
