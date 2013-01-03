/**
 * SakuraCmd - Package: net.syamn.sakuracmd
 * Created: 2012/12/28 13:39:58
 */
package net.syamn.sakuracmd.permissions;

import org.bukkit.permissions.Permissible;

/**
 * Perms (Perms.java)
 * @author syam(syamn)
 */
public enum Perms {
    /* 権限ノード */
    // Item Commands
    REPAIRITEM("item.repairitem"),
    REPAIRALL("item.repairall"),

    // Tp Commands
    TP ("tp.tp"),
    TPHERE ("tp.here"),
    
    // Server Commands
    LOCKDOWN ("server.lockdown"),
    LOCKDOWN_BYPASS ("server.lockdown.bypass"),
    
    // Player Commands
    AFK ("player.afk"),
    
    // Free Permissions

    // Spec Permissions

    // Admin Commands
    RELOAD("admin.reload"),

    ;

    // ノードヘッダー
    final String HEADER = "sakuracmd.";
    private String node;

    /**
     * コンストラクタ
     *
     * @param node
     *            権限ノード
     */
    Perms(final String node) {
        this.node = HEADER + node;
    }

    /**
     * 指定したプレイヤーが権限を持っているか
     *
     * @param player
     *            Permissible. Player, CommandSender etc
     * @return boolean
     */
    public boolean has(final Permissible perm) {
        if (perm == null)
            return false;
        return perm.hasPermission(node); // only support SuperPerms
    }
}