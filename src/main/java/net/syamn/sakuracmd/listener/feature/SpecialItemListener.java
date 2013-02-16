/**
 * SakuraCmd - Package: net.syamn.sakuracmd.listener.feature
 * Created: 2013/02/16 4:35:30
 */
package net.syamn.sakuracmd.listener.feature;

import java.util.Locale;

import net.syamn.sakuracmd.feature.SpecialItem;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.utils.TimeUtil;
import net.syamn.utils.Util;
import net.syamn.utils.cb.PacketUtil;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * SpecialItemListener (SpecialItemListener.java)
 * @author syam(syamn)
 */
public class SpecialItemListener implements Listener{
    @EventHandler(priority = EventPriority.HIGH) // ignoreCancelled = true
    public void onPlayerRightClickWithItem(final PlayerInteractEvent event) {
        if (event.useItemInHand() == Result.DENY){
            return; // instead of ignoreCancelled = true
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK){
            return;
        }

        final Player player = event.getPlayer();
        ItemStack is = player.getItemInHand();
        if (is == null || is.getType().equals(Material.AIR) || player.getWorld().getEnvironment().equals(Environment.THE_END)){
            return; // return if player not item in hand, or player on end environment
        }
        
        SpecialItem.Type type = SpecialItem.getSpecialItemType(is);
        if (type == null){
            return;
        }
        
        final Block block = event.getClickedBlock();
        if (type.isRequireBlockClicked() && (block == null || block.getType() == Material.AIR)){
            return;
        }
        
        if (!Perms.SPECITEM_USE_PARENT.has(player, type.name().toLowerCase(Locale.ENGLISH))){
            Util.message(player, "&cこのアイテムを使用する権限がありません！");
            return;
        }
        
        // check expiration
        int expiration = SpecialItem.getExpiration(is);
        if (expiration > 0 && expiration <= TimeUtil.getCurrentUnixSec().intValue()){
            Util.message(player, "&cこのアイテムは使用期限を超過しています！");
            player.setItemInHand(SpecialItem.markAsExpired(is));
            return;
        }
        
               
        boolean success = false;
        switch(type){
            case CRYSTAL:
                success = useCrystalItem(player, is, block);
                break;
            default:
                Util.message(player, "&cこのアイテムはまだ未実装です！");
                break;
        }
        
        if (!success){
            event.setCancelled(true);
        }
        event.setUseInteractedBlock(Result.DENY);
        event.setUseItemInHand(Result.DENY);        
    }
    
    private boolean useCrystalItem(final Player player, ItemStack is, final Block block){
        Block check;
        for (int i = 1; i <= 3; i++){
            check = block.getRelative(BlockFace.UP, i);
            if (check != null && check.getType() != Material.AIR){
                Util.message(player, "&c上に十分なスペースがありません！");
                return false;
            }
        }
        
        // use item
        is = decrementRemainCount(is);
        player.setItemInHand(is);
        if (is == null){
            PacketUtil.playSound(player, "random.break", 0.3F, 0.0F);
        }
        
        final Location spawnLoc = block.getRelative(BlockFace.UP, 1).getLocation().add(0.5D, 0D, 0.5D);
        final Entity spawned  = spawnLoc.getWorld().spawn(spawnLoc, EntityType.ENDER_CRYSTAL.getEntityClass());
        
        Util.message(player, "&aエンダークリスタルを設置しました！");
        
        return true;
    }
    
    private ItemStack decrementRemainCount(ItemStack is){
        final int remain = SpecialItem.getRemainCount(is) - 1;
        if (remain > 0){
            is = SpecialItem.setRemainCount(is, remain);
        }else{
            is = null;
        }
        return is;
    }
}
