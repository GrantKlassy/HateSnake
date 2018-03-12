import java.util.LinkedList;
import java.util.Queue;

import processing.core.PApplet;

public class HateSnake extends PApplet {

	// The current state of the game
	// 0 = Not yet started
	// 1 = Running
	// 2 = Dead
	int state = 0;

	// Used to hold character input to be processed
	Queue<Character> keyQueue = new LinkedList<Character>();

	// Constants for the game
	final int boardSizeX = 5;
	final int boardSizeY = 5;
	final int scale = 50;

	// The snake for this game
	Snake snake = new Snake(this.boardSizeX, this.boardSizeY);

	// The apple for this game
	Apple apple = new Apple(this.boardSizeX, this.boardSizeY);

	// Used to slow down or speed up the game
	// Higher speed = SLOWER. Is there a good way to fix this?
	final int speed = 15;
	int speedCnt = 0; // (A counter used to delay execution)

	// The colors for the background and grid lines
	Color backgroundColor = new Color(105, 105, 105);
	Color lineColor = new Color(255, 255, 255);

	public static void main(String[] args) {
		PApplet.main("HateSnake");
	}

	/* (non-Javadoc)
	 * @see processing.core.PApplet#settings()
	 */
	public void settings() {
		// Set up the size of the windows
		this.size((this.boardSizeX * this.scale), (this.boardSizeY * this.scale));
	}

	/* (non-Javadoc)
	 * @see processing.core.PApplet#setup()
	 */
	public void setup() {
		// Set the color mode to RGB
		this.colorMode(1);

		// Set the game state to 0
		this.state = 0;

		// Set up the background
		this.background(this.backgroundColor.getRed(), this.backgroundColor.getGreen(), this.backgroundColor.getBlue());

		// Draw the vertical lines on the board
		this.fill(this.lineColor.getRed(), this.lineColor.getGreen(), this.lineColor.getBlue());
		for (int i = 0; i <= this.boardSizeX; i++) {
			int lineX = i * this.scale;
			int lineY = this.boardSizeY * this.scale;
			this.line(lineX, 0, lineX, lineY);
		}
		// Draw the horizontal lines on the board
		for (int i = 0; i <= this.boardSizeY; i++) {
			int lineY = i * this.scale;
			int lineX = this.boardSizeX * this.scale;
			this.line(0, lineY, lineX, lineY);
		}

		// Draw the first position of the snake and apple
		for (Integer[] snakePos : this.snake.getSnake()) {
			int snakeX = snakePos[0];
			int snakeY = snakePos[1];
			this.fill(this.snake.getColor().getRed(), this.snake.getColor().getGreen(), this.snake.getColor().getBlue());
			this.rect((snakeX * this.scale), (snakeY * this.scale), this.scale, this.scale);
		}

		// Draw the apple
		this.fill(this.apple.getColor().getRed(), this.apple.getColor().getGreen(), this.apple.getColor().getBlue());
		int[] appCoords = this.apple.getCoords();
		this.rect((appCoords[0] * this.scale), (appCoords[1] * this.scale), this.scale, this.scale);
	}

	/* (non-Javadoc)
	 * @see processing.core.PApplet#draw()
	 */
	public void draw() {
		// If the game is currently running...
		if (this.state == 1) {

			if (this.speedCnt % this.speed == 0) {
				// Save the previous tail position
				Integer tailX = this.snake.getSnake().get(0)[0];
				Integer tailY = this.snake.getSnake().get(0)[1];

				// Erase the tail
				this.fill(this.backgroundColor.getRed(), this.backgroundColor.getGreen(), this.backgroundColor.getBlue());
				this.rect((tailX * this.scale), (tailY * this.scale), this.scale, this.scale);

				// Update the snake
				Character key = null;
				if (this.keyQueue.peek() != null) {
					key = this.keyQueue.remove();
				}
				this.snake.update(key, this.apple);

				// Check to see if the snake died
				if (this.snake.isDead()) {
					// TODO Something more than just exit
					System.exit(0);
				}

				// If the apple was updated...
				if (this.apple.wasUpdated()) {
					// Erase the old apple
					this.fill(this.backgroundColor.getRed(), this.backgroundColor.getGreen(), this.backgroundColor.getBlue());
					int[] oldAplCoords = this.apple.getLastCoords();
					this.rect((oldAplCoords[0] * this.scale), (oldAplCoords[1] * this.scale), this.scale, this.scale);

					// Draw the new apple
					this.fill(this.apple.getColor().getRed(), this.apple.getColor().getGreen(), this.apple.getColor().getBlue());
					int[] appCoords = this.apple.getCoords();
					this.rect((appCoords[0] * this.scale), (appCoords[1] * this.scale), this.scale, this.scale);

				}

				// Draw the new snake
				for (Integer[] snakePos : this.snake.getSnake()) {
					int snakeX = snakePos[0];
					int snakeY = snakePos[1];
					this.fill(this.snake.getColor().getRed(), this.snake.getColor().getGreen(), this.snake.getColor().getBlue());
					this.rect((snakeX * this.scale), (snakeY * this.scale), this.scale, this.scale);
				}

			}
			// TODO Reset speed count to avoid overflow?
			// TODO Or maybe use System.timeout() here instead of a counter?
			this.speedCnt++;
		}
	}

	/* (non-Javadoc)
	 * @see processing.core.PApplet#keyPressed()
	 */
	public void keyPressed() {

		// If we're not yet running, run the game
		if (this.state == 0) {
			this.state = 1;
		}

		// If the key is coded, convert arrow keys into WSAD
		char myKey = '0';
		if (this.key == CODED) {
			if (this.keyCode == UP) {
				myKey = 'w';
			} else if (this.keyCode == DOWN) {
				myKey = 's';
			} else if (this.keyCode == LEFT) {
				myKey = 'a';
			} else if (this.keyCode == RIGHT) {
				myKey = 'd';
			}
		} else {
			// If it's not coded, just save the raw character
			myKey = this.key;
		}

		// If the key is empty, add it
		if (this.keyQueue.peek() == null) {
			this.keyQueue.add(myKey);
		} else if (!this.keyQueue.peek().equals(myKey)) {
			// If the queue isn't empty, only add if it's different than the top
			this.keyQueue.add(myKey);
		}

	}

}
