/**
 * SakuraCmd - Package: net.syamn.sakuracmd.utils.plugin Created: 2013/02/13
 * 19:13:47
 */
package net.syamn.sakuracmd.utils.plugin;

import net.syamn.sakuracmd.signs.BaseSign;
import net.syamn.sakuracmd.signs.BaseSign.BlockSign;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * SignUtil (SignUtil.java)
 * 
 * @author syam(syamn)
 */
public class SignUtil {
    /**
     * そのブロックの破壊によって看板が破壊されるかどうか返す
     * @param block
     * @return
     */
    public static boolean checkIfBlockBreaksSigns(final Block block) {
        Block check = block.getRelative(BlockFace.UP);
        if (check.getType() == Material.SIGN_POST && BaseSign.isValidSign(new BlockSign(check))) {
            return true;
        }

        final BlockFace[] dirs = new BlockFace[] {
                BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST
        };

        for (final BlockFace blockFace : dirs) {
            check = block.getRelative(blockFace);

            if (check.getType() == Material.WALL_SIGN) {
                final org.bukkit.material.Sign signMat = (org.bukkit.material.Sign) check.getState().getData();
                if (signMat != null && signMat.getFacing() == blockFace && BaseSign.isValidSign(new BlockSign(check))) {
                    return true;
                }
            }
        }

        return false;
    }
}
