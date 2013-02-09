/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.other
 * Created: 2013/02/09 13:53:18
 */
package net.syamn.sakuracmd.commands.other;

import java.util.List;
import java.util.Locale;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.sakuracmd.serial.endreset.EndResetWorld;
import net.syamn.sakuracmd.worker.EndResetWorker;
import net.syamn.sakuracmd.worker.EndResetWorker.RegenThread;
import net.syamn.utils.StrUtil;
import net.syamn.utils.Util;
import net.syamn.utils.exception.CommandException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

/**
 * EndResetCommand (EndResetCommand.java)
 * @author syam(syamn)
 */
public class EndResetCommand extends BaseCommand {
    public EndResetCommand() {
        bePlayer = false;
        name = "endreset";
        perm = Perms.END_RESET;
        argLength = 0;
        usage = "[args] <- endreset commands";
    }
    
    private EndResetWorker worker = null;
    
    @Override
    public void execute() throws CommandException {
        worker = EndResetWorker.getInstance();
        if (worker == null){
            throw new CommandException("&c現在ワーカーがオフラインのためコマンドを実行できません");
        }
        
        if (args.size() == 0){
            resetCurrentWorld();
            return;
        }
        
        if (StrUtil.isInteger(args.get(0))){
            worker.inactiveMin = Integer.parseInt(args.get(0));
            Util.message(sender, "&a設定した非アクティブ時間: " + worker.inactiveMin + " 分");
            worker.inactiveMin = worker.inactiveMin * 20 * 60;
            return;
        }
        
        String action = args.get(0).toLowerCase(Locale.ENGLISH);
        if (action.equals("force")){
            if (args.size() < 3){
                throw new CommandException("&c引数が足りません！ /endreset force add/remove world_name");
            }
            String sub = args.get(1).toLowerCase(Locale.ENGLISH);
            if (sub.equals("add")){
                if (args.size() < 4){
                    throw new CommandException("&c引数が足りません！ /endreset force add world_name hours");
                }
                if (!StrUtil.isInteger(args.get(3))){
                    throw new CommandException("&c数値ではありません: " + args.get(3));
                }
                worker.forceReset.put(args.get(2), new EndResetWorld(Integer.parseInt(args.get(3))));
                Util.message(sender, "&aワールド " + args.get(2) + " は" + args.get(3) + "時間毎にリセットされます");
            }
            else if (sub.equals("remove")){
                if (!worker.forceReset.containsKey(args.get(2))){
                    throw new CommandException("&c" + args.get(2) + " がリストに見つかりません");
                }
                worker.forceReset.remove(args.get(2));
                Util.message(sender, "&aワールド " + args.get(2) + " は指定された間隔でリセットされなくなりました");
            }
            else{
                throw new CommandException("&c不正なサブコマンドです /endreset add/remove");
            }
        }
        else if (action.equals("ignore")){
            if (args.size() < 2){
                throw new CommandException("&c引数が足りません！ /endreset ignore world_name");
            }
            String wname = args.get(1);
            if (worker.dontHandle.contains(wname)){
                worker.dontHandle.remove(wname);
                Util.message(sender, "&aワールド " + wname + " を除外リストから削除しました");
            }else{
                worker.dontHandle.add(wname);
                Util.message(sender, "&aワールド " + wname + " を除外リストに追加しました");
            }
        }
        else if (action.equals("amount")){
            if (args.size() < 3){
                throw new CommandException("&c引数が足りません！ /endreset amount world_name amount");
            }
            if (!StrUtil.isShort(args.get(2))){
                throw new CommandException("&c数値が不正です: " + args.get(2));
            }
            String wname = args.get(1);
            short amount = Short.parseShort(args.get(2));
            if (amount == 1){
                worker.dragonAmount.remove(wname);
            }else{
                worker.dragonAmount.put(wname, amount);
            }
            Util.message(sender, "&a" + wname + " でのドラゴン数を" + amount + "に設定しました");
        }
        else if (action.equals("list")){
            List<World> worlds = Bukkit.getServer().getWorlds();
            if (worlds.isEmpty()){
                throw new CommandException("&cワールドが見つかりません！");
            }
            
            StringBuilder sb = new StringBuilder();
            World world;
            boolean first = true;
            for (int i = 1; i < worlds.size(); i++){
                world = worlds.get(i);
                if (world != null && world.getEnvironment() == Environment.THE_END){
                    if (!first) sb.append(' ');
                    else first = false;
                    
                    sb.append(world.getName());
                }
            }
            sb.insert(0, ChatColor.LIGHT_PURPLE);
            Util.message(sender, sb.toString());
            return;
        }
        else{
            throw new CommandException("&c有効なアクションではありません (force/ignore/amount/list)");
        }
        worker.save = true;
    }
    
    private void resetCurrentWorld() throws CommandException{
        if (!(sender instanceof Player)){
            throw new CommandException("&cコンソールからは実行できません");
        }
        
        World world = player.getWorld();
        if (world.getEnvironment() != Environment.THE_END){
            throw new CommandException("&cここはエンドワールドではありません！");
        }
        
        String wname = world.getName();
        Util.broadcastMessage("&dワールド '&6" + wname + "&d' をリセットしています... (" + sender.getName() + ")");
        
        for (final Player p : world.getPlayers()){
            player.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation(), TeleportCause.PLUGIN);
            Util.message(p, "&d このワールドはリセットされます！");
        }
        
        long toRun = 1L;
        RegenThread regenThread = worker.new RegenThread(wname, world.getFullTime() + toRun);
        worker.pids.put(wname, Bukkit.getScheduler().runTaskLater(plugin, regenThread, toRun).getTaskId());
        worker.threads.put(wname, regenThread);
    }
    
    @Override
    public boolean permission(CommandSender sender) {
        return true;
    }
}
