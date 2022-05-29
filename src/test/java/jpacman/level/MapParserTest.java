package jpacman.level;

import jpacman.PacmanConfigurationException;
import jpacman.board.BoardFactory;
import jpacman.npc.Ghost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * MapParser的测试类
 * 测试 MapParser与它所依赖的两个工厂的交互，
 * 即 levelFactory 和 boardFactory。
 */
class MapParserTest {

    private LevelFactory levelFactory;
    private BoardFactory boardFactory;
    private MapParser mapParser;

    /**
     * 在每次测试之前创建一次 levelFactory 和 boardFactory。
     */
    @BeforeEach
    void setup() {
        levelFactory = Mockito.mock(LevelFactory.class);
        boardFactory = Mockito.mock(BoardFactory.class);
        mapParser = new MapParser(levelFactory, boardFactory);
    }

    /**
     * 测试地图正确输入的情况下，
     * parseMap函数正确创建了相应的board以及board上的对象。
     */
    @Test
    void testLevelCreationViaFile() {
        Ghost ghost = Mockito.mock(Ghost.class);
        Mockito.when(levelFactory.createGhost()).thenReturn(ghost);
        Pellet pellet = Mockito.mock(Pellet.class);
        Mockito.when(levelFactory.createPellet()).thenReturn(pellet);

        try {
            mapParser.parseMap("/testMap.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Mockito.verify(levelFactory, Mockito.times(2)).createGhost();
        Mockito.verify(levelFactory, Mockito.times(2)).createPellet();
        Mockito.verify(boardFactory, Mockito.times(2)).createWall();
        Mockito.verify(boardFactory, Mockito.times(6)).createGround();

    }

    /**
     * 测试输入文本不等宽的情况, 抛出PacmanConfigurationException异常
     */
    @Test
    void testNotEqualWidth() {
        assertThrows(PacmanConfigurationException.class, () -> mapParser.parseMap("/errorMap.txt"));
    }
}