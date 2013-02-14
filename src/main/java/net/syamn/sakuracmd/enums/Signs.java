/**
 * SakuraCmd - Package: net.syamn.sakuracmd.signs
 * Created: 2013/02/13 18:26:57
 */
package net.syamn.sakuracmd.enums;

import net.syamn.sakuracmd.signs.BaseSign;
import net.syamn.sakuracmd.signs.HardEndSign;

/**
 * Signs (Signs.java)
 * @author syam(syamn)
 */
public enum Signs {
    HARD_END (new HardEndSign()),
    ;

    private final BaseSign sign;

    private Signs(final BaseSign sign){
        this.sign = sign;
    }

    public BaseSign getSign(){
        return sign;
    }

    public String getSignName(){
        return sign.getSignName();
    }
    public String getSuccessName(){
        return sign.getSuccessName();
    }
}
