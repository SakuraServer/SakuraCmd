/**
 * SakuraCmd - Package: net.syamn.sakuracmd.commands.world
 * Created: 2013/05/04 20:30:34
 */
package net.syamn.sakuracmd.commands.world;

import java.util.ArrayList;
import java.util.List;

import net.syamn.sakuracmd.commands.BaseCommand;
import net.syamn.sakuracmd.permission.Perms;
import net.syamn.utils.exception.CommandException;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * EntityCommand (EntityCommand.java)
 * @author syam(syamn)
 */
public class EntityCommand extends BaseCommand{
    public EntityCommand(){
        bePlayer = true;
        name = "entity";
        perm = Perms.ENTITY;
        argLength = 1;
        usage = "[action] [type] <- modify world entities";
    }
    
    private World world;

    @Override
    public void execute() throws CommandException{
        world = player.getWorld();
        
        if ("clear".equalsIgnoreCase(args.get(0))){
            onClear();
        }
        else{
            throw new CommandException("&c有効なサブコマンドではありません(clear)");
        }
    }
    
    private void onClear() throws CommandException{
        final List<EntityType> removeType = new ArrayList<EntityType>();
        
        if ("item".equalsIgnoreCase(args.get(1))){
            removeType.add(EntityType.DROPPED_ITEM);
        }
        else{
            throw new CommandException("&cエンティティタイプが不正です(item)");
        }
        
        for (final Entity ent : world.getEntities()){
            if (ent != null && removeType.contains(ent)){
                ent.remove();
            }
        }
    }
}
