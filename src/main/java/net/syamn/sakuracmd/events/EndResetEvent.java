/**
 * SakuraCmd - Package: net.syamn.sakuracmd.events
 * Created: 2013/02/09 19:33:17
 */
package net.syamn.sakuracmd.events;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * EndResetEvent (EndResetEvent.java)
 * @author syam(syamn)
 */
public class EndResetEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    
    private World world;
    private short dragonAmount;
    private String message;
    
    public EndResetEvent(World world, short dragonAmount, String message){
        this.world = world;
        this.dragonAmount = dragonAmount;
        this.message = message;
    }
    
    public World getWorld(){
        return this.world;
    }
    
    public short getDragonAmount(){
        return this.dragonAmount;
    }
    
    public String getCompleteMessage(){
        return this.message;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
