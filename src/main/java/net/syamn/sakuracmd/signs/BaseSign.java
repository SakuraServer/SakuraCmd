/**
 * SakuraCmd - Package: net.syamn.sakuracmd.signs
 * Created: 2013/02/13 17:41:49
 */
package net.syamn.sakuracmd.signs;

import java.util.Locale;

import net.syamn.sakuracmd.SakuraCmd;
import net.syamn.sakuracmd.exception.SignException;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.utils.Util;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

/**
 * BaseSign (BaseSign.java)
 * @author syam(syamn)
 */
public class BaseSign {
    protected final String signName;
    
    public BaseSign(final String signName){
        this.signName = signName;
    }
    
    // Create event handler
    public final boolean onSignCreate(final SakuraCmd plugin, final SignChangeEvent event){
        final Player player = event.getPlayer();
        final ISign sign = new EventSign(event);
        
        sign.setLine(0, getFailName());
        
        if (!Perms.SIGN_CREATE_PARENT.has(player, signName.toLowerCase(Locale.ENGLISH))){
            Util.message(player, "&cこの看板を設置する権限がありません！");
            return true;
        }
        
        try{
            final boolean ret = onSignCreate(player, sign, plugin);
            if (ret){
                sign.setLine(0, getSuccessName());
            }
            return ret;
        }
        catch (SignException ex){
            Util.message(player, ex.getMessage());
        }
        return true; // not cancel create event, show error sign
    }
    protected boolean onSignCreate(final Player player, final ISign sign, final SakuraCmd plugin) throws SignException{
        return true; // if returns false, or throws SignException, cancel sign create event
    }
    
    // Interact event handler
    public final boolean onSignInteract(final Player player, final Block block, final SakuraCmd plugin){
        final ISign sign = new BlockSign(block);
        
        if (!Perms.SIGN_USE_PARENT.has(player, signName.toLowerCase(Locale.ENGLISH))){
            Util.message(player, "&cこの看板を使用する権限がありません！");
            return false;
        }
        
        try{
            onSignInteract(player, sign, plugin);
            return true;
        }
        catch (SignException ex){
            Util.message(player, ex.getMessage());
            return false;
        }
    }
    protected void onSignInteract(final Player player, final ISign sign, final SakuraCmd plugin) throws SignException{
    }
    
    // Break event handler
    public final boolean onSignBreak(final Player player, final Block block, final SakuraCmd plugin){
        final ISign sign = new BlockSign(block);
        
        if (!Perms.SIGN_BREAK_PARENT.has(player, signName.toLowerCase(Locale.ENGLISH))){
            Util.message(player, "&cこの看板を破壊する権限がありません！");
            return false;
        }
        
        try{
            return onSignBreak(player, sign, plugin);
        }
        catch (SignException ex){
            Util.message(player, ex.getMessage());
            return false;
        }
    }
    protected boolean onSignBreak(final Player player, final ISign sign, final SakuraCmd plugin) throws SignException{
        return true; // if returns false, or throws SignException, cancel BlockBreak event
    }
    
    public String getSignName(){
        return this.signName;
    }
    public String getTemplateName(){
        return "[" + this.signName + "]";
    }
    public String getSuccessName(){
        return "\u00a71[" + this.signName + "]";
    }
    public String getFailName(){
        return "\u00a74[" + this.signName + "]";
    }
    
    public static boolean isValidSign(final ISign sign){
        return sign.getLine(0).matches("§1\\[.*\\]");
    }
    
    @Deprecated
    public String getPlayerName(final Player player){
        return player.getName().substring(0, player.getName().length() > 13 ? 13 : player.getName().length());
    }
    
    public interface ISign{
        String getLine(final int index);
        void setLine(final int index, final String text);
        Block getBlock();
        void updateSign();
    }
    
    private class EventSign implements ISign{
        private final SignChangeEvent event;
        private final Block block;
        
        public EventSign(final SignChangeEvent event){
            this.event = event;
            this.block = event.getBlock();
        }
        
        @Override
        public final String getLine(final int index){
            return event.getLine(index);
        }
        @Override
        public final void setLine(final int index, final String text){
            event.setLine(index, text);
        }
        
        @Override
        public Block getBlock(){
            return this.block;
        }
        
        @Override
        public void updateSign(){ }
    }
    
    public static class BlockSign implements ISign{
        private final Block block;
        private final Sign sign;
        
        public BlockSign(final Block block){
            this.block = block;
            this.sign = (Sign)block.getState();
        }
        
        @Override
        public final String getLine(final int index){
            return sign.getLine(index);
        }
        @Override
        public final void setLine(final int index, final String text){
            sign.setLine(index, text);
        }
        
        @Override
        public Block getBlock(){
            return this.block;
        }
        
        @Override
        public void updateSign(){
            sign.update();
        }
    }
}
