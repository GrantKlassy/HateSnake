package hatesnake;

public record Position(int x, int y) {

    public Position translate(Direction dir) {
        return new Position(x + dir.dx(), y + dir.dy());
    }
}
