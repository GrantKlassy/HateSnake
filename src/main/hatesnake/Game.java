package hatesnake;

import java.util.ArrayDeque;
import java.util.Deque;

public final class Game {

    public enum Status { READY, RUNNING, DEAD, WON }

    public enum TickResult { WAITING, MOVED, ATE_APPLE, DIED, WON }

    public static final int DEFAULT_GRACE_TICKS = 30;

    private final int width;
    private final int height;
    private final int maxGraceTicks;
    private final Deque<Direction> inputBuffer = new ArrayDeque<>();
    private Snake snake;
    private Apple apple;
    private Status status = Status.READY;
    private int graceTicks = 0;

    public Game(int width, int height) {
        this(width, height, DEFAULT_GRACE_TICKS);
    }

    public Game(int width, int height, int maxGraceTicks) {
        if (width < 2 || height < 2) {
            throw new IllegalArgumentException("board must be at least 2x2");
        }
        if (maxGraceTicks < 0) {
            throw new IllegalArgumentException("maxGraceTicks must be non-negative");
        }
        this.width = width;
        this.height = height;
        this.maxGraceTicks = maxGraceTicks;
        this.snake = new Snake(width, height);
        this.apple = new Apple(width, height);
    }

    public int width()         { return width; }
    public int height()        { return height; }
    public int maxGraceTicks() { return maxGraceTicks; }
    public int graceTicks()    { return graceTicks; }
    public Status status()     { return status; }
    public Snake snake()       { return snake; }
    public Apple apple()       { return apple; }

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

        Direction peeked = inGrace() ? inputBuffer.peekLast() : inputBuffer.peekFirst();
        Position projected = snake.projectHead(peeked);

        if (snake.wouldDieAt(projected)) {
            if (graceTicks < maxGraceTicks) {
                graceTicks++;
                return TickResult.WAITING;
            }
        } else if (inGrace()) {
            Direction last = inputBuffer.peekLast();
            inputBuffer.clear();
            if (last != null) {
                inputBuffer.addLast(last);
            }
        }
        graceTicks = 0;

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
        graceTicks = 0;
        inputBuffer.clear();
    }

    private boolean inGrace() {
        return graceTicks > 0;
    }
}
