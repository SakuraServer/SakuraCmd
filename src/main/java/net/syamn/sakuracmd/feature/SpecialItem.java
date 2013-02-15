/**
 * SakuraCmd - Package: net.syamn.sakuracmd.feature
 * Created: 2013/02/15 8:57:39
 */
package net.syamn.sakuracmd.feature;

import java.util.ArrayList;
import java.util.List;

import net.syamn.utils.StrUtil;
import net.syamn.utils.Util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * SpecialItem (SpecialItem.java)
 * @author syam(syamn)
 */
public class SpecialItem {
    //private static String header = "§aSpecialItem";
    private static String line = "§a-§b=§a-§b=§a-§b=§a-§b=§a-§b=§a-§b=§a-§b=§a-§b=§a-§b=§a-§b=§a-§b=§a-";
    private static String remainCount = "§3 残りの使用可能回数:§6 ";
    
    public static ItemStack createSpecialItem(final ItemStack is, final Type type, final int value){
        if (is == null || is.getType() == Material.AIR){
            throw new IllegalStateException("ItemStack must not be null");
        }
        
        final ItemMeta meta = is.getItemMeta();
        List<String> lores = new ArrayList<>();
        
        switch (type){
            case CRYSTAL:
                lores.add("&a右クリックした地点に");
                lores.add("&dクリスタル&aが出現します");
                if (value != 0){
                    lores.add(remainCount + value);
                }
                break;
        }
        
        int i = 0;
        for (final String line : lores){
            if (line != null){
                lores.set(i, Util.coloring(line));
            }
            i++;
        }
        
        lores.add(0, line);
        lores.add(line);
        
        meta.setDisplayName(type.getItemName());
        meta.setLore(lores);
        
        is.setItemMeta(meta);
        return is;
    }
    
    public static Type getSpecialItemType(final ItemStack is){
        if (is == null || is.getType() == Material.AIR){
            return null;
        }
        
        final String name = is.getItemMeta().getDisplayName();
        final List<String> lores = is.getItemMeta().getLore();
        if (name == null || lores == null || lores.size() <= 2){
            return null;
        }
        
        if (!lores.get(0).equals(line) || !lores.get(lores.size() - 1).equals(line)){
            return null;
        }
        
        for (final Type type : Type.values()){
            if (name.equals(type.getItemName()))
                return type;
        }
        return null;
    }
    
    public static int getRemainCount(final ItemStack is){
        if (is == null || is.getType() == Material.AIR){
            throw new IllegalArgumentException("ItemStack must not be null");
        }
        
        final List<String> lores = is.getItemMeta().getLore();
        if (lores == null || lores.size() <= 2){
            throw new IllegalArgumentException("ItemStack lores must not be null or illgeal size (<=2)");
        }
        
        for (final String line : lores){
            if (line.indexOf(remainCount) != -1){
                String number = line.replace(remainCount, "").trim();
                if (StrUtil.isInteger(number)){
                    return Integer.parseInt(number);
                }else{
                    throw new IllegalArgumentException(number + " is not a number");
                }
            }
        }
        
        return 1; // remain counts line not found, remains count is 1
    }
    
    public static ItemStack setRemainCount(final ItemStack is, final int remain){
        if (is == null || is.getType() == Material.AIR){
            throw new IllegalArgumentException("ItemStack must not be null");
        }
        
        final List<String> lores = is.getItemMeta().getLore();
        if (lores == null || lores.size() <= 2){
            throw new IllegalArgumentException("ItemStack lores must not be null or illgeal size (<=2)");
        }
        
        int i = 0;
        for (final String line : lores){
            if (line.indexOf(remainCount) != -1){
                if (remain != 0){
                    lores.set(i, remainCount + remain);
                }else{
                    lores.remove(i);
                }
                is.getItemMeta().setLore(lores);
                return is;
            }
            i++;
        }
        
        // remain counts line not found, add new one
        if (remain != 0){
            lores.add(lores.size() - 2, remainCount + remain);
        }
        is.getItemMeta().setLore(lores);
        return is;
    }
    
    public enum Type{
        CRYSTAL ("&aEnder&bCrystallizer"),
        ;
        
        private String name;
        private Type(final String name){
            this.name = Util.coloring(name);
        }
        public String getItemName(){
            return this.name;
        }
    }
}
