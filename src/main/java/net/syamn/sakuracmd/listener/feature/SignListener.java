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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
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
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent event){
        final Block block = event.getClickedBlock();
        if (block == null){
            return;
        }
        
        final int id = block.getTypeId();
        if (id == Material.SIGN_POST.getId() || id == Material.WALL_SIGN.getId()){
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
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event){
        if (checkSignProtected(event.getBlock(), event.getPlayer())){
            event.setCancelled(true);
        }
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
