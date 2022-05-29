package jpacman.game;

import jpacman.level.Level;
import jpacman.level.Player;
import jpacman.points.PointCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

/**
 *  Game 类的 start 方法的测试类。
 */
public class GameUnitTest {

    private SinglePlayerGame singlePlayerGame;
    private Player player;
    private Level level;
    private PointCalculator pointCalculator;

    /**
     * 在测试之前调用的方法
     * 模拟玩家、关卡和积分计算器并创建一个新的单人游戏。
     */
    @BeforeEach
    void setup() {
        player = Mockito.mock(Player.class);
        level = Mockito.mock(Level.class);
        pointCalculator = Mockito.mock(PointCalculator.class);
        singlePlayerGame = new SinglePlayerGame(player, level, pointCalculator);
    }

    /**
     * 检测试玩家 活着 并且剩下 1 个 pellet 的情况
     */
    @Test
    public void playerIsAlivePelletsRemainTest() {
        Mockito.when(level.isAnyPlayerAlive()).thenReturn(true);
        Mockito.when(level.remainingPellets()).thenReturn(1);

        singlePlayerGame.start();
        assertTrue(singlePlayerGame.isInProgress());
    }

    /**
     * 检测试玩家 死亡 并且剩下 1 个 pellet 的情况
     */
    @Test
    public void playerIsNotAlivePelletsRemainTest() {
        Mockito.when(level.isAnyPlayerAlive()).thenReturn(false);
        Mockito.when(level.remainingPellets()).thenReturn(1);

        singlePlayerGame.start();
        assertFalse(singlePlayerGame.isInProgress());
    }

    /**
     * 检测试玩家 活着 并且剩下 0 个 pellet 的情况
     */
    @Test
    public void playerIsAlivePelletsDoNotRemainTest() {
        Mockito.when(level.isAnyPlayerAlive()).thenReturn(true);
        Mockito.when(level.remainingPellets()).thenReturn(0);

        singlePlayerGame.start();
        assertFalse(singlePlayerGame.isInProgress());
    }

    /**
     * 检测试玩家 活着 并且剩下 1 个 pellet
     * 并且两次调用 start 方法的情况
     */
    @Test
    public void playerIsAlivePelletsRemainTestStartTwiceTest() {
        Mockito.when(level.isAnyPlayerAlive()).thenReturn(true);
        Mockito.when(level.remainingPellets()).thenReturn(1);

        singlePlayerGame.start();
        singlePlayerGame.start();

        assertTrue(singlePlayerGame.isInProgress());
    }
}