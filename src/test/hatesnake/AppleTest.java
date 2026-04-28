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
    void hardestSquareIsFurthestReachableFromHead() {
        Snake s = new Snake(4, 4);
        Apple a = new Apple(4, 4);
        Position hardest = a.hardestReachableSquare(s);
        assertThat(hardest).isEqualTo(new Position(3, 3));
    }

    @Test
    void hardestSquareNeverLandsOnSnakeBody() {
        Snake s = new Snake(6, 6);
        s.advance(Direction.RIGHT, new Position(1, 0));
        s.advance(Direction.RIGHT, new Position(2, 0));
        s.advance(Direction.RIGHT, new Position(3, 0));
        Apple a = new Apple(6, 6);
        Position hardest = a.hardestReachableSquare(s);
        assertThat(s.body()).doesNotContain(hardest);
    }

    @Test
    void respawnUpdatesPositionAndPreserveHistory() {
        Snake s = new Snake(4, 4);
        Apple a = new Apple(4, 4);
        Position before = a.position();
        s.advance(Direction.RIGHT, new Position(1, 0));
        a.respawn(s);
        assertThat(a.previous()).isEqualTo(before);
        assertThat(a.position()).isNotEqualTo(s.head());
    }

    @Test
    void prefersUnreachableSquareOverFarReachableOne() {
        Snake s = new Snake(5, 5);
        s.advance(Direction.DOWN,  new Position(0, 1));
        s.advance(Direction.RIGHT, new Position(1, 1));
        s.advance(Direction.RIGHT, new Position(2, 1));
        s.advance(Direction.RIGHT, new Position(3, 1));
        s.advance(Direction.RIGHT, new Position(4, 1));
        s.advance(Direction.DOWN,  new Position(4, 2));
        Apple a = new Apple(5, 5);
        Position hardest = a.hardestReachableSquare(s);
        assertThat(hardest).isEqualTo(new Position(1, 0));
    }
}
