package hatesnake;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public final class Apple {

    private static final int TRAP_REGION_SLACK = 2;

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
        Position trap = trapInFrontOf(snake);
        position = (trap != null) ? trap : cruelestReachableSquare(snake);
    }

    // When the snake's reachable region is small enough that we can fill it
    // before it can wiggle out, drop the apple one step in front of the head.
    // The bite freezes the tail; we do it again next respawn; the snake fills
    // its own pocket and dies by self-collision.
    Position trapInFrontOf(Snake snake) {
        Set<Position> body = new HashSet<>(snake.body());
        int regionSize = countReachable(bfsFromHead(snake.head(), body));
        if (regionSize > snake.size() + TRAP_REGION_SLACK) {
            return null;
        }
        Position front = snake.head().translate(snake.direction());
        if (!inBounds(front) || body.contains(front)) {
            return null;
        }
        return front;
    }

    Position cruelestReachableSquare(Snake snake) {
        Position head = snake.head();
        Set<Position> body = new HashSet<>(snake.body());
        int[][] distance = bfsFromHead(head, body);

        long[] bestScore = null;
        Position best = null;
        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                if (distance[x][y] == -1) {
                    continue;
                }
                Position candidate = new Position(x, y);
                if (body.contains(candidate)) {
                    continue;
                }
                long postEat = postEatRegionSize(candidate, body);
                long corner  = cornerScore(candidate, body);
                long dist    = distance[x][y];
                long[] score = { -postEat, corner, dist, -x, -y };
                if (bestScore == null || Arrays.compare(score, bestScore) > 0) {
                    bestScore = score;
                    best = candidate;
                }
            }
        }
        if (best != null) {
            return best;
        }
        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                Position p = new Position(x, y);
                if (!body.contains(p)) {
                    return p;
                }
            }
        }
        return position;
    }

    private int[][] bfsFromHead(Position head, Set<Position> blocked) {
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
        return distance;
    }

    private int countReachable(int[][] distance) {
        int count = 0;
        for (int[] row : distance) {
            for (int d : row) {
                if (d != -1) {
                    count++;
                }
            }
        }
        return count;
    }

    private int postEatRegionSize(Position p, Set<Position> body) {
        boolean[][] seen = new boolean[boardWidth][boardHeight];
        Deque<Position> queue = new ArrayDeque<>();
        queue.add(p);
        seen[p.x()][p.y()] = true;
        int size = 0;
        while (!queue.isEmpty()) {
            Position cur = queue.poll();
            size++;
            for (Direction dir : Direction.values()) {
                Position n = cur.translate(dir);
                if (!inBounds(n) || seen[n.x()][n.y()] || body.contains(n)) {
                    continue;
                }
                seen[n.x()][n.y()] = true;
                queue.add(n);
            }
        }
        return size;
    }

    private int cornerScore(Position p, Set<Position> body) {
        int count = 0;
        for (Direction dir : Direction.values()) {
            Position next = p.translate(dir);
            if (!inBounds(next) || body.contains(next)) {
                count++;
            }
        }
        return count;
    }

    private boolean inBounds(Position pos) {
        return pos.x() >= 0 && pos.x() < boardWidth
            && pos.y() >= 0 && pos.y() < boardHeight;
    }
}
