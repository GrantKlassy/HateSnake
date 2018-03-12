
/**
 * A simple Color class for use with HateSnake and PApplet
 *
 */
public class Color {

	// The current RGB Color values
	private int red;
	private int green;
	private int blue;

	public int getRed() {
		return this.red;
	}

	public void setRed(int red) {
		this.red = red;
	}

	public int getGreen() {
		return this.green;
	}

	public void setGreen(int green) {
		this.green = green;
	}

	public int getBlue() {
		return this.blue;
	}

	public void setBlue(int blue) {
		this.blue = blue;
	}

	public Color() {
		this.red = 0;
		this.green = 0;
		this.blue = 0;
	}

	public Color(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

}
