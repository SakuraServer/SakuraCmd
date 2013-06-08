/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.other
 * Created: 2013/01/30 0:30:46
 */
package net.syamn.sakuracmd.commands.other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.utils.LogUtil;
import net.syamn.utils.StrUtil;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
/**
 * WebCommand (WebCommand.java)
 * @author syam(syamn)
 */
public class WebCommand extends BaseCommand {
    public WebCommand() {
        bePlayer = false;
        name = "web";
        perm = null;
        argLength = 1;
        usage = "<- web link commands";
    }

    private static final String reset = "\u00A7r";

    @Override
    public void execute() throws CommandException {
        String action = args.remove(0);

        // web tploc [name] (world) [x] [y] [z] (yaw) (pitch) [message]
        if (action.equalsIgnoreCase("thorloc") && args.size() >= 4){
            plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                @Override public void run(){
                    thorloc();
                }
            }, 1L);
            return;
        }
        // web tploc [name] (world) [x] [y] [z] (yaw) (pitch) [message]
        else if (action.equalsIgnoreCase("tploc") && args.size() >= 7){
            plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                @Override public void run(){
                    tploc();
                }
            }, 1L);
            return;
        }        
        // web enchgive [name] [itemID] [enchID:lv]...
        else if(action.equalsIgnoreCase("enchgive") && args.size() >= 3){
            enchgive();
            return;
        }

        throw new CommandException("&cUndefined command!");
    }

    private void thorloc() {
        final String worldName = args.remove(0);
        final World world = Bukkit.getWorld(worldName);
        if (world == null){
            LogUtil.warning("ThorLoc aborted. World not found; " + worldName);
            return;
        }        
        double x = Double.parseDouble(args.remove(0)) + 0.5;
        double y = Double.parseDouble(args.remove(0));
        double z = Double.parseDouble(args.remove(0)) + 0.5;
        
        final Location loc = new Location(world, x, y, z);
        world.strikeLightning(loc);
        //world.strikeLightningEffect(loc);
        LogUtil.info("ThorLoc processed: " + StrUtil.getLocationString(loc, 0));
    }
    
    private void tploc() {
        final String targetName = args.remove(0);

        Player target = Bukkit.getPlayer(targetName);
        if (target == null || !target.isOnline()) {
            LogUtil.warning("Teleport aborted. Player offline: " + targetName);
            return;
        }

        // check location
        World world = target.getWorld();
        if (!StrUtil.isDouble(args.get(0))) {
            String wname = args.remove(0);
            world = Bukkit.getWorld(wname);
            if (world == null) {
                LogUtil.warning("Teleport aborted. World not found: " + wname);
                return; // World not found
            }
        }
        if (!StrUtil.isDouble(args.get(0)) || !StrUtil.isDouble(args.get(1)) || !StrUtil.isDouble(args.get(2))) {
            LogUtil.warning("Teleport aborted. Invalid location: " + args.get(0) + "," + args.get(1) + "," + args.get(2));
            return; // invalid location
        }

        double x = Double.parseDouble(args.remove(0));
        double y = Double.parseDouble(args.remove(0));
        double z = Double.parseDouble(args.remove(0));
        Location loc = new Location(world, x, y, z);

        // check yaw/pitch
        if (args.size() >= 2 && StrUtil.isFloat(args.get(0)) && StrUtil.isFloat(args.get(1))) {
            loc.setYaw(Float.valueOf(args.remove(0)));
            loc.setPitch(Float.valueOf(args.remove(0)));
        }

        String msg = (args.size() > 0) ? StrUtil.join(args, " ") : null;

        // do action
        target.teleport(loc, TeleportCause.PLUGIN);
        if (msg != null) Util.message(target, msg);
        LogUtil.info("Teleported player:" + target.getName() + " to " + loc.getWorld().getName() + ":" + loc.getX() + "," + loc.getY() + "," + loc.getZ());
    }

    // Stupid tetaemon...
    private void enchgive() {
        final String targetName = args.remove(0);

        Player target = Bukkit.getPlayer(targetName);
        if (target == null || !target.isOnline()) {
            LogUtil.warning("Enchgive abrted. Player offline: " + targetName);
            return;
        }
        
        final String itemData = args.remove(0);
        String[] datas = itemData.split(":");
        if (!StrUtil.isInteger(datas[0])){
            LogUtil.warning("Item is must be integer: " + datas[0]);
            return;
        }
        
        final int itemID = Integer.parseInt(datas[0]);
        short dur = -1;
        if (datas.length >= 2){
            if (!StrUtil.isShort(datas[1])){
                LogUtil.warning("Durability must be short: " + datas[1]);
                return;
            }
            dur = Short.parseShort(datas[1]);
        }
        
        HashMap<Enchantment, Integer> map = new HashMap<>();
        
        int enchId, enchLv;
        for (final String ench : args){
            datas = ench.split(":");
            if (datas.length != 2){
                LogUtil.warning("Enchant data must has ID and level: " + ench);
                return;
            }
            
            if (!StrUtil.isInteger(datas[0]) || !StrUtil.isInteger(datas[1])){
                LogUtil.warning("Enchant datas must be integer: " + ench);
                return;
            }
            
            enchId = Integer.parseInt(datas[0]);
            enchLv = Integer.parseInt(datas[1]);
            
            map.put(Enchantment.getById(enchId), enchLv);
        }
        
        ArrayList<String> str = new ArrayList<>();
        for (final Map.Entry<Enchantment, Integer> entry : map.entrySet()){
            str.add(entry.getKey().getName() + ":" + entry.getValue());
        }
        
        try{
            ItemStack is = new ItemStack(itemID);
            if (dur > 0){
                is.setDurability(dur);
            }
            is.addUnsafeEnchantments(map);

            PlayerInventory inv = target.getInventory();
            int num = getEmptySlotNum(inv);
            if (num == 0){
                LogUtil.warning("Target has not empty inventory slot");
                return;
            }
            
            inv.addItem(is);
            LogUtil.info("Give enchanted item " + is.getType().name() + " to " + target.getName() + " (" + StrUtil.join(str, ", ") + ")");
        }catch (Exception ex){
            LogUtil.warning("Error occred in enchgive: " + ex.getMessage());
        }
    }
    
    private int getEmptySlotNum(Inventory inv){
        Iterator<ItemStack> iter = inv.iterator();
        int i = 0;
        
        while(iter.hasNext()){
            if(iter.next() == null){
                i++;
            }
        }
        return i;
    }
    
    @Override
    public boolean permission(CommandSender sender) {
        if (sender instanceof Player){
            return false;
        }
        return true;
    }
}
