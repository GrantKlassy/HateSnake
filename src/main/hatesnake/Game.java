package hatesnake;

import java.util.ArrayDeque;
import java.util.Deque;

public final class Game {

    public enum Status { READY, RUNNING, DEAD, WON }

    public enum TickResult { WAITING, MOVED, ATE_APPLE, DIED, WON }

    private final int width;
    private final int height;
    private final Deque<Direction> inputBuffer = new ArrayDeque<>();
    private Snake snake;
    private Apple apple;
    private Status status = Status.READY;

    public Game(int width, int height) {
        if (width < 2 || height < 2) {
            throw new IllegalArgumentException("board must be at least 2x2");
        }
        this.width = width;
        this.height = height;
        this.snake = new Snake(width, height);
        this.apple = new Apple(width, height);
    }

    public int width()     { return width; }
    public int height()    { return height; }
    public Status status() { return status; }
    public Snake snake()   { return snake; }
    public Apple apple()   { return apple; }

    public void start() {
        if (status == Status.READY) {
            status = Status.RUNNING;
        }
    }

    public void enqueue(Direction d) {
        if (d == null) {
            return;
        }
        Direction reference = inputBuffer.peekLast();
        if (reference == null) {
            reference = snake.direction();
        }
        if (d != reference) {
            inputBuffer.addLast(d);
        }
    }

    public TickResult tick() {
        if (status != Status.RUNNING) {
            return TickResult.WAITING;
        }

        Direction commit = inputBuffer.pollFirst();
        boolean ate = snake.advance(commit, apple.position());

        if (snake.isDead()) {
            status = Status.DEAD;
            return TickResult.DIED;
        }
        if (snake.fillsBoard()) {
            status = Status.WON;
            return TickResult.WON;
        }
        if (ate) {
            apple.respawn(snake);
            return TickResult.ATE_APPLE;
        }
        return TickResult.MOVED;
    }

    public void reset() {
        snake = new Snake(width, height);
        apple = new Apple(width, height);
        status = Status.READY;
        inputBuffer.clear();
    }
}
