package hatesnake;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PositionTest {

    @Test
    void recordEquality() {
        assertThat(new Position(2, 3)).isEqualTo(new Position(2, 3));
        assertThat(new Position(2, 3)).isNotEqualTo(new Position(3, 2));
    }

    @Test
    void translateAppliesDirectionVector() {
        Position p = new Position(5, 5);
        assertThat(p.translate(Direction.UP)).isEqualTo(new Position(5, 4));
        assertThat(p.translate(Direction.DOWN)).isEqualTo(new Position(5, 6));
        assertThat(p.translate(Direction.LEFT)).isEqualTo(new Position(4, 5));
        assertThat(p.translate(Direction.RIGHT)).isEqualTo(new Position(6, 5));
    }

    @Test
    void translateIsImmutable() {
        Position p = new Position(0, 0);
        p.translate(Direction.RIGHT);
        assertThat(p).isEqualTo(new Position(0, 0));
    }
}
