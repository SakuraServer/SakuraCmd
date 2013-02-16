/**
 * SakuraCmd - Package: net.syamn.sakuracmd.signs
 * Created: 2013/02/16 17:16:14
 */
package net.syamn.sakuracmd.signs;

import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.sakuracmd.exception.SignException;
import net.syamn.utils.Util;

import org.bukkit.entity.Player;

/**
 * ProtectSign (ProtectSign.java)
 * @author syam(syamn)
 */
public class ProtectSign extends BaseSign{
    public ProtectSign() {
        super("Protect");
    }


    @Override
    protected boolean onSignCreate(final Player player, final ISign sign, final SakuraCmd plugin) throws SignException{
        String name = player.getName();
        if (name.length() > 15){
            name = name.substring(0, 15);
        }
        sign.setLine(1, name);
        return true;
    }

    @Override
    protected boolean onSignBreak(final Player player, final ISign sign, final SakuraCmd plugin) throws SignException{
        String name = player.getName();
        if (name.length() > 15){
            name = name.substring(0, 15);
        }

        if (sign.getLine(1).equals(name)){
            Util.message(player, "&aあなたの保護看板を壊しました！");
            return true;
        }else{
            Util.message(player, "&cこれはあなたが設置した看板ではありません！");
            return false;
        }
    }

    @Override
    protected void onSignInteract(final Player player, final ISign sign, final SakuraCmd plugin) throws SignException{
        final String name = sign.getLine(1);
        Util.message(player, "&6この看板は " + name + " に保護されています");
    }
}