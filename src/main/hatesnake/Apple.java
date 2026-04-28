package hatesnake;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public final class Apple {

    private final int boardWidth;
    private final int boardHeight;
    private Position position;
    private Position previous;

    public Apple(int boardWidth, int boardHeight) {
        if (boardWidth < 2 || boardHeight < 2) {
            throw new IllegalArgumentException("board must be at least 2x2");
        }
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.position = new Position(boardWidth - 1, boardHeight - 1);
        this.previous = this.position;
    }

    public Position position() {
        return position;
    }

    public Position previous() {
        return previous;
    }

    public void respawn(Snake snake) {
        previous = position;
        position = hardestReachableSquare(snake);
    }

    Position hardestReachableSquare(Snake snake) {
        Position head = snake.head();
        Set<Position> blocked = new HashSet<>(snake.body());
        blocked.remove(head);

        int[][] distance = new int[boardWidth][boardHeight];
        for (int[] row : distance) {
            Arrays.fill(row, -1);
        }
        distance[head.x()][head.y()] = 0;

        Deque<Position> queue = new ArrayDeque<>();
        queue.add(head);
        while (!queue.isEmpty()) {
            Position p = queue.poll();
            int d = distance[p.x()][p.y()];
            for (Direction dir : Direction.values()) {
                Position n = p.translate(dir);
                if (!inBounds(n) || distance[n.x()][n.y()] != -1 || blocked.contains(n)) {
                    continue;
                }
                distance[n.x()][n.y()] = d + 1;
                queue.add(n);
            }
        }

        long bestScore = Long.MIN_VALUE;
        Position best = null;
        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                Position candidate = new Position(x, y);
                if (blocked.contains(candidate)) {
                    continue;
                }
                int d = distance[x][y];
                long score = (d == -1) ? Long.MAX_VALUE : d;
                if (score > bestScore) {
                    bestScore = score;
                    best = candidate;
                }
            }
        }
        return best;
    }

    private boolean inBounds(Position pos) {
        return pos.x() >= 0 && pos.x() < boardWidth
            && pos.y() >= 0 && pos.y() < boardHeight;
    }
}
