package hatesnake;

import processing.core.PApplet;

public final class HateSnakeApp extends PApplet {

    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 10;
    private static final int CELL = 50;
    private static final int BORDER = 10;
    private static final int FRAMES_PER_TICK = 6;
    private static final int DEATH_LOCKOUT_FRAMES = 180;

    private static final Rgb BACKGROUND = new Rgb(105, 105, 105);
    private static final Rgb GRID_LINE  = new Rgb(255, 255, 255);
    private static final Rgb SNAKE      = new Rgb(0, 255, 0);
    private static final Rgb APPLE      = new Rgb(255, 0, 0);
    private static final Rgb DEATH_TEXT = new Rgb(255, 0, 0);

    private final Game game = new Game(BOARD_WIDTH, BOARD_HEIGHT);
    private int frameCounter = 0;
    private int deathFrame = -1;

    public static void main(String[] args) {
        PApplet.main(HateSnakeApp.class);
    }

    @Override
    public void settings() {
        size(BOARD_WIDTH * CELL + BORDER * 2, BOARD_HEIGHT * CELL + BORDER * 2);
    }

    @Override
    public void setup() {
        colorMode(RGB);
        drawBoard();
    }

    @Override
    public void draw() {
        switch (game.status()) {
            case READY, RUNNING -> step();
            case DEAD -> waitOutDeath();
            case WON -> {
                System.out.println("You won!");
                exit();
            }
        }
    }

    @Override
    public void keyPressed() {
        if (game.status() == Game.Status.DEAD) {
            return;
        }
        game.start();
        Direction dir = (key == CODED) ? fromKeyCode(keyCode) : Direction.fromKey(key);
        game.enqueue(dir);
    }

    private void step() {
        if (game.status() != Game.Status.RUNNING) {
            return;
        }
        if (frameCounter++ % FRAMES_PER_TICK != 0) {
            return;
        }
        Game.TickResult result = game.tick();
        switch (result) {
            case MOVED, ATE_APPLE -> drawBoard();
            case DIED -> {
                drawDeathOverlay();
                deathFrame = frameCount;
            }
            case WAITING, WON -> { /* WAITING: in grace; WON: handled next draw() */ }
        }
    }

    private void waitOutDeath() {
        if (deathFrame >= 0 && frameCount - deathFrame >= DEATH_LOCKOUT_FRAMES) {
            game.reset();
            frameCounter = 0;
            deathFrame = -1;
            drawBoard();
        }
    }

    private Direction fromKeyCode(int code) {
        if (code == UP)    return Direction.UP;
        if (code == DOWN)  return Direction.DOWN;
        if (code == LEFT)  return Direction.LEFT;
        if (code == RIGHT) return Direction.RIGHT;
        return null;
    }

    private void drawBoard() {
        background(BACKGROUND.red(), BACKGROUND.green(), BACKGROUND.blue());
        drawGrid();
        for (Position p : game.snake().body()) {
            drawCell(p, SNAKE);
        }
        drawCell(game.apple().position(), APPLE);
    }

    private void drawGrid() {
        fill(GRID_LINE.red(), GRID_LINE.green(), GRID_LINE.blue());
        int boardPxX = BOARD_WIDTH * CELL + BORDER;
        int boardPxY = BOARD_HEIGHT * CELL + BORDER;
        for (int i = 0; i <= BOARD_WIDTH; i++) {
            int x = i * CELL + BORDER;
            line(x, BORDER, x, boardPxY);
        }
        for (int i = 0; i <= BOARD_HEIGHT; i++) {
            int y = i * CELL + BORDER;
            line(BORDER, y, boardPxX, y);
        }
    }

    private void drawCell(Position p, Rgb color) {
        fill(color.red(), color.green(), color.blue());
        rect(p.x() * CELL + BORDER, p.y() * CELL + BORDER, CELL, CELL);
    }

    private void drawDeathOverlay() {
        fill(0, 0, 0, 180);
        rect(0, 0, width, height);
        fill(DEATH_TEXT.red(), DEATH_TEXT.green(), DEATH_TEXT.blue());
        textSize(72);
        textAlign(CENTER, CENTER);
        text("YOU DIED", width / 2f, height / 2f);
    }
}
