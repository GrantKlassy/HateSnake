import java.util.LinkedList;

import processing.core.PApplet;

public class HateSnake extends PApplet {

	// The current state of the game
	// 0 = Not yet started
	// 1 = Running
	// 2 = Dead (lockout before auto-reset)
	int state = 0;

	// Used to hold character input to be processed
	LinkedList<Character> keyQueue = new LinkedList<Character>();

	// Constants for the game
	final int boardSizeX = 10;
	final int boardSizeY = 10;
	final int scale = 50;
	final int border = 10;

	// The snake for this game
	Snake snake = new Snake(this.boardSizeX, this.boardSizeY);

	// The apple for this game
	Apple apple = new Apple(this.boardSizeX, this.boardSizeY);

	// Used to slow down or speed up the game
	// Higher speed = SLOWER. Is there a good way to fix this?
	final int speed = 6;
	int speedCnt = 0; // (A counter used to delay execution)

	// Grace window: when the next tick would kill us, freeze the snake for
	// a short time so the player has a chance to turn away.
	int graceFrames = 0;
	final int maxGraceFrames = 30; // ~0.5s at 60fps

	// Death lockout: frame number of death + how long we wait before reset.
	int deathFrame = 0;
	final int deathLockoutFrames = 180; // 3s at 60fps

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
		this.size((this.boardSizeX * this.scale) + (this.border * 2), (this.boardSizeY * this.scale) + (this.border * 2));
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
			int lineX = (i * this.scale) + this.border;
			int lineY = (this.boardSizeY * this.scale) + this.border;
			this.line(lineX, this.border, lineX, lineY);
		}
		// Draw the horizontal lines on the board
		for (int i = 0; i <= this.boardSizeY; i++) {
			int lineY = (i * this.scale) + this.border;
			int lineX = (this.boardSizeX * this.scale) + this.border;
			this.line(this.border, lineY, lineX, lineY);
		}

		// Draw the first position of the snake and apple
		for (Integer[] snakePos : this.snake.getSnake()) {
			int snakeX = snakePos[0];
			int snakeY = snakePos[1];
			this.fill(this.snake.getColor().getRed(), this.snake.getColor().getGreen(), this.snake.getColor().getBlue());
			this.rect((snakeX * this.scale) + this.border, (snakeY * this.scale) + this.border, this.scale, this.scale);
		}

		// Draw the apple
		this.fill(this.apple.getColor().getRed(), this.apple.getColor().getGreen(), this.apple.getColor().getBlue());
		int[] appCoords = this.apple.getCoords();
		this.rect((appCoords[0] * this.scale) + this.border, (appCoords[1] * this.scale) + this.border, this.scale, this.scale);
	}

	/* (non-Javadoc)
	 * @see processing.core.PApplet#draw()
	 */
	public void draw() {
		// If the game is currently running...
		if (this.state == 1) {

			if (this.speedCnt % this.speed == 0) {

				// Check to see if we've won
				if (this.snake.hasWon()) {
					// TODO Something more than just exit
					System.out.println("You won!");
					System.exit(0);
				}

				// Grace check: while in grace, peek the most recent input so
				// a last-second turn can save us. Otherwise peek the head.
				Character peekKey = this.graceFrames > 0
						? this.keyQueue.peekLast()
						: this.keyQueue.peek();
				Integer[] peekPos = this.snake.peekNextPos(peekKey);

				if (this.snake.wouldDieAt(peekPos)) {
					if (this.graceFrames < this.maxGraceFrames) {
						this.graceFrames++;
						// Don't advance speedCnt --- recheck next frame so
						// the save triggers as soon as the player reacts.
						return;
					}
					// Grace expired --- fall through and let the snake die.
				}

				// Escaped grace: collapse the queue to the most recent key so
				// the update below consumes the player's save, not a stale one.
				if (this.graceFrames > 0 && !this.keyQueue.isEmpty()) {
					Character last = this.keyQueue.peekLast();
					this.keyQueue.clear();
					if (last != null) {
						this.keyQueue.add(last);
					}
				}
				this.graceFrames = 0;

				// Save the previous tail position
				Integer tailX = this.snake.getSnake().get(0)[0];
				Integer tailY = this.snake.getSnake().get(0)[1];

				// Update the snake (do this before erasing the tail so that if
				// we die, the last-alive frame stays on screen under the overlay).
				Character key = null;
				if (this.keyQueue.peek() != null) {
					key = this.keyQueue.remove();
				}
				this.snake.update(key, this.apple);

				// Check to see if the snake died
				if (this.snake.isDead()) {
					this.state = 2;
					this.deathFrame = this.frameCount;
					this.drawDeathOverlay();
					this.speedCnt++;
					return;
				}

				// Erase the tail (safe now that we know we survived)
				this.fill(this.backgroundColor.getRed(), this.backgroundColor.getGreen(), this.backgroundColor.getBlue());
				this.rect((tailX * this.scale) + this.border, (tailY * this.scale) + this.border, this.scale, this.scale);


				// If the apple was updated...
				if (this.apple.wasUpdated()) {
					// Erase the old apple
					this.fill(this.backgroundColor.getRed(), this.backgroundColor.getGreen(), this.backgroundColor.getBlue());
					int[] oldAplCoords = this.apple.getLastCoords();
					this.rect((oldAplCoords[0] * this.scale) + this.border, (oldAplCoords[1] * this.scale) + this.border, this.scale, this.scale);

					// Draw the new apple
					this.fill(this.apple.getColor().getRed(), this.apple.getColor().getGreen(), this.apple.getColor().getBlue());
					int[] appCoords = this.apple.getCoords();
					this.rect((appCoords[0] * this.scale) + this.border, (appCoords[1] * this.scale) + this.border, this.scale, this.scale);

				}

				// Draw the new snake
				for (Integer[] snakePos : this.snake.getSnake()) {
					int snakeX = snakePos[0];
					int snakeY = snakePos[1];
					this.fill(this.snake.getColor().getRed(), this.snake.getColor().getGreen(), this.snake.getColor().getBlue());
					this.rect((snakeX * this.scale) + this.border, (snakeY * this.scale) + this.border, this.scale, this.scale);
				}


			}
			// TODO Reset speed count to avoid overflow?
			// TODO Or maybe use System.timeout() here instead of a counter?
			this.speedCnt++;
		} else if (this.state == 2) {
			// Dead: hold the overlay, then reset after the lockout.
			if (this.frameCount - this.deathFrame >= this.deathLockoutFrames) {
				this.resetGame();
			}
		}
	}

	private void drawDeathOverlay() {
		this.fill(0, 0, 0, 180);
		this.rect(0, 0, this.width, this.height);
		this.fill(255, 0, 0);
		this.textSize(72);
		this.textAlign(CENTER, CENTER);
		this.text("YOU DIED", this.width / 2f, this.height / 2f);
	}

	private void resetGame() {
		this.snake = new Snake(this.boardSizeX, this.boardSizeY);
		this.apple = new Apple(this.boardSizeX, this.boardSizeY);
		this.keyQueue.clear();
		this.speedCnt = 0;
		this.graceFrames = 0;
		this.deathFrame = 0;
		this.setup();
	}

	/* (non-Javadoc)
	 * @see processing.core.PApplet#keyPressed()
	 */
	public void keyPressed() {

		// Don't accept input during the death lockout.
		if (this.state == 2) {
			return;
		}

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
