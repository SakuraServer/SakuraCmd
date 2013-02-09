/**
 * SakuraCmd - Package: net.syamn.sakuracmd.listener
 * Created: 2013/02/10 4:06:31
 */
package net.syamn.sakuracmd.listener;

import java.util.List;
import java.util.Random;

import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.sakuracmd.manager.Worlds;
import net.syamn.utils.Util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * HardEndListener (HardEndListener.java)
 * @author syam(syamn)
 */
public class HardEndListener implements Listener{
    private SakuraCmd plugin;
    public HardEndListener (final SakuraCmd plugin){
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent event) {
        final Entity ent = event.getEntity();
        
        if (!ent.getWorld().getName().equals(Worlds.hard_end)){
            return;
        }
        
        // ドラゴンへのダメージ
        if (ent.getType() == EntityType.ENDER_DRAGON || ent.getType() == EntityType.COMPLEX_PART) {
            if (event.getCause() == DamageCause.ENTITY_EXPLOSION || event.getCause() == DamageCause.BLOCK_EXPLOSION) {
                event.setCancelled(true);
                event.setDamage(0); // 爆発ダメージ無視
            }else{
                event.setDamage(event.getDamage() / 2); // ダメージ半減
            }
        }
        
        // ドラゴンがダメージを受けた
        if (ent.getType() == EntityType.ENDER_DRAGON) {
            final Location dragonLocation = ent.getLocation();
            final List<Player> inWorldPlayers = ent.getWorld().getPlayers();
            
            if (inWorldPlayers.isEmpty()){
                return;
            }
            
            // 毒グモ3匹ランダムターゲットで召還
            for (short i = 0; i < 3; i++) {
                CaveSpider caveSpider = (CaveSpider) ent.getWorld().spawnEntity(dragonLocation, EntityType.CAVE_SPIDER);
                caveSpider.setNoDamageTicks(200);
            }
            
            // スケルトン3匹ランダムターゲットで召還
            for (short i = 0; i < 2; i++) {
                Ghast ghast = (Ghast) ent.getWorld().spawnEntity(dragonLocation, EntityType.GHAST);
                ghast.setNoDamageTicks(200);
            }
            
            // スケルトン3匹ランダムターゲットで召還
            for (short i = 0; i < 5; i++) {
                Skeleton skeleton = (Skeleton) ent.getWorld().spawnEntity(dragonLocation, EntityType.SKELETON);
                skeleton.setNoDamageTicks(200);
            }
            
            // 帯電クリーパー3匹召還
            for (short i = 0; i < 3; i++) {
                Creeper creeper = (Creeper) ent.getWorld().spawnEntity(dragonLocation, EntityType.CREEPER);
                creeper.setNoDamageTicks(200);
                creeper.setPowered(true);
            }
            
            // ランダムプレイヤーの真上にTNTをスポーン
            for (short i = 0; i < 15; i++) {
                Random rnd = new Random(); // 乱数宣言
                if (inWorldPlayers.size() < 1) return;
                Location targetLoc = inWorldPlayers.get(rnd.nextInt(inWorldPlayers.size())).getLocation(); // ターゲットプレイヤー確定と座標取得
                Location tntloc = new Location(targetLoc.getWorld(), targetLoc.getX(), dragonLocation.getY(), targetLoc.getZ());
                ent.getWorld().spawn(tntloc, TNTPrimed.class);
            }
        }
        
        // TNT -> MOBダメージ無効
        if (!(ent instanceof Player) && event.getCause().equals(DamageCause.ENTITY_EXPLOSION)){
            event.setDamage(0);
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        if (!event.getEntity().getWorld().getName().equals(Worlds.hard_end)){
            return;
        }
        
        final Entity ent = event.getEntity();
        final Entity attacker = event.getDamager();
        
        // エンダークリスタルが矢によってダメージを受けた
        if (ent.getType() == EntityType.ENDER_CRYSTAL) {
            switch(attacker.getType()){
                case ARROW:
                case PRIMED_TNT:
                    event.setDamage(0);
                    event.setCancelled(true);
                    break;
            }
            
            if (attacker.getType() == EntityType.ARROW){
                final Projectile arrow = (Arrow) attacker;
                if (arrow.getShooter() instanceof Player) {
                    Util.message((Player) arrow.getShooter(), "&c矢ではクリスタルを破壊できません！");
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void addPlayerDamage(final EntityDamageByEntityEvent event) {
        if (!event.getEntity().getWorld().getName().equals(Worlds.hard_end)){
            return;
        }
        
        event.setDamage(event.getDamage() + 8);
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onProjectileHit(final ProjectileHitEvent event) {
        if (event.getEntity().getWorld().getName().equals(Worlds.hard_end)){
            if (event.getEntityType() == EntityType.ARROW && (((Arrow) event.getEntity()).getShooter().getType() == EntityType.SKELETON)) {
                event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), (float) 3.0, true);
                event.getEntity().remove(); // 規模1.0の炎有りの爆発をスケルトンの弓に与える
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getPlayer().getWorld().getName().equals(Worlds.hard_end)){
            if (event.getClickedBlock().getType() == Material.BED_BLOCK && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onItemSpawn(final ItemSpawnEvent event) {
        final Item item = event.getEntity();
        if (item.getWorld().getName().equals(Worlds.hard_end) && item.getItemStack().getType() == Material.ENDER_STONE) {
            event.setCancelled(true); // 負荷対策
        }
    }
}
