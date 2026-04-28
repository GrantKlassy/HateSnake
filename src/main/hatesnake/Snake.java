package hatesnake;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public final class Snake {

    private final int boardWidth;
    private final int boardHeight;
    private final Deque<Position> body = new ArrayDeque<>();
    private Direction direction = Direction.DOWN;
    private boolean dead = false;

    public Snake(int boardWidth, int boardHeight) {
        if (boardWidth < 2 || boardHeight < 2) {
            throw new IllegalArgumentException("board must be at least 2x2");
        }
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.body.addLast(new Position(0, 0));
    }

    public List<Position> body() {
        return List.copyOf(body);
    }

    public Position head() {
        return body.peekLast();
    }

    public Position tail() {
        return body.peekFirst();
    }

    public Direction direction() {
        return direction;
    }

    public int size() {
        return body.size();
    }

    public boolean isDead() {
        return dead;
    }

    public boolean fillsBoard() {
        return body.size() >= boardWidth * boardHeight;
    }

    public Position projectHead(Direction proposed) {
        Direction projected = canTurn(proposed) ? proposed : direction;
        return head().translate(projected);
    }

    public boolean wouldDieAt(Position pos) {
        if (!inBounds(pos)) {
            return true;
        }
        Iterator<Position> it = body.iterator();
        if (it.hasNext()) {
            it.next();
        }
        while (it.hasNext()) {
            if (it.next().equals(pos)) {
                return true;
            }
        }
        return false;
    }

    public boolean advance(Direction proposed, Position applePosition) {
        if (canTurn(proposed)) {
            direction = proposed;
        }
        Position next = head().translate(direction);
        boolean ate = next.equals(applePosition);
        if (!ate) {
            body.removeFirst();
        }
        body.addLast(next);

        if (!inBounds(next) || collidesWithBody(next)) {
            dead = true;
        }
        return ate;
    }

    private boolean canTurn(Direction proposed) {
        return proposed != null && proposed != direction.opposite();
    }

    private boolean inBounds(Position pos) {
        return pos.x() >= 0 && pos.x() < boardWidth
            && pos.y() >= 0 && pos.y() < boardHeight;
    }

    private boolean collidesWithBody(Position newHead) {
        Iterator<Position> it = body.iterator();
        int max = body.size() - 1;
        for (int i = 0; i < max; i++) {
            if (it.next().equals(newHead)) {
                return true;
            }
        }
        return false;
    }
}
