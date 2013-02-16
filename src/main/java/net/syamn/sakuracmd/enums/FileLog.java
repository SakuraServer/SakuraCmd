/**
 * SakuraCmd - Package: net.syamn.sakuracmd.enums
 * Created: 2013/02/17 5:19:00
 */
package net.syamn.sakuracmd.enums;

import java.io.File;

import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.utils.LogUtil;

/**
 * FileLog (FileLog.java)
 * @author syam(syamn)
 */
public enum FileLog {
    HARD_END ("hard_end.log"),
    SPECITEM ("special_item.log"),
    GENITEM ("generate_item.log"),
    ;

    final private static String dirName = "log";

    private String file;
    private FileLog(final String file){
        this.file = file;
    }

    public String getFileName(){
        return this.file;
    }

    public void log(final String line){
        log(this.file, line);
    }

    public static void log(final String fileName, final String line){
        LogUtil.writeLog(SakuraCmd.getInstance().getDataFolder() + File.separator + dirName + File.separator + fileName, line);
    }
}
