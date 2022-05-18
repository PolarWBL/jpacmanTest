package utility;

import jpacman.board.Square;
import jpacman.board.Unit;
import jpacman.sprite.Sprite;

import java.util.Objects;

/**
 * Utility class used to match squares for constraints.
 */
public class MatcherSquare extends Square {
    private final Integer expectedOccupants;
    private final Boolean expectedAccessibility;
    private final Class<?> expectedOccupantType;

    /**
     * Provides a MatcherSquare that matches any Square.
     *
     * @return a MatcherSquare matching any Square.
     */
    public static MatcherSquare any() {
        return new MatcherSquare(null, null, null);
    }

    /**
     * Constructor for a MatcherSquare.
     *
     * @param expectedOccupants     amount of occupants a Square needs to have to match this (or null)
     * @param expectedAccessibility accessibility a Square must have to match this (or null)
     * @param expectedOccupantType  Class of the first occupant on the Square (or null)
     */
    public MatcherSquare(Integer expectedOccupants, Boolean expectedAccessibility, Class<?> expectedOccupantType) {
        this.expectedOccupants = expectedOccupants;
        this.expectedAccessibility = expectedAccessibility;
        this.expectedOccupantType = expectedOccupantType;
    }

    @Override
    public boolean isAccessibleTo(Unit unit) {
        return false;
    }

    @Override
    public Sprite getSprite() {
        return null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(expectedOccupants, expectedAccessibility, expectedOccupantType);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Square)) {
            return false;
        }

        Square other = (Square) obj;
        if (expectedOccupants != null && other.getOccupants().size() != expectedOccupants) {
            return false;
        }
        if (expectedAccessibility != null && other.isAccessibleTo(null) != expectedAccessibility) {
            return false;
        }
        return expectedOccupantType == null
            || expectedOccupantType.isAssignableFrom(other.getOccupants().get(0).getClass());
    }

    @Override
    public String toString() {
        return "MatcherSquare{"
            + "expectedOccupants=" + expectedOccupants
            + ", expectedAccessibility=" + expectedAccessibility
            + ", expectedOccupantType=" + expectedOccupantType
            + '}';
    }
}
