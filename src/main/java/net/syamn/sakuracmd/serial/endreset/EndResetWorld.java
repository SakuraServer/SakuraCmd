package net.syamn.sakuracmd.serial.endreset;

import java.io.Serializable;

public class EndResetWorld implements Serializable {
    private static final long serialVersionUID = 4748861011063168140L;
    
    public final long hours;
    public long lastReset;

    /**
     * コンストラクタ
     * 
     * @param hours
     */
    public EndResetWorld(final long hours) {
        this.hours = hours * 60 * 60;
        lastReset = System.currentTimeMillis() * 1000;
    }
}