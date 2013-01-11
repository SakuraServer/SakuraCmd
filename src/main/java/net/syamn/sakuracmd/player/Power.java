/**
 * SakuraCmd - Package: net.syamn.sakuracmd.player
 * Created: 2013/01/06 8:40:41
 */
package net.syamn.sakuracmd.player;

import java.util.Locale;

/**
 * Power (Power.java)
 * @author syam(syamn)
 */
public enum Power {
    INVISIBLE,
    GODMODE,
    NO_PICKUP,
    FLY,
    ;
    
    @Override
    public String toString(){
        return this.name().toLowerCase(Locale.ENGLISH);
    }
}
