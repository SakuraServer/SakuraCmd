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
    private final Database db;

    public MySQLReconnect(final SakuraCmd plugin, final Database db){
        this.plugin = plugin;
        this.db = db;
    }

    @Override
    public void run(){
        if (!db.isConnected()){
            Database.connect();
            if (db.isConnected()){
                // TODO stuff
            }
        }
    }
}