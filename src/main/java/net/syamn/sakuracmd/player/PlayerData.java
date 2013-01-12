/**
 * SakuraCmd - Package: net.syamn.sakuracmd.player
 * Created: 2013/01/01 22:29:06
 */
package net.syamn.sakuracmd.player;

import java.io.File;
import java.util.ArrayList;

import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.utils.ParseUtil;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * PlayerData (PlayerData.java)
 * @author syam(syamn)
 */
public class PlayerData{
    private final static String SEPARATOR = System.getProperty("file.separator");
    private final static String dataDir = "userData";
    
    private final String playerName;
    private YamlConfiguration conf = new YamlConfiguration();
    private File file;
    private boolean saved = true;
    
    /* Transient status */
    // --> moved to SakuraPlayer
    
    /* Saves values*/
    // infos:
    private long lastConnection = 0L;
    private long lastDisconnect = 0L;
    private String lastIP = "";
    private int mofCount = 0;
    private Location lastLocation = null;
    
    // powers:
    private ArrayList<Power> powers = new ArrayList<Power>();
    private int flymodeTime = 0;
    
    /* ************************** */

    public PlayerData(final String playerName){
        this.playerName = playerName;
        String fileName = SakuraCmd.getInstance().getDataFolder() + SEPARATOR + dataDir + SEPARATOR + playerName + ".yml";
        load(new File(fileName));
    }
    private PlayerData(final String playerName, final File file){
        this.playerName = playerName;
        load(file);
    }
    
    private boolean load(final File file){
        this.file = file;
        if (!file.exists()){
            if (!file.getParentFile().exists()){
                file.getParentFile().mkdir();
            }
            if (!save(true)){
                throw new IllegalStateException("Could not create plaer data file: " + file.getPath());
            }
        }
        
        try {
            conf = new YamlConfiguration();
            conf.load(file);
            
            // load infos
            ConfigurationSection csi = conf.getConfigurationSection("infos");
            if (csi != null){
                this.lastConnection = csi.getLong("lastConnection", 0L);
                this.lastDisconnect = csi.getLong("lastDisconnect", 0L);
                this.lastIP = csi.getString("last-ip", "");
                this.mofCount = csi.getInt("mofCount", 0);
                this.lastLocation = ParseUtil.stringToLocation(csi.getString("lastLocation", ""));
            }
            
            // load powers
            ConfigurationSection csp = conf.getConfigurationSection("powers");
            powers.clear();
            if (csp != null){
                for (final Power p : Power.values()){
                    if (csp.getBoolean(p.toString(), false)){
                        this.powers.add(p);
                    }
                }
                if (hasPower(Power.FLYMODE)){
                    this.flymodeTime = csp.getInt("flymodeTime", 0);
                }
            }
            
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
    public boolean save(final boolean force){
        if (!saved || force){
            try{
                // save infos
                ConfigurationSection csi = conf.createSection("infos");
                csi.set("lastConnection", lastConnection);
                csi.set("lastDisconnect", lastDisconnect);
                csi.set("last-ip", lastIP);
                csi.set("mofCount", mofCount);
                if (lastLocation != null){
                    csi.set("lastLocation", ParseUtil.locationToString(lastLocation));
                }
                
                // save powers
                ConfigurationSection csp = conf.createSection("powers");
                for (final Power p : Power.values()){
                    if (this.hasPower(p)){
                        csp.set(p.toString(), true);
                    }
                }
                if (hasPower(Power.FLYMODE)){
                    csp.set("flymodeTime", this.flymodeTime);
                }
                
                conf.save(file);
            }catch (Exception ex){
                ex.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public String getPlayerName(){
        return this.playerName;
    }
    
    public static PlayerData getDataIfExists(final String playerName){
        final String fileName = SakuraCmd.getInstance().getDataFolder() + SEPARATOR + dataDir + SEPARATOR + playerName + ".yml";
        final File file = new File(fileName);
        return (file.exists()) ? new PlayerData(playerName, file) : null;
    }
    
    /* Getter/Setter */
    
    // infos:
    // lastConnection
    public void updateLastConnection(){
        lastConnection = System.currentTimeMillis();
        saved = false;
    }
    public long getLastConnection(){
        return lastConnection;
    }
    
    // lastDisconnect
    public void updateLastDisconnect(){
        lastDisconnect = System.currentTimeMillis();
        saved = false;
    }
    public long getLastDisconnect(){
        return lastDisconnect;
    }
    
    // last-ip
    public void setLastIP(final String ip){
        this.lastIP = ip;
        saved = false;
    }
    public String getLastIP(){
        return this.lastIP;
    }
    
    // mofCount
    public int addMofCount(){
        this.mofCount++;
        saved = false;
        return mofCount;
    }
    public int getMofCount(){
        return this.mofCount;
    }

    // lastLocation
    public void setLastLocation(final Location loc){
        this.lastLocation = loc;
        saved = false;
    }
    public Location getLastLocation(){
        return this.lastLocation;
    }
    
    // powers:
    public boolean hasPower(final Power power){
        return powers.contains(power);
    }
    public void addPower(final Power power/*, final int level*/){
        if (!hasPower(power)){
            powers.add(power);
            saved = false;
        }
    }
    public void removePower(final Power power){
        powers.remove(power);
        saved = false;
    }
    
    public void setFlymodeTime(final int time){
        this.flymodeTime = time;
        saved = false;
    }
    public int getFlymodeTime(){
        return this.flymodeTime;
    }
}
