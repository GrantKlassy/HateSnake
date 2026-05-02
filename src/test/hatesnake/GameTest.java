package hatesnake;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;

class GameTest {

    @Test
    void startsInReadyAndStaysUntilStartCalled() {
        Game g = new Game(5, 5);
        assertThat(g.status()).isEqualTo(Game.Status.READY);
        assertThat(g.tick()).isEqualTo(Game.TickResult.WAITING);
        assertThat(g.snake().head()).isEqualTo(new Position(0, 0));
    }

    @Test
    void rejectsBadDimensions() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Game(1, 5));
        assertThatIllegalArgumentException().isThrownBy(() -> new Game(5, 1));
    }

    @Test
    void startTransitionsReadyToRunning() {
        Game g = new Game(5, 5);
        g.start();
        assertThat(g.status()).isEqualTo(Game.Status.RUNNING);
    }

    @Test
    void tickMovesSnakeAlongDefaultDirection() {
        Game g = new Game(5, 5);
        g.start();
        Game.TickResult r = g.tick();
        assertThat(r).isEqualTo(Game.TickResult.MOVED);
        assertThat(g.snake().head()).isEqualTo(new Position(0, 1));
    }

    @Test
    void enqueuedDirectionTakesEffectOnNextTick() {
        Game g = new Game(5, 5);
        g.start();
        g.enqueue(Direction.RIGHT);
        g.tick();
        assertThat(g.snake().direction()).isEqualTo(Direction.RIGHT);
        assertThat(g.snake().head()).isEqualTo(new Position(1, 0));
    }

    @Test
    void enqueueDropsConsecutiveDuplicates() {
        Game g = new Game(5, 5);
        g.start();
        g.enqueue(Direction.RIGHT);
        g.enqueue(Direction.RIGHT);
        g.enqueue(Direction.DOWN);

        g.tick();
        assertThat(g.snake().head()).isEqualTo(new Position(1, 0));

        g.tick();
        assertThat(g.snake().head()).isEqualTo(new Position(1, 1));
        assertThat(g.snake().direction()).isEqualTo(Direction.DOWN);
    }

    @Test
    void eatingAppleEmitsAteAppleAndRespawnsApple() {
        Game g = new Game(2, 2);
        g.start();
        g.enqueue(Direction.RIGHT);
        Position originalApple = g.apple().position();
        Game.TickResult r = g.tick();
        assertThat(r).isEqualTo(Game.TickResult.MOVED);
        assertThat(g.apple().position()).isEqualTo(originalApple);

        g.enqueue(Direction.DOWN);
        r = g.tick();
        assertThat(r).isEqualTo(Game.TickResult.ATE_APPLE);
        assertThat(g.snake().size()).isEqualTo(2);
        assertThat(g.apple().previous()).isEqualTo(originalApple);
    }

    @Test
    void hittingWallIsInstadeath() {
        Game g = new Game(3, 3);
        g.start();
        assertThat(g.tick()).isEqualTo(Game.TickResult.MOVED);
        assertThat(g.tick()).isEqualTo(Game.TickResult.MOVED);
        assertThat(g.tick()).isEqualTo(Game.TickResult.DIED);
        assertThat(g.status()).isEqualTo(Game.Status.DEAD);
    }

    @Test
    void deadGameStaysDeadOnFurtherTicks() {
        Game g = new Game(3, 3);
        g.start();
        g.tick();
        g.tick();
        g.tick();
        assertThat(g.status()).isEqualTo(Game.Status.DEAD);
        assertThat(g.tick()).isEqualTo(Game.TickResult.WAITING);
        assertThat(g.status()).isEqualTo(Game.Status.DEAD);
    }

    @Test
    void resetReturnsGameToReadyState() {
        Game g = new Game(3, 3);
        g.start();
        g.tick();
        g.tick();
        g.tick();
        assertThat(g.status()).isEqualTo(Game.Status.DEAD);
        g.reset();
        assertThat(g.status()).isEqualTo(Game.Status.READY);
        assertThat(g.snake().head()).isEqualTo(new Position(0, 0));
        assertThat(g.snake().size()).isEqualTo(1);
    }

    @Test
    void wallCollisionGivesNoGracePeriod() {
        Game g = new Game(3, 3);
        g.start();
        g.tick();
        g.tick();
        // Head is at (0,2); next tick walks off the bottom edge with
        // no input queued. Snake dies on the very next tick --- no
        // grace ticks, no chance to enqueue an escape.
        assertThat(g.tick()).isEqualTo(Game.TickResult.DIED);
        assertThat(g.status()).isEqualTo(Game.Status.DEAD);
    }

    @Test
    void winningEmitsWonAndTransitions() {
        Game g = new Game(2, 2);
        g.start();
        g.enqueue(Direction.RIGHT);
        assertThat(g.tick()).isEqualTo(Game.TickResult.MOVED);
        g.enqueue(Direction.DOWN);
        assertThat(g.tick()).isEqualTo(Game.TickResult.ATE_APPLE);
        g.enqueue(Direction.LEFT);
        assertThat(g.tick()).isEqualTo(Game.TickResult.MOVED);
        g.enqueue(Direction.UP);
        assertThat(g.tick()).isEqualTo(Game.TickResult.ATE_APPLE);
        g.enqueue(Direction.RIGHT);
        assertThat(g.tick()).isEqualTo(Game.TickResult.WON);
        assertThat(g.status()).isEqualTo(Game.Status.WON);
    }
}
