/**
 * SakuraCmd - Package: net.syamn.sakuracmd.utils.plugin
 * Created: 2013/01/10 3:49:05
 */
package net.syamn.sakuracmd.feature;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;
import com.maxmind.geoip.regionName;

import net.syamn.sakuracmd.SCHelper;
import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.utils.LogUtil;

/**
 * GeoIpUtil (GeoIpUtil.java)
 * @author syam(syamn)
 */
public class GeoIP {
    private static GeoIP instance = null;
    public static GeoIP getInstance(){
        return instance;
    }
    public static void dispose(){
        if (instance != null){
            instance.ls.close();
            instance.ls = null;
        }
        instance = null;
    }
    
    private static final String dbUrlCountry = "http://geolite.maxmind.com/download/geoip/database/GeoLiteCountry/GeoIP.dat.gz";
    private static final String dbUrlCity = "http://geolite.maxmind.com/download/geoip/database/GeoLiteCity.dat.gz";
    
    private SakuraCmd plugin;
    private File database;
    private LookupService ls;
    
    /**
     * コンストラクタ
     * @param plugin
     */
    public GeoIP(final SakuraCmd plugin){
        instance = this;
        this.plugin = plugin;
    }
    
    /**
     * 初期化
     */
    public void init(){
        final File parent = new File(plugin.getDataFolder(), "geoIp");
        if (!parent.exists()){
            parent.mkdirs();
        }
        
        final boolean isCity = SCHelper.getInstance().getConfig().getUseCityDB();
        database = new File(parent, (isCity) ? "GeoIPCity.dat" : "GeoIPCountry.dat");
        
        if (!database.exists()){
            // auto download db if missing
            downloadDatabase((isCity) ? dbUrlCity : dbUrlCountry, database);
        }
        
        try{
            ls = new LookupService(database);
        }catch(IOException ex){
            LogUtil.warning("Could not read GeoIP database: " + ex.getMessage());
        }
    }
    
    /**
     * プレイヤー接続時の処理
     * @param joined
     */
    public void onPlayerJoin(final Player joined){
        if (joined == null || joined.getAddress() == null || joined.getAddress().getAddress() == null){
            return;
        }
        final InetAddress addr = joined.getAddress().getAddress();
        
        final StringBuilder sb = new StringBuilder();
        // Show City name
        if (SCHelper.getInstance().getConfig().getUseCityDB()){
            final Location loc = ls.getLocation(addr);
            if (loc == null) return;
            
            if (loc.city != null){
                sb.append(loc.city).append(", ");
            }
            
            final String region = regionName.regionNameByCode(loc.countryCode, loc.region);
            if (region != null){
                sb.append(region).append(", ");
            }
            
            sb.append(loc.countryName);
        }
        // Only countroies name
        else{
            sb.append(ls.getCountry(addr).getName());
        }
        
        // send message to players
        for (final Player player : plugin.getServer().getOnlinePlayers()){
            if (!player.canSee(joined)){
                continue; // skip vanished player
            }
            player.sendMessage("&6プレイヤー " + joined.getName() + " の接続元: " + sb.toString());
        }
    }
    
    /**
     * LookupServiceを返す
     * @return
     */
    public LookupService getLookupService(){
        return this.ls;
    }
    
    /**
     * GeoIPデータベースのダウンロードを行う
     * @param url
     * @param fileName
     */
    private static void downloadDatabase(final String url, final File file){
        if (url == null || url.isEmpty()){
            LogUtil.warning("Could not download GeoIP database! Url is empty!");
            return;
        }
        
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        
        InputStream input = null;
        OutputStream output = null;
        try{
            LogUtil.info("Downloading GeoIP databases... : " + file.getName());
            
            final URL dlUrl = new URL(url);
            final URLConnection conn = dlUrl.openConnection();
            conn.setConnectTimeout(10000); // 10 secs timeout
            
            input = conn.getInputStream();
            if (url.endsWith(".gz")){
                input = new GZIPInputStream(input);
            }
            
            output = new FileOutputStream(file);
            final byte[] buff = new byte[2048];
            int len = input.read(buff);
            while (len >= 0){
                output.write(buff, 0, len);
                len = input.read(buff);
            }
            
            input.close();
            output.close();
        }
        catch(MalformedURLException ex){
            LogUtil.warning("Download error (Malformed URL): " + ex.getMessage());
        }
        catch(IOException ex){
            LogUtil.warning("Download error (Connection failed): " + ex.getMessage());
        }
        finally{
            if (output != null){
                try{ output.close(); }catch(Exception ignore){}
            }
            if (input != null){
                try{ input.close(); }catch(Exception ignore){}
            }
        }
        
    }
}
