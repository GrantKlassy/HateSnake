package hatesnake;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;

class AppleTest {

    @Test
    void initialPositionIsBottomRight() {
        Apple a = new Apple(4, 5);
        assertThat(a.position()).isEqualTo(new Position(3, 4));
        assertThat(a.previous()).isEqualTo(a.position());
    }

    @Test
    void rejectsTinyBoard() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Apple(1, 4));
    }

    @Test
    void cruelestSquareIsFurthestCornerOnEmptyBoard() {
        Snake s = new Snake(4, 4);
        Apple a = new Apple(4, 4);
        Position pick = a.cruelestReachableSquare(s);
        assertThat(pick).isEqualTo(new Position(3, 3));
    }

    @Test
    void cruelestSquareNeverLandsOnSnakeBody() {
        Snake s = new Snake(6, 6);
        s.advance(Direction.RIGHT, new Position(1, 0));
        s.advance(Direction.RIGHT, new Position(2, 0));
        s.advance(Direction.RIGHT, new Position(3, 0));
        Apple a = new Apple(6, 6);
        Position pick = a.cruelestReachableSquare(s);
        assertThat(s.body()).doesNotContain(pick);
    }

    @Test
    void respawnUpdatesPositionAndPreservesHistory() {
        Snake s = new Snake(4, 4);
        Apple a = new Apple(4, 4);
        Position before = a.position();
        s.advance(Direction.RIGHT, new Position(1, 0));
        a.respawn(s);
        assertThat(a.previous()).isEqualTo(before);
        assertThat(a.position()).isNotEqualTo(s.head());
    }

    @Test
    void refusesUnreachableStalemateAndPicksReachableSquare() {
        Snake s = new Snake(5, 5);
        s.advance(Direction.DOWN,  new Position(0, 1));
        s.advance(Direction.RIGHT, new Position(1, 1));
        s.advance(Direction.RIGHT, new Position(2, 1));
        s.advance(Direction.RIGHT, new Position(3, 1));
        s.advance(Direction.RIGHT, new Position(4, 1));
        s.advance(Direction.DOWN,  new Position(4, 2));
        Apple a = new Apple(5, 5);
        Position pick = a.cruelestReachableSquare(s);
        // Old logic placed the apple at unreachable (1,0) --- a stalemate.
        // New logic refuses that and stays in the reachable region (y >= 2).
        assertThat(pick.y()).isGreaterThanOrEqualTo(2);
        assertThat(s.body()).doesNotContain(pick);
    }

    @Test
    void trapsConfinedSnakeByPlacingFoodInFrontOfMouth() {
        Snake s = new Snake(3, 3);
        s.advance(Direction.DOWN,  new Position(0, 1));
        s.advance(Direction.DOWN,  new Position(0, 2));
        s.advance(Direction.RIGHT, new Position(1, 2));
        s.advance(Direction.RIGHT, new Position(2, 2));
        s.advance(Direction.UP,    new Position(2, 1));
        // Head at (2,1), facing UP, reachable region is the small pocket
        // along the right column. Apple must land directly in front of
        // the head so the next bite freezes the tail and the snake fills
        // its own pocket.
        Apple a = new Apple(3, 3);
        a.respawn(s);
        assertThat(a.position()).isEqualTo(new Position(2, 0));
    }
}
