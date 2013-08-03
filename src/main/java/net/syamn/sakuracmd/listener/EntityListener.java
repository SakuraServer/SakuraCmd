/**
 * SakuraCmd - Package: net.syamn.sakuracmd.listener
 * Created: 2013/01/06 17:52:27
 */
package net.syamn.sakuracmd.listener;

import java.util.List;

import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.sakuracmd.manager.Worlds;
import net.syamn.sakuracmd.utils.plugin.SakuraCmdUtil;
import net.syamn.utils.LogUtil;
import net.syamn.utils.StrUtil;
import net.syamn.utils.Util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
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

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCrystalDamagedByEntity(final EntityDamageByEntityEvent event) {
        if (event.getEntityType() != EntityType.ENDER_CRYSTAL || 
                event.getEntity().getWorld().getEnvironment() == Environment.THE_END ||
                event.getDamage() <= 0){
            return;
        }
        
        final Entity ent = event.getEntity();
        final Player player = (event.getDamager() instanceof Player) ? (Player)event.getDamager() : null;
        
        final Block baseBlock = ent.getLocation().getBlock().getRelative(BlockFace.DOWN, 2);
        //LogUtil.info(player.getName() + " -> " + baseBlock.getType().name());//debug
        
        if (baseBlock != null && baseBlock.getType() == Material.OBSIDIAN){
            if (player != null) Util.message(player, "&cこのクリスタルは保護されています！");
            
            event.setCancelled(true);
            event.setDamage(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPetDamagedByPlayer(final EntityDamageByEntityEvent event){
        final Entity ent = event.getEntity();
        final Entity damager = event.getDamager();
        
        // Damager is human?
        if (!EntityType.PLAYER.equals(damager.getType()) || !(ent instanceof Tameable)){
            return;
        }
        
        // Entity is tameable? Entity is tamed?
        if (!(ent instanceof Tameable) || !((Tameable) ent).isTamed()){
            return;
        }
        
        // Set variables
        final String mobName = getReadableMobName(ent.getType());        
        final String tamerStr = ((OfflinePlayer) ((Tameable) ent).getOwner()).getName();
        final Player tamer = Bukkit.getServer().getPlayerExact(tamerStr);
        final Player damagerPlayer = (Player) damager;
        final String locStr = StrUtil.getLocationString(ent.getLocation(), 0);
        
        // Tamer is online?
        if (tamer != null && tamer.isOnline()){
            if (tamer.equals(damagerPlayer)) return; // has ownership?
            Util.message(tamer, "&b[情報] &e" + damagerPlayer.getName() + "&cがあなたの" + mobName + "&f(" + locStr + ")&cに攻撃しました！");            
        }
        
        Util.message(damagerPlayer, "&c[注意] &fこれは他人の" + mobName + "です。同意を得ていない殺傷は禁止です。");
        SakuraCmdUtil.sendlog("&6" + damagerPlayer.getName() + "&fが&6" + tamerStr + "&fの&6" + mobName + "&fに攻撃しました:" + locStr);
        
        // TODO: Add records to honeypet table, flatfile.
    }
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPetKilledByPlayer(final EntityDeathEvent event){
        final LivingEntity ent = event.getEntity();
        // 
        if (ent.getKiller() == null || !(ent instanceof Tameable) || !((Tameable) ent).isTamed()){
            return;
        }
        
        // has ownership?
        final Player killer = ent.getKiller();        
        final OfflinePlayer tamer = (OfflinePlayer) ((Tameable) event.getEntity()).getOwner();        
        if (killer.equals(tamer)) return;
        
        // Set variables
        final String mobName = getReadableMobName(ent.getType());
        final String locStr = StrUtil.getLocationString(ent.getLocation(), 0);
        Util.broadcastMessage("&c[警告] &e" + killer.getName() + "&cが&e" + tamer.getName() + "&cの" + mobName + "を倒しました！");
        
        // TODO: Add records to honeypet table, flatfile.
    }
    private String getReadableMobName(EntityType type){
        if(type == null) throw new IllegalArgumentException("EntityType type must not be null.");
        switch(type){
            case OCELOT: return "ネコ";
            case WOLF:   return "オオカミ";
            case HORSE:  return "馬";
            default:      return type.name();
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreatureSpawn(final CreatureSpawnEvent event) {
        final int size = event.getLocation().getWorld().getEntities().size();
        // 10,000 mobs limit per world
        if (size >= 10000){
            // cancell null reason
            if (event.getSpawnReason() == null){
                event.setCancelled(true);
                return;
            }
            
            switch (event.getSpawnReason()){
                case BUILD_IRONGOLEM:
                case BUILD_SNOWMAN:
                case BUILD_WITHER:
                case SPAWNER_EGG:
                case CUSTOM:
                    // special spawns are allows to 20,000
                    if (size >= 20000){
                        event.setCancelled(true);
                        return;
                    }
                    break;
                default:
                    event.setCancelled(true);
                    return;       
            }
        }
        
        // スポナー制限
        if (SpawnReason.SPAWNER.equals(event.getSpawnReason())) {
            final String wname = event.getLocation().getWorld().getName();
            
            // main worlds
            final Entity e = event.getEntity();
            if (Worlds.main_world.equals(wname) || Worlds.main_nether.equals(wname)){
                event.setCancelled(true);
                return;
            }
            
            // all worlds
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
            
            // resources
            if (EntityType.SKELETON.equals(e.getType()) && Worlds.isResource(wname)) {
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

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityExplode(final EntityExplodeEvent event){
        if (event.getEntity() != null){
            final Entity ent = event.getEntity();
            switch (ent.getType()){
                case ENDER_CRYSTAL:
                case MINECART_TNT:
                    if (ent.getWorld().getEnvironment() == Environment.THE_END){
                        return;
                    }
                    event.blockList().clear();
                    break;
                default: break;
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

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDeath(final EntityDeathEvent event){
        if (!isCheckEntity(event.getEntity()) || event.getDroppedExp() <= 0){
            return;
        }

        final Entity ent = event.getEntity();
        if (ent.hasMetadata("NPC")) return;
        
        final List<Entity> ents = ent.getNearbyEntities(0.75D, 1.75D, 0.75D);

        int i = 0;
        for (final Entity e : ents){
            if (isCheckEntity(e)) i++;
        }

        if (i >= 3){
            event.setDroppedExp(0);
            event.getDrops().clear();

            String locStr = StrUtil.getLocationString(ent.getLocation().getBlock());
            SakuraCmdUtil.sendlog("&6経験値トラップを検出: " + locStr);
            LogUtil.warning("ExpTrap detected at " + locStr);
        }
    }

    private boolean isCheckEntity(final Entity ent){
        if (ent == null) return false;
        switch (ent.getType()){
            case ZOMBIE:
            case SKELETON:
            case BLAZE:
                return true;
            default:
                return false;
        }
    }
}
