/**
 * SakuraCmd - Package: net.syamn.sakuracmd.storage
 * Created: 2013/01/13 1:42:44
 */
package net.syamn.sakuracmd.storage;

import net.syamn.sakuracmd.SakuraCmd;

/**
 * MySQLReconnect (MySQLReconnect.java)
 * @author syam(syamn)
 */
public class MySQLReconnect implements Runnable{
    private final SakuraCmd plugin;

    public MySQLReconnect(final SakuraCmd plugin){
        this.plugin = plugin;
    }

    @Override
    public void run(){
        if (!Database.isConnected()){
            Database.connect();
            if (Database.isConnected()){
                // TODO stuff
            }
        }
    }
}