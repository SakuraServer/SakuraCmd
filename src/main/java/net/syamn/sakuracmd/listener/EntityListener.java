/**
 * SakuraCmd - Package: net.syamn.sakuracmd.listener
 * Created: 2013/01/06 17:52:27
 */
package net.syamn.sakuracmd.listener;

import net.syamn.sakuracmd.SakuraCmd;

import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

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
}
