package jpacman.level;
import jpacman.npc.Ghost;
import jpacman.points.DefaultPointCalculator;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/*
碰撞测试
*/

public abstract class PlayerCollisionsTest {
    private Player player = mock(Player.class);
    private Ghost ghost = mock(Ghost.class);
    private Pellet pellet = mock(Pellet.class);
    private CollisionMap cmap;

    void setCmap(CollisionMap cmap){
        this.cmap = cmap;
    }

    @Test
    public void PlayerCollidesWithGhost(){
        cmap.collide(player, ghost);
        verify(player).setAlive(false);
        verifyZeroInteractions(ghost);
        verifyZeroInteractions(pellet);
    }

    @Test
    public void PlayerCollidesWithPellet(){
        when(pellet.getValue()).thenReturn(3);
        cmap.collide(player, pellet);
        verify(player).addPoints(3);
        verify(pellet).leaveSquare();
        verifyZeroInteractions(ghost);
    }

    @Test
    public void GhostCollidesWithPellet(){
        cmap.collide(ghost, pellet);
        verifyZeroInteractions(player);
        verifyZeroInteractions(ghost);
        verifyZeroInteractions(pellet);
    }

    @Test
    public void GhostCollidesWithPlayer(){
        cmap.collide(ghost, player);
        verify(player).setAlive(false);
        verifyZeroInteractions(ghost);
        verifyZeroInteractions(pellet);
    }

    @Test
    public void PelletCollidesWithGhost(){
        cmap.collide(pellet, ghost);
        verifyZeroInteractions(player);
        verifyZeroInteractions(ghost);
        verifyZeroInteractions(pellet);
    }

    @Test
    public void PelletCollidesWithPlayer(){
        when(pellet.getValue()).thenReturn(3);
        cmap.collide(pellet, player);
        verify(player).addPoints(3);
        verify(pellet).leaveSquare();
        verifyZeroInteractions(ghost);
    }
}
