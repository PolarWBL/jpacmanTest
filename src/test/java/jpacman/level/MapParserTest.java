package jpacman.level;

import jpacman.board.BoardFactory;
import jpacman.board.Square;
import jpacman.PacmanConfigurationException;
import jpacman.npc.Ghost;
import jpacman.npc.ghost.GhostFactory;
import jpacman.npc.ghost.GhostMapParser;
import jpacman.points.DefaultPointCalculator;
import jpacman.sprite.PacManSprites;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import utility.MatcherSquare;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

/**
 * Tests {@link MapParser}.
 */
public class MapParserTest {
    private LevelFactory levelFactory = null;
    private BoardFactory boardFactory = null;
    private MapParser mapParser = null;

    @Captor
    private ArgumentCaptor<Square[][]> squareMatrixCaptor;

    @Captor
    private ArgumentCaptor<List<Square>> listOfSquaresCaptor;

    @Captor
    private ArgumentCaptor<char[][]> charMatrixCaptor;

    @Captor
    private ArgumentCaptor<List<String>> stringListCaptor;

    /**
     * Called before every Test.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        PacManSprites pacManSprites = new PacManSprites();
        GhostFactory ghostFactory = new GhostFactory(pacManSprites);
        levelFactory = Mockito.spy(new LevelFactory(pacManSprites, ghostFactory, new DefaultPointCalculator()));
        boardFactory = Mockito.spy(new BoardFactory(pacManSprites));
        mapParser = Mockito.spy(new MapParser(levelFactory, boardFactory));
    }

    /**
     * verify that the MapParser handles empty maps correctly.
     */
    @Test
    public void test_parseMap_emptyMap() {
        mapParser.parseMap(new char[][]{});

        Mockito.verify(boardFactory, Mockito.times(1)).createBoard(squareMatrixCaptor.capture());
        assertEquals(0, squareMatrixCaptor.getValue().length,
            "Expected boardFactory.createBoard to be called with an empty grid!"
        );
        Mockito.verify(levelFactory, Mockito.times(1)).createLevel(any(), any(), any());
    }

    /**
     * Verify that the MapParser handles a typical map correctly.
     */
    @Test
    public void test_parseMap_normalMap() {
        mapParser.parseMap(new char[][]{{' ', '#'}, {'.', 'G'}});
        List<MatcherSquare> squareValidators = Arrays.asList(
            new MatcherSquare(0, true, null),
            new MatcherSquare(0, false, null),
            new MatcherSquare(1, null, Pellet.class),
            new MatcherSquare(1, null, Ghost.class)
        );

        Mockito.verify(boardFactory, Mockito.times(1)).createBoard(squareMatrixCaptor.capture());
        List<Square> squares = Arrays.stream(squareMatrixCaptor.getValue())
            .flatMap(Arrays::stream)
            .collect(Collectors.toList());
        for (int i = 0; i < squareValidators.size(); i++) {
            assertEquals(squareValidators.get(i), squares.get(i));
        }

        Mockito.verify(levelFactory, Mockito.times(1)).createLevel(any(), any(), any());
    }

    /**
     * Verify that the MapParser correctly reads the player position.
     */
    @Test
    public void test_parseMap_validPlayerPosition() {
        mapParser.parseMap(new char[][]{{'.', 'P', 'G'}});
        int indexOfPlayer = 1;

        Mockito.verify(boardFactory, Mockito.times(1)).createBoard(squareMatrixCaptor.capture());
        List<Square> squares = Arrays.stream(squareMatrixCaptor.getValue())
            .flatMap(Arrays::stream)
            .collect(Collectors.toList());

        Mockito.verify(levelFactory, Mockito.times(1)).createLevel(any(), any(), listOfSquaresCaptor.capture());
        List<Square> startingPositions = listOfSquaresCaptor.getValue();
        assertEquals(1, startingPositions.size(), "Expected 1 startingPosition to be found.");
        assertEquals(squares.get(indexOfPlayer), startingPositions.get(0));
    }

    /**
     * Verify that the MapParser throws an exception when encountering unknown characters.
     */
    @Test
    public void test_parseMap_invalidCharacter() {
        assertThrows(PacmanConfigurationException.class, () -> mapParser.parseMap(new char[][]{{'~'}}));
    }

    /**
     * Verify that the MapParser reject maps of invalid format.
     */
    @Test
    public void test_parseMap_invalidFormat() {
        Class<PacmanConfigurationException> targetException = PacmanConfigurationException.class;
        assertThrows(targetException, () -> mapParser.parseMap((List<String>) null));
        assertThrows(targetException, () -> mapParser.parseMap(new ArrayList<>()));
        assertThrows(targetException, () -> mapParser.parseMap(Collections.singletonList("")));
        assertThrows(targetException, () -> mapParser.parseMap(Arrays.asList("..", "...")));
    }

    /**
     * Verify that the MapParser correctly handles a valid list of strings.
     */
    @Test
    public void test_parseMap_validStringList() {
        char[][] targetMap = new char[][]{{' ', '#'}, {'.', 'G'}, {'P', ' '}};
        char[][] targetMapTransposed = new char[targetMap[0].length][targetMap.length];
        for (int y = 0; y < targetMap.length; y++) {
            for (int x = 0; x < targetMap[y].length; x++) {
                targetMapTransposed[x][y] = targetMap[y][x];
            }
        }
        mapParser.parseMap(Arrays.stream(targetMap).map(String::new).collect(Collectors.toList()));

        Mockito.verify(mapParser, Mockito.times(1)).parseMap(charMatrixCaptor.capture());
        assertThat(charMatrixCaptor.getValue())
            .usingRecursiveFieldByFieldElementComparator()
            .isEqualTo(targetMapTransposed);
    }

    /**
     * Verify that the MapParser correctly handles a valid input stream.
     *
     * @throws IOException may be thrown by the MapParser (but shouldn't)
     */
    @Test
    public void test_parseMap_validInputStream() throws IOException {
        String text = " #\n.G\rP \r\n..";
        InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        List<String> lines = Arrays.stream(text.split("(\r\n|\n|\r)")).collect(Collectors.toList());

        mapParser.parseMap(stream);

        Mockito.verify(mapParser, Mockito.times(1)).parseMap(stringListCaptor.capture());
        assertEquals(lines, stringListCaptor.getValue());
    }

    /**
     * Verify that the MapParser correctly handles valid resource files.
     *
     * @throws ClassNotFoundException may be thrown by the utility method `getMapNameForTest`
     * @throws IOException            may be thrown by the MapParser
     */
    @Test
    public void test_parseMap_validMapName() throws ClassNotFoundException, IOException {
        String mapNameForTest = GhostMapParser.getMapNameForTest();
        InputStream resourceAsStream = MapParserTest.class.getResourceAsStream(mapNameForTest);
        assert resourceAsStream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8));
        List<String> expectedLines = reader.lines().collect(Collectors.toList());
        reader.close();

        mapParser.parseMap(mapNameForTest);

        Mockito.verify(mapParser, Mockito.times(1)).parseMap(stringListCaptor.capture());
        assertEquals(expectedLines, stringListCaptor.getValue());
    }

    /**
     * Verify that the mapParser rejects invalid resource files.
     */
    @Test
    public void test_parseMap_invalidMapName() {
        assertThrows(PacmanConfigurationException.class, () -> mapParser.parseMap("86c66f9c_DoNotCreateThisMap.map"));
    }

    /**
     * Verify behaviour of parseMap(String) when parseMap(InputStream) throws an IOException.
     *
     * @throws IOException            should not be thrown
     * @throws ClassNotFoundException may be thrown by the utility method `getMapNameForTest`
     */
    @Test
    public void test_parseMap_throwsIOException() throws IOException, ClassNotFoundException {
        String mapNameForTest = GhostMapParser.getMapNameForTest();
        Mockito.doThrow(new IOException("Testing!")).when(mapParser).parseMap(Mockito.any(InputStream.class));

        assertThrows(IOException.class, () -> mapParser.parseMap(mapNameForTest));
    }
}
