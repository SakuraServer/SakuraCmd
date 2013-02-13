/**
 * SakuraCmd - Package: net.syamn.sakuracmd.enums
 * Created: 2013/02/13 19:52:09
 */
package net.syamn.sakuracmd.signs;

import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.utils.Util;

import org.bukkit.entity.Player;


/**
 * HardEndSign (HardEndSign.java)
 * @author syam(syamn)
 */
public class HardEndSign extends BaseSign{
    public HardEndSign() {
        super("HardEnd");
    }
    
    @Override
    protected void onSignInteract(final Player player, final ISign sign, final SakuraCmd plugin){
        Util.message(player, "&aTesting for new sign structure");
    }
}
