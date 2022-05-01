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
import java.util.Optional;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

public class ClydeTest {

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
    void ClydeIsNotShy() {
        List<String> layout = Lists.newArrayList(
                "########################################",
                "# P                                   C#",
                "########################################"
        );
        Level level = gmp.parseMap(layout);
        Clyde clyde = Navigation.findUnitInBoard(Clyde.class, level.getBoard());
        level.registerPlayer(pacman);
        pacman.setDirection(Direction.EAST);
        assertThat(clyde.nextAiMove()).contains(Direction.WEST);
    }

    @Test
    void ClydeIsShy() {
        List<String> layout = Lists.newArrayList(
                "########################################",
                "# P       C                            #",
                "########################################"
        );
        Level level = gmp.parseMap(layout);
        Clyde clyde = Navigation.findUnitInBoard(Clyde.class, level.getBoard());
        level.registerPlayer(pacman);
        pacman.setDirection(Direction.EAST);
        assertThat(clyde.nextAiMove()).contains(Direction.EAST);
    }


    @Test
    void ClydeHasNoSquare() {
        List<String> layout = Lists.newArrayList(
                "########################################",
                "# P                                    #",
                "########################################"
        );
        Level level = gmp.parseMap(layout);
        assertThat(Navigation.findUnitInBoard(Clyde.class, level.getBoard())).isEqualTo(null);
    }

    @Test
    void PacmanHasNoSquare() {
        List<String> layout = Lists.newArrayList(
                "########################################",
                "# P       C                            #",
                "########################################"
        );
        Level level = gmp.parseMap(layout);
        Clyde clyde = Navigation.findUnitInBoard(Clyde.class, level.getBoard());
        assertThat(clyde.nextAiMove()).isEqualTo(Optional.empty());
    }

}
