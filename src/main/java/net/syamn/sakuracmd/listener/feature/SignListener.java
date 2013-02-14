/**
 * SakuraCmd - Package: net.syamn.sakuracmd.listener.feature
 * Created: 2013/02/13 18:30:43
 */
package net.syamn.sakuracmd.listener.feature;

import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.sakuracmd.enums.Signs;
import net.syamn.sakuracmd.signs.BaseSign;
import net.syamn.sakuracmd.utils.plugin.SignUtil;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * SignListener (SignListener.java)
 * @author syam(syamn)
 */
public class SignListener implements Listener{
    private final SakuraCmd plugin;

    private final static int WALL_SIGN = Material.WALL_SIGN.getId();
    private final static int SIGN_POST = Material.SIGN_POST.getId();

    public SignListener(final SakuraCmd plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSignChange(final SignChangeEvent event){
        for (final Signs signs : Signs.values()){
            final BaseSign sign = signs.getSign();
            if (event.getLine(0).equalsIgnoreCase(sign.getSuccessName())){
                event.setCancelled(true);
                return;
            }
            if (event.getLine(0).equalsIgnoreCase(sign.getTemplateName()) && !sign.onSignCreate(plugin, event)){
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(final PlayerInteractEvent event){
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK){
            return;
        }

        final Block block = event.getClickedBlock();
        if (block == null){
            return;
        }

        final int id = block.getTypeId();
        if (id == WALL_SIGN || id == SIGN_POST){
            final Sign s = (Sign)block.getState();
            for (Signs signs : Signs.values()){
                if (s.getLine(0).equalsIgnoreCase(signs.getSuccessName())){
                    signs.getSign().onSignInteract(event.getPlayer(), block, plugin);
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event){
        final int id = event.getBlockAgainst().getTypeId();

        if ((id == WALL_SIGN || id == SIGN_POST) && BaseSign.isValidSign(new BaseSign.BlockSign(event.getBlockAgainst()))){
            event.setCancelled(true);
            return;
        }
    }

    /* protect */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event){
        if (checkSignProtected(event.getBlock(), event.getPlayer())){
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBurn(final BlockBurnEvent event){
        if (checkSignProtected(event.getBlock())){
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockIgnite(final BlockIgniteEvent event){
        if (checkSignProtected(event.getBlock())){
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPistonExtend(final BlockPistonExtendEvent event){
        for (final Block block : event.getBlocks()){
            if (checkSignProtected(block)){
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPistonRetract(final BlockPistonRetractEvent event){
        if (event.isSticky() && checkSignProtected(event.getBlock())){
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityExplode(final EntityExplodeEvent event){
        for (final Block block : event.blockList()){
            if (checkSignProtected(block)){
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityChangeBlock(final EntityChangeBlockEvent event){
        if (checkSignProtected(event.getBlock())){
            event.setCancelled(true);
        }
    }

    private boolean checkSignProtected(final Block block){
        if (block == null) {
            return false;
        }

        final int id = block.getTypeId();
        if (((id == WALL_SIGN || id == SIGN_POST) && BaseSign.isValidSign(new BaseSign.BlockSign(block))) || SignUtil.checkIfBlockBreaksSigns(block)){
            return true;
        }
        return false;
    }

    private boolean checkSignProtected(final Block block, final Player player){
        if (SignUtil.checkIfBlockBreaksSigns(block)){
            return true;
        }

        final int id = block.getTypeId();
        if (id == SIGN_POST || id == WALL_SIGN){
            final Sign sign = (Sign)block.getState();
            for (final Signs signs : Signs.values()){
                if (sign.getLine(0).equalsIgnoreCase(signs.getSuccessName()) && !signs.getSign().onSignBreak(player, block, plugin)){
                    return true;
                }
            }
        }
        return false;
    }
}
