public class SmokeTest {

	public static void main(String[] args) {
		System.out.println("HateSnake smoke tests");
		testColor();
		testSnakeInit();
		testSnakeMoveRight();
		testSnakeDeathByWall();
		testAppleInit();
		testAppleHardestCoords();
		System.out.println("ALL TESTS PASSED");
	}

	static void assertEq(Object expected, Object actual, String msg) {
		boolean ok = (expected == null) ? (actual == null) : expected.equals(actual);
		if (!ok) {
			throw new AssertionError(msg + " --- expected=" + expected + " actual=" + actual);
		}
	}

	static void assertTrue(boolean cond, String msg) {
		if (!cond) {
			throw new AssertionError(msg);
		}
	}

	static void testColor() {
		Color c = new Color(10, 20, 30);
		assertEq(10, c.getRed(), "Color.red");
		assertEq(20, c.getGreen(), "Color.green");
		assertEq(30, c.getBlue(), "Color.blue");
		System.out.println("  [OK] Color stores RGB values");
	}

	static void testSnakeInit() {
		Snake s = new Snake(4, 4);
		assertEq(1, s.getSnake().size(), "Snake initial length");
		Integer[] head = s.getHead();
		assertEq(0, head[0], "Snake head x");
		assertEq(0, head[1], "Snake head y");
		assertTrue(!s.isDead(), "new snake is alive");
		assertTrue(!s.hasWon(), "new snake has not won");
		System.out.println("  [OK] Snake initializes at (0,0), length 1");
	}

	static void testSnakeMoveRight() {
		Snake s = new Snake(4, 4);
		Apple a = new Apple(4, 4);
		s.update('d', a);
		Integer[] head = s.getHead();
		assertEq(1, head[0], "after 'd' move, head x");
		assertEq(0, head[1], "after 'd' move, head y");
		assertTrue(!s.isDead(), "alive after single right step");
		System.out.println("  [OK] Snake moves right on 'd' keypress");
	}

	static void testSnakeDeathByWall() {
		Snake s = new Snake(4, 4);
		Apple a = new Apple(4, 4);
		s.update(null, a);
		s.update(null, a);
		s.update(null, a);
		assertTrue(!s.isDead(), "still alive at y=3");
		s.update(null, a);
		assertTrue(s.isDead(), "dead after crossing wall");
		System.out.println("  [OK] Snake dies when it crosses a wall");
	}

	static void testAppleInit() {
		Apple a = new Apple(4, 4);
		int[] coords = a.getCoords();
		assertEq(3, coords[0], "Apple initial x");
		assertEq(3, coords[1], "Apple initial y");
		System.out.println("  [OK] Apple initializes at (boardX-1, boardY-1)");
	}

	static void testAppleHardestCoords() {
		Snake s = new Snake(4, 4);
		Apple a = new Apple(4, 4);
		Integer[] hardest = a.genHardestCoords(s);
		assertTrue(hardest != null, "hardest coords is non-null");
		assertTrue(hardest[0] >= 0 && hardest[0] < 4, "hardest x within board");
		assertTrue(hardest[1] >= 0 && hardest[1] < 4, "hardest y within board");
		System.out.println("  [OK] Apple.genHardestCoords returns a valid square (" + hardest[0] + "," + hardest[1] + ")");
	}
}
