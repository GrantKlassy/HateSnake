package hatesnake;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;

class SnakeTest {

    private static final Position FAR_AWAY = new Position(99, 99);

    @Test
    void initializesAtOriginWithLengthOne() {
        Snake s = new Snake(4, 4);
        assertThat(s.size()).isEqualTo(1);
        assertThat(s.head()).isEqualTo(new Position(0, 0));
        assertThat(s.tail()).isEqualTo(s.head());
        assertThat(s.direction()).isEqualTo(Direction.DOWN);
        assertThat(s.isDead()).isFalse();
        assertThat(s.fillsBoard()).isFalse();
    }

    @Test
    void rejectsTinyBoard() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Snake(1, 4));
        assertThatIllegalArgumentException().isThrownBy(() -> new Snake(4, 1));
    }

    @Test
    void advanceMovesHeadAndKeepsLengthWhenNoApple() {
        Snake s = new Snake(4, 4);
        s.advance(Direction.RIGHT, FAR_AWAY);
        assertThat(s.head()).isEqualTo(new Position(1, 0));
        assertThat(s.size()).isEqualTo(1);
        assertThat(s.direction()).isEqualTo(Direction.RIGHT);
    }

    @Test
    void advanceGrowsWhenEatingApple() {
        Snake s = new Snake(4, 4);
        Position apple = new Position(1, 0);
        boolean ate = s.advance(Direction.RIGHT, apple);
        assertThat(ate).isTrue();
        assertThat(s.size()).isEqualTo(2);
        assertThat(s.head()).isEqualTo(apple);
        assertThat(s.tail()).isEqualTo(new Position(0, 0));
    }

    @Test
    void cannotReverseInto180Turn() {
        Snake s = new Snake(4, 4);
        s.advance(Direction.RIGHT, FAR_AWAY);
        s.advance(Direction.LEFT, FAR_AWAY);
        assertThat(s.direction()).isEqualTo(Direction.RIGHT);
        assertThat(s.head()).isEqualTo(new Position(2, 0));
    }

    @Test
    void diesWhenHittingWall() {
        Snake s = new Snake(4, 4);
        s.advance(null, FAR_AWAY);
        s.advance(null, FAR_AWAY);
        s.advance(null, FAR_AWAY);
        assertThat(s.isDead()).isFalse();
        s.advance(null, FAR_AWAY);
        assertThat(s.isDead()).isTrue();
    }

    @Test
    void diesWhenBitingOwnBody() {
        Snake s = new Snake(6, 6);
        s.advance(Direction.RIGHT, new Position(1, 0));
        s.advance(Direction.RIGHT, new Position(2, 0));
        s.advance(Direction.DOWN,  new Position(2, 1));
        s.advance(Direction.LEFT,  new Position(1, 1));
        assertThat(s.isDead()).isFalse();
        s.advance(Direction.UP, FAR_AWAY);
        assertThat(s.isDead()).isTrue();
    }

    @Test
    void wouldDieAtDetectsWalls() {
        Snake s = new Snake(4, 4);
        assertThat(s.wouldDieAt(new Position(-1, 0))).isTrue();
        assertThat(s.wouldDieAt(new Position(0, -1))).isTrue();
        assertThat(s.wouldDieAt(new Position(4, 0))).isTrue();
        assertThat(s.wouldDieAt(new Position(0, 4))).isTrue();
        assertThat(s.wouldDieAt(new Position(2, 2))).isFalse();
    }

    @Test
    void wouldDieAtIgnoresTailBecauseItMovesNextTick() {
        Snake s = new Snake(6, 6);
        s.advance(Direction.RIGHT, new Position(1, 0));
        s.advance(Direction.RIGHT, new Position(2, 0));
        Position tail = s.tail();
        assertThat(s.wouldDieAt(tail)).isFalse();
    }

    @Test
    void projectHeadIgnoresOppositeDirection() {
        Snake s = new Snake(4, 4);
        s.advance(Direction.RIGHT, FAR_AWAY);
        Position projected = s.projectHead(Direction.LEFT);
        assertThat(projected).isEqualTo(new Position(2, 0));
    }

    @Test
    void bodyIsImmutable() {
        Snake s = new Snake(4, 4);
        var body = s.body();
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> body.add(new Position(9, 9)))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}
