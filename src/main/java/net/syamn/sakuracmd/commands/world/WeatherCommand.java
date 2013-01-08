/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.world
 * Created: 2013/01/06 23:18:00
 */
package net.syamn.sakuracmd.commands.world;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.manager.ServerManager;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.utils.StrUtil;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * WeatherCommand (WeatherCommand.java)
 * @author syam(syamn)
 */
public class WeatherCommand extends BaseCommand{
    public WeatherCommand(){
        bePlayer = false;
        name = "weather";
        perm = Perms.WEATHER;
        argLength = 0;
        usage = "[weather] <- modify world weather";
    }
    
    public void execute() throws CommandException{
        // get world
        World world = null;
        if (!isPlayer){
            if (args.size() < 2){
                throw new CommandException("&cワールド名と天候を指定してください！");
            }
            final String wname = args.remove(0);
            world = Bukkit.getWorld(wname);
            if (world == null){
                throw new CommandException("&cワールド " + wname + " が見つかりません！");
            }
        }else{
            world = player.getWorld();
        }
        
        // get weather type
        if (args.size() < 1){
            throw new CommandException("&c天候を指定してください！");
        }
        Weather weather = StrUtil.isMatches(Weather.values(), args.get(0));
        if (weather == null){
            throw new CommandException("&c天候には clear, rain のいずれかを指定してください！");
        }
        weather = (weather.name == null) ? weather.actual : weather;
        
        // get duration secs
        int secs = -1;
        if (args.size() > 1){
            if (StrUtil.isInteger(args.get(1))){
                secs = Integer.parseInt(args.get(1));
            }
        }
      
        
        // set weather
        if (weather.equals(Weather.CLEAR)){
            world.setStorm(false);
        }else if (weather.equals(Weather.RAIN)){
            world.setStorm(true);
        }
        if (secs > 0){
            world.setWeatherDuration(secs * 20); // in seconds -> ticks
        }
        
        // send messages
        
        final String wmsg = (!isPlayer || !(world.equals(player.getWorld()))) ? "&aワールド " + world.getName() + " " : "&aこのワールド";
        final String lenmsg = (secs > 0) ? secs + "秒間" : "";
        Util.message(sender, wmsg + "の天候を" + weather.name +"に" + lenmsg + "変更しました！");
    }
    
    private enum Weather{
        CLEAR ("晴れ"),
        SUN (Weather.CLEAR),
        RAIN ("雨"),
        //STORM ("嵐"),
        STORM (Weather.RAIN),
        ;
        
        String name = null;
        Weather actual = null;
        Weather(final String name){
            this.name = name;
        }
        Weather(final Weather actual){
            this.actual = actual;
        }
    }
}
