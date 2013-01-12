/**
 * SakuraCmd - Package: net.syamn.sakuracmd
 * Created: 2012/12/28 13:39:58
 */
package net.syamn.sakuracmd.permission;

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
    BACK ("tp.back"),
    
    // Server Commands
    LOCKDOWN ("server.lockdown"),
    LOCKDOWN_BYPASS ("server.lockdown.bypass"),
    
    // Player Commands
    AFK ("player.afk"),
    INVISIBLE ("player.invisible"),
    GOD ("player.god"),
    WHOIS ("player.whois"),
    GAMEMODE ("player.gamemode"),
    GAMEMODE_OTHER ("player.gamemode.other"),
    FLY ("player.fly"),
    FLY_OTHER ("player.fly.other"),
    FLYMODE ("player.flymode"),
    
    // World Commands
    WEATHER ("world.weather"),

    // Other / Admin Commands
    SAKURACMD ("admin.sakuracmd"),
    
     // Spec Permissions
    INV_CANSEE ("spec.cansee"),
    HIDE_GEOIP ("spec.hidegeoip"),
    TRUST ("spec.trust"),
    
    // Feature
    RIDE_PLAYER ("feature.ride"),
    RIDE_ALLENTITY ("feature.ride.all"),
    ICE_TO_WATER ("feature.icetowater"),
    
    // Bypass permissions
    BYPASS_CREATIVE_ITEM ("bypass.creativeitem"),
    
    // Tab color
    TAB_RED ("tab.red"),
    TAB_PURPLE ("tab.purple"),
    TAB_AQUA ("tab.aqua"),
    TAB_NONE ("tab.none"),
    TAB_GRAY ("tab.gray"),
    
    // Rei's Minimap
    REIS_DEFAULT ("reisminimap.default"),
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
        //return perm.hasPermission(node); // only support SuperPerms
        return PermissionManager.hasPerm(perm, this);
    }
    
    public String getNode(){
        return this.node;
    }
}