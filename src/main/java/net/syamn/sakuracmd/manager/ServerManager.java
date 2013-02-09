/**
 * SakuraCmd - Package: net.syamn.sakuracmd.manager
 * Created: 2012/12/31 2:59:28
 */
package net.syamn.sakuracmd.manager;

import net.syamn.sakuracmd.SakuraCmd;

/**
 * ServerManager (ServerManager.java)
 * @author syam(syamn)
 */
public class ServerManager {
    private final SakuraCmd plugin;
    public ServerManager(final SakuraCmd plugin){
        this.plugin = plugin;
    }

    private boolean lockdown = false;

    public void setLockdown(boolean flag){
        this.lockdown = flag;
    }
    public boolean isLockdown(){
        return this.lockdown;
    }
}
