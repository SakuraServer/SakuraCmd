/**
 * SakuraCmd - Package: net.syamn.sakuracmd
 * Created: 2013/01/12 20:00:17
 */
package net.syamn.sakuracmd;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * SakuraCmdTest (SakuraCmdTest.java)
 * @author syam(syamn)
 */
public class SakuraCmdTest {
    private transient List<Player> playerList;
    
    @Before
    public void setUp(){
        playerList = new ArrayList<Player>();
    }
    
    @Test
    public void addPlayerTest(){
        addPlayer("testPlayer1");
    }
    
    @After
    public void tearDown(){
        playerList.clear();
        System.gc();
    }
    
    private void addPlayer(String name){
        Player player = mock(Player.class);
        when(player.getName()).thenReturn(name);
        playerList.add(player);
    }
}
