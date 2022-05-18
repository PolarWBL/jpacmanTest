package jpacman.level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import jpacman.board.Unit;
import jpacman.npc.Ghost;
import jpacman.npc.ghost.Blinky;
import jpacman.npc.ghost.Clyde;
import jpacman.npc.ghost.Inky;
import jpacman.npc.ghost.Pinky;
import jpacman.points.PointCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * 对游戏期间可能发生的所有碰撞进行测试。
 */
public class PlayerCollisionsTest {

    private static final List<Class<? extends Unit>> PLAYERS = List.of(Player.class);
    private static final List<Class<? extends Unit>> GHOSTS = Arrays.asList(
        Blinky.class,
        Clyde.class,
        Ghost.class,
        Inky.class,
        Pinky.class);
    private static final List<Class<? extends Unit>> COLLISION_LESS = List.of(Unit.class);
    private static final List<Class<? extends Unit>> PELLETS = List.of(Pellet.class);

    private PointCalculator pointCalculatorMock = null;
    private List<CollisionMap> collisionMapsToTest = null;

    @BeforeEach
    public void setUp() {
        pointCalculatorMock = Mockito.mock(PointCalculator.class);
        collisionMapsToTest = new ArrayList<>();
        collisionMapsToTest.add(Mockito.spy(new PlayerCollisions(pointCalculatorMock)));
        collisionMapsToTest.add(Mockito.spy(new DefaultPlayerInteractionMap(pointCalculatorMock)));
    }

    /**
     * 玩家和玩家之间的碰撞
     */
    @Test
    public void test_collide_playerOnPlayer() {
        for (Class<? extends Unit> playerClassA : PLAYERS) {
            Unit playerA = Mockito.mock(playerClassA);
            for (Class<? extends Unit> playerClassB : PLAYERS) {
                Unit playerB = Mockito.mock(playerClassB);
                for (CollisionMap collisionMap : collisionMapsToTest) {
                    collisionMap.collide(playerA, playerB);
                }
            }
        }
        verify(pointCalculatorMock, times(0)).collidedWithAGhost(any(), any());
        verify(pointCalculatorMock, times(0)).consumedAPellet(any(), any());
    }

    /**
     * 玩家和豆子之间的碰撞
     */
    @Test
    public void test_collide_playerOnPellet() {
        int collisionCounter = 0;
        for (Class<? extends Unit> playerClass : PLAYERS) {
            Unit player = Mockito.mock(playerClass);
            for (Class<? extends Unit> pelletClass : PELLETS) {
                Unit pellet = Mockito.mock(pelletClass);
                for (CollisionMap collisionMap : collisionMapsToTest) {
                    collisionMap.collide(player, pellet);
                    collisionMap.collide(pellet, player);
                }
                collisionCounter += 2;
            }
        }
        verify(pointCalculatorMock, times(0)).collidedWithAGhost(any(), any());
        verify(pointCalculatorMock, times(collisionCounter * collisionMapsToTest.size())).consumedAPellet(any(), any());
    }

    /**
     * 幽灵和幽灵之间的碰撞
     */
    @Test
    public void test_collide_ghostOnGhost() {
        for (Class<? extends Unit> ghostClassA : GHOSTS) {
            Unit ghostA = Mockito.mock(ghostClassA);
            for (Class<? extends Unit> ghostClassB : GHOSTS) {
                Unit ghostB = Mockito.mock(ghostClassB);
                for (CollisionMap collisionMap : collisionMapsToTest) {
                    collisionMap.collide(ghostA, ghostB);
                }
            }
        }
        verify(pointCalculatorMock, times(0)).collidedWithAGhost(any(), any());
        verify(pointCalculatorMock, times(0)).consumedAPellet(any(), any());
    }

    /**
     * 幽灵和豆子之间的碰撞
     */
    @Test
    public void test_collide_ghostOnPellet() {
        for (Class<? extends Unit> ghostClass : GHOSTS) {
            Unit ghost = Mockito.mock(ghostClass);
            for (Class<? extends Unit> pelletClass : PELLETS) {
                Unit pellet = Mockito.mock(pelletClass);
                for (CollisionMap collisionMap : collisionMapsToTest) {
                    collisionMap.collide(ghost, pellet);
                    collisionMap.collide(pellet, ghost);
                }
            }
        }
        verify(pointCalculatorMock, times(0)).collidedWithAGhost(any(), any());
        verify(pointCalculatorMock, times(0)).consumedAPellet(any(), any());
    }

    /**
     * 玩家或者幽灵和空地之间的碰撞
     */
    @Test
    public void test_collide_movingOnCollisionLess() {
        Stream.concat(PLAYERS.stream(), GHOSTS.stream()).forEach(moverClass -> {
            Unit mover = Mockito.mock(moverClass);
            for (Class<? extends Unit> collisionClass : COLLISION_LESS) {
                Unit collisionLessUnit = Mockito.mock(collisionClass);
                for (CollisionMap collisionMap : collisionMapsToTest) {
                    collisionMap.collide(mover, collisionLessUnit);
                    collisionMap.collide(collisionLessUnit, mover);
                }
            }
        });
        verify(pointCalculatorMock, times(0)).collidedWithAGhost(any(), any());
        verify(pointCalculatorMock, times(0)).consumedAPellet(any(), any());
    }

    /**
     * 玩家和幽灵之间的碰撞
     */
    @Test
    public void test_collide_playerOnGhost() {
        int collisionCounter = 0;
        for (Class<? extends Unit> playerClass : PLAYERS) {
            Unit player = Mockito.mock(playerClass);
            for (Class<? extends Unit> ghostClass : GHOSTS) {
                Unit ghost = Mockito.mock(ghostClass);
                for (CollisionMap collisionMap : collisionMapsToTest) {
                    collisionMap.collide(player, ghost);
                    collisionMap.collide(ghost, player);
                }
                collisionCounter += 2;
            }
        }
        verify(pointCalculatorMock, times(collisionCounter * collisionMapsToTest.size())).collidedWithAGhost(any(), any());
        verify(pointCalculatorMock, times(0)).consumedAPellet(any(), any());
    }
}
