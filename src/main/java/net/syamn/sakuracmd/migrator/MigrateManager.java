/**
 * SakuraCmd - Package: net.syamn.sakuracmd.migrator
 * Created: 2013/01/08 19:05:21
 */
package net.syamn.sakuracmd.migrator;

import java.io.File;

/**
 * MigrateManager (MigrateManager.java)
 * @author syam(syamn)
 */
public class MigrateManager {
    public static String getPluginsDirectory(final File file){
        final String path = file.getAbsolutePath();
        return path.substring(0, path.lastIndexOf(File.separator));
    }
}
