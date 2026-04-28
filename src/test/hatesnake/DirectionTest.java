package hatesnake;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DirectionTest {

    @Test
    void opposites() {
        assertThat(Direction.UP.opposite()).isEqualTo(Direction.DOWN);
        assertThat(Direction.DOWN.opposite()).isEqualTo(Direction.UP);
        assertThat(Direction.LEFT.opposite()).isEqualTo(Direction.RIGHT);
        assertThat(Direction.RIGHT.opposite()).isEqualTo(Direction.LEFT);
    }

    @Test
    void oppositeIsInvolution() {
        for (Direction d : Direction.values()) {
            assertThat(d.opposite().opposite()).isEqualTo(d);
        }
    }

    @ParameterizedTest
    @CsvSource({
        "w, UP",
        "a, LEFT",
        "s, DOWN",
        "d, RIGHT",
        "W, UP",
        "A, LEFT",
        "S, DOWN",
        "D, RIGHT"
    })
    void fromKeyMapsWasdAndCaseInsensitive(char key, Direction expected) {
        assertThat(Direction.fromKey(key)).isEqualTo(expected);
    }

    @Test
    void fromKeyReturnsNullForUnknown() {
        assertThat(Direction.fromKey('x')).isNull();
        assertThat(Direction.fromKey(' ')).isNull();
        assertThat(Direction.fromKey('0')).isNull();
    }

    @Test
    void vectorsAreUnit() {
        for (Direction d : Direction.values()) {
            int magnitude = Math.abs(d.dx()) + Math.abs(d.dy());
            assertThat(magnitude).as("unit vector %s", d).isEqualTo(1);
        }
    }
}
