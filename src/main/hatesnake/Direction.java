package hatesnake;

public enum Direction {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int dx() {
        return dx;
    }

    public int dy() {
        return dy;
    }

    public Direction opposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }

    public static Direction fromKey(char key) {
        return switch (Character.toLowerCase(key)) {
            case 'w' -> UP;
            case 'a' -> LEFT;
            case 's' -> DOWN;
            case 'd' -> RIGHT;
            default -> null;
        };
    }
}
