import java.util.LinkedList;

// A simple enumerated class for direction
enum Dir {
	UP, DOWN, LEFT, RIGHT
}

public class Snake {

	// The color of this snake
	Color snakeColor = new Color(0, 255, 0);

	// The current snake
	// Each Integer[] is an xy coordinate
	// Position 0 is the end of the snake
	LinkedList<Integer[]> snake;

	// Whether or not we're invulnerable
	// Used for debugging purposes
	boolean invuln;

	// Whether or not we are currently dead
	boolean dead = false;

	// The current direction
	Dir snakeDir;

	// The board size (used to check for death)
	int boardSizeX;
	int boardSizeY;

	public Snake(int boardSizeX, int boardSizeY) {
		// Create the head of the snake
		this.snake = new LinkedList<Integer[]>();
		this.snake.add(new Integer[2]);
		this.snake.get(0)[0] = 0;
		this.snake.get(0)[1] = 0;

		// Set the initial snake direction
		this.snakeDir = Dir.DOWN;

		// Make us not invulnerable
		this.invuln = false;

		// Save the board size
		this.boardSizeX = boardSizeX;
		this.boardSizeY = boardSizeY;
	}

	public LinkedList<Integer[]> getSnake(){
		return this.snake;
	}

	public Color getColor() {
		return this.snakeColor;
	}

	public void update(Character key, Apple apple) {

		// Change the direction based on the most recent key
		if (key != null) {
			if (key == 'w' && this.snakeDir != Dir.DOWN) {
				this.snakeDir = Dir.UP;
			} else if (key == 'a' && this.snakeDir != Dir.RIGHT) {
				this.snakeDir = Dir.LEFT;
			} else if (key == 's' && this.snakeDir != Dir.UP) {
				this.snakeDir = Dir.DOWN;
			} else if (key == 'd' && this.snakeDir != Dir.LEFT) {
				this.snakeDir = Dir.RIGHT;
			}
		}

		// Save the previous X and Y of the snake head
		int prevX = this.snake.get(this.snake.size() - 1)[0];
		int prevY = this.snake.get(this.snake.size() - 1)[1];

		// Create an array for the new head
		Integer[] nextPos = new Integer[2];
		if (this.snakeDir == Dir.UP) {
			nextPos[0] = prevX;
			nextPos[1] = prevY - 1;
		} else if (this.snakeDir == Dir.LEFT) {
			nextPos[0] = prevX - 1;
			nextPos[1] = prevY;

		} else if (this.snakeDir == Dir.RIGHT) {
			nextPos[0] = prevX + 1;
			nextPos[1] = prevY;

		} else if (this.snakeDir == Dir.DOWN) {
			nextPos[0] = prevX;
			nextPos[1] = prevY + 1;
		}

		// Check if we ate the apple
		Integer[] head = this.getHead();
		if ((head[0] == apple.getCoords()[0]) && (head[1] == apple.getCoords()[1])) {
			apple.update(this);
		} else {
			// If we didn't eat the apple, remove the tail
			this.snake.remove(0);
		}

		// Add the new head and remove the old one
		this.snake.add(nextPos);

		// TODO Check for win

		// Check for death by snake
		for (int i=0; i < (this.snake.size() - 1); i++) {
			Integer[] bodyPiece = this.snake.get(i);
			if ((bodyPiece[0] == nextPos[0]) && (bodyPiece[1] == nextPos[1])) {
				// If we're not invulnerable, mark us as dead
				if (!this.invuln) {
					this.dead = true;
				}
			}
		}

		// Check for death by wall
		if (nextPos[0] < 0 || nextPos[0] >= this.boardSizeX || nextPos[1] < 0 || nextPos[1] >= this.boardSizeY) {
				if (!this.invuln) {
					this.dead = true;
				}
		}

	}

	public Integer[] getHead() {
		return this.snake.get(this.snake.size() - 1);
	}

	public boolean isDead() {
		return this.dead;
	}

	public void debugSnake() {
		System.out.println("\n***** DEBUG SNAKE *****");
		System.out.println("DIR: " + this.snakeDir);
		for (int i = 0; i <= (this.snake.size() - 1); i++) {
			System.out.println("SEG " + i + "\t\t\t(" + this.snake.get(i)[0] + ", " + this.snake.get(i)[1] + ")");
		}
		System.out.println("***** DEBUG SNAKE END *****\n");
	}

}
