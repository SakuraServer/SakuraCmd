/**
 * SakurtaCmd - Package: net.syamn.sakuracmd.feature
 * Created: 2013/02/06 12:53:42
 */
package net.syamn.sakuracmd.feature;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.sakuracmd.manager.Worlds;
import net.syamn.sakuracmd.player.PlayerManager;
import net.syamn.sakuracmd.utils.plugin.SakuraCmdUtil;
import net.syamn.utils.ItemUtil;
import net.syamn.utils.LogUtil;
import net.syamn.utils.Util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.PlayerSession;
import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.SessionManager;
import uk.co.oliwali.HawkEye.callbacks.BaseCallback;
import uk.co.oliwali.HawkEye.database.SearchQuery;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.HawkEyeAPI;

/**
 * HawkEyeSearcher (HawkEyeSearcher.java)
 * @author syam(syamn)
 */
public class HawkEyeSearcher implements Runnable{
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static Map<String, Integer> lookupHistory = new HashMap<String, Integer>();
    private static boolean using = false;

    private SakuraCmd plugin;
    private CommandSender sender;

    public String targetName;
    public int totals = 0;
    public List<Integer> broken = new ArrayList<Integer>(0);
    public List<Integer> blockIds;
    public String[] searchWorlds;

    public int searchTime;
    private boolean done = false;
    private boolean error = false;
    private boolean command;

    public static void dispose(){
        lookupHistory.clear();
        using = false;
    }
    public static boolean isUsing(){
        return using;
    }

    public HawkEyeSearcher(final SakuraCmd plugin, CommandSender sender, String target, final List<Integer> blockIds, int time, String[] searchWorlds, boolean command){
        this.plugin = plugin;

        this.command = command;

        this.targetName = target;
        this.blockIds = blockIds;
        this.sender = sender;
        this.searchTime = time;
        this.searchWorlds = searchWorlds;
    }

    public HawkEyeSearcher(final SakuraCmd plugin, CommandSender sender, String target, int time, boolean command){
        this (plugin, sender, target,
                new ArrayList<Integer>(6) {{
                    add(15); add(14); add(21); add(48); add(129); add(56); // default blocks
                }},
                time,
                Worlds.getNormalWorlds().toArray(new String[0]), // default worlds
                command);
    }

    private void printData(){
        if (sender == null){
            return;
        }

        String dname = targetName;
        Player p = Bukkit.getPlayerExact(targetName);
        if (p != null && p.isOnline()){
            dname = PlayerManager.getPlayer(p).getName();
        }

        Util.message(sender, "&f---------- &aX-ray Check for " + dname + " &f----------");
        Util.message(sender, "&aTotal blocks broken in last " + searchTime + " hours: " + totals);

        if (totals == 0){
            return;
        }

        for (int i = 0; i < this.blockIds.size(); i++){
            String prefix = getColor(blockIds.get(i));
            String name = ItemUtil.getReadableName(blockIds.get(i));
            Util.message(sender, prefix + name + ": " + Util.getPercent(broken.get(i), totals, 5) + "% (" + broken.get(i) + ")");
        }
    }
    private void printWarn(){
        if (totals == 0){
            return;
        }
        for (int i = 0; i < this.blockIds.size(); i++){
            // checks only diamond-ore(56)
            if (blockIds.get(i) != 56){
                continue;
            }

            double perc = Util.getPercent(broken.get(i), totals, 5);
            String name = ItemUtil.getReadableName(blockIds.get(i));
            if (perc > 1.5D && broken.get(i) > 5){
                String dname = targetName;
                Player p = Bukkit.getPlayerExact(targetName);
                if (p != null && p.isOnline()){
                    dname = PlayerManager.getPlayer(p).getName();
                }
                SakuraCmdUtil.sendlog(dname + " &cが &6" + name + " &cを &6" + perc +"% &cの比率で採掘しました");
                LogUtil.warning(targetName + " has a break percentage of " + perc + "% for " + name);
            }
            break;
        }
    }

    private String getColor(int blockId){
        switch (Material.getMaterial(blockId)){
            case DIAMOND_ORE:
                return "&b";
            case IRON_ORE:
                return "&7";
            case GOLD_ORE:
                return "&6";
            case LAPIS_ORE:
                return "&9";
            case MOSSY_COBBLESTONE:
                return "&2";
            case EMERALD_ORE:
                return "&a";
            default:
                return "&7";
        }
    }

    @Override
    public void run(){
        synchronized (this){
            if (using){
                return;
            }
            using = true;
        }
        try{
            search();

            while (!this.done){
                try{
                    wait(50L);
                } catch (InterruptedException ignore) {}
            }

            if (!error){
                if (command){
                    printData();
                }else{
                    printWarn();
                }
            }
        }finally{
            using = false;
        }
    }

    private boolean search() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis() - this.searchTime * 3600000);

        SearchParser parser = new SearchParser();
        parser.players = Arrays.asList(new String[]{ this.targetName });
        parser.actions = Arrays.asList(new DataType[]{ DataType.BLOCK_BREAK });
        parser.worlds = this.searchWorlds;
        parser.dateFrom = format.format(cal.getTime());

        ExCallback callback = new ExCallback(this.sender, this.blockIds);

        HawkEyeAPI.performSearch(callback, parser, SearchQuery.SearchDir.DESC);

        while (!callback.complete){
            synchronized (this){
                try{
                    wait(50L);
                }catch (InterruptedException ignore){}
            }
        }

        this.totals = callback.total;
        this.broken = callback.broken;
        this.error = callback.error;

        done = true;
        return true;
    }

    public class ExCallback extends BaseCallback{
        private CommandSender sender;
        private PlayerSession session;

        private boolean complete = false;
        private boolean error = false;

        private int total = 0;
        private List<Integer> blocks;
        private List<Integer> broken = new ArrayList<Integer>(0);

        public ExCallback(CommandSender sender, List<Integer> blockIds){
            this.blocks = new ArrayList<Integer>(blockIds);
            this.sender = sender;

            for (int i = 0; i < blocks.size(); i++){
                broken.add(0);
            }
        }

        @Override
        public void error(SearchQuery.SearchError error, String message) {
            try{
                this.error = true;
                if (sender != null && sender instanceof Player){
                    Util.message(sender, "&cHawkeye Error: &f" + error.name() + ": " + message);
                }else{
                    LogUtil.warning("Hawkeye Error: " + error.name() + " " + message);
                }
            }finally{
                complete = true;
            }
        }

        @Override
        public void execute(){
            try{
                session = SessionManager.getSession(sender);
                session.setSearchResults(results);

                String data = null;
                int split;
                int blockId;

                for (final DataEntry entry : results){
                    data = entry.getStringData();
                    split = Integer.valueOf(data.lastIndexOf(":"));

                    if (split > 0){
                        blockId = Material.getMaterial(data.substring(0, split)).getId();
                    }else{
                        blockId = Material.getMaterial(data).getId();
                    }

                    total++;

                    for (int i = 0; i < blocks.size(); i++){
                        if (blockId == blocks.get(i).intValue()){
                            broken.set(i, broken.get(i) + 1);
                        }
                    }
                }
            }finally{
                complete = true;
            }
        }
    }
}
