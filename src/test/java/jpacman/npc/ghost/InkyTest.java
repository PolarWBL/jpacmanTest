package jpacman.npc.ghost;

import jpacman.board.BoardFactory;
import jpacman.board.Direction;
import jpacman.level.Level;
import jpacman.level.LevelFactory;
import jpacman.level.Player;
import jpacman.level.PlayerFactory;
import jpacman.points.DefaultPointCalculator;
import jpacman.sprite.PacManSprites;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class InkyTest {

    private GhostMapParser gmp;
    private Player pacman;

    @BeforeEach
    void setUp() {
        PacManSprites sprites = new PacManSprites();
        BoardFactory boardfactory = new BoardFactory(sprites);
        GhostFactory ghostFactory = new GhostFactory(sprites);
        DefaultPointCalculator pc = new DefaultPointCalculator();
        LevelFactory levelFatory = new LevelFactory(sprites, ghostFactory, pc);
        gmp = new GhostMapParser( levelFatory, boardfactory, ghostFactory);
        PlayerFactory playerFactory = new PlayerFactory(sprites);
        pacman = playerFactory.createPacMan();
    }

    @Test
    void InkyInFront() {
        List<String> layout = Lists.newArrayList(
                "########################################",
                "#                                      #",
                "#           I         P           A    #",
                "#                                      #",
                "########################################"
        );
        Level level = gmp.parseMap(layout);
        Inky inky = Navigation.findUnitInBoard(Inky.class, level.getBoard());
        level.registerPlayer(pacman);
        pacman.setDirection(Direction.WEST);
        assertThat(inky.nextAiMove()).contains(Direction.WEST);
    }

    @Test
    void InkyBehind() {
        List<String> layout = Lists.newArrayList(
                "########################################",
                "#                                      #",
                "#       I    P                   A     #",
                "#                                      #",
                "########################################"
        );
        Level level = gmp.parseMap(layout);
        Inky inky = Navigation.findUnitInBoard(Inky.class, level.getBoard());
        level.registerPlayer(pacman);
        pacman.setDirection(Direction.EAST);
        assertThat(inky.nextAiMove()).contains(Direction.EAST);
    }


    @Test
    void InkyHasNoSquare() {
        List<String> layout = Lists.newArrayList(
                "########################################",
                "#                                      #",
                "#                      P               #",
                "# A                                    #",
                "########################################"
        );
        Level level = gmp.parseMap(layout);
        assertThat(Navigation.findUnitInBoard(Inky.class, level.getBoard())).isEqualTo(null);
    }

    @Test
    void PacmanHasNoSquare() {
        List<String> layout = Lists.newArrayList(
                "########################################",
                "#     A                           I    #",
                "#                                      #",
                "#                                      #",
                "########################################"
        );
        Level level = gmp.parseMap(layout);
        Inky inky = Navigation.findUnitInBoard(Inky.class, level.getBoard());
        assertThat(inky.nextAiMove()).isEqualTo(Optional.empty());
    }


    @Test
    void BlinkyHasNoSquare() {
        List<String> layout = Lists.newArrayList(
                "########################################",
                "#                  P              I    #",
                "#                                      #",
                "#                                      #",
                "########################################"
        );
        Level level = gmp.parseMap(layout);
        Inky inky = Navigation.findUnitInBoard(Inky.class, level.getBoard());
        assertThat(inky.nextAiMove()).isEqualTo(Optional.empty());
    }
}
