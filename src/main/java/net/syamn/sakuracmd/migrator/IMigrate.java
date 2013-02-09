/**
 * SakuraCmd - Package: net.syamn.sakuracmd.migrator
 * Created: 2013/01/08 19:03:24
 */
package net.syamn.sakuracmd.migrator;

/**
 * IMigrate (IMigrate.java)
 * @author syam(syamn)
 */
public interface IMigrate {
    public abstract void init();

    public abstract void importPlayerData();
    public abstract void importOtherFiles();
}
