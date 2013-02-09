package net.syamn.sakuracmd.serial.endreset;

import java.io.Serializable;

import net.syamn.utils.TimeUtil;

public class EndResetWorld implements Serializable {
    private static final long serialVersionUID = 4748861011063168140L;
    
    private final int hours;
    private long lastReset;

    /**
     * コンストラクタ
     * 
     * @param hours
     */
    public EndResetWorld(final int hours) {
        this.hours = hours;
        updateLastReset();
    }
    
    public int getInterval(){
        return this.hours;
    }
    
    public long getNextReset(){
        return this.lastReset + (this.hours * 60 * 60);
    }
    
    public void updateLastReset(){
        this.lastReset = TimeUtil.getCurrentUnixSec();
    }
}