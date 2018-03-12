import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class Apple {
	// Coordinates of the current apple
	// Index 0 = x, 1 = y
	int[] coords;

	// The previous coordinates of the apple
	int[] lastCoords;

	// Whether or not we were updated
	// This is used by HateSnake to figure out when to erase the old apple
	boolean updated = true;

	// The color of the apple
	Color appleColor = new Color(255, 0, 0);

	// The X and Y board size
	int boardSizeX;
	int boardSizeY;

	public Apple(int boardSizeX, int boardSizeY) {
		this.coords = new int[2];
		this.lastCoords = new int[2];
		this.coords[0] = (boardSizeX - 1);
		this.coords[1] = (boardSizeY - 1);
		// Note that last coords are set to our initial coords
		// to avoid checking an initial case in HateSnake
		this.lastCoords[0] = (boardSizeX - 1);
		this.lastCoords[1] = (boardSizeY - 1);

		this.boardSizeX = boardSizeX;
		this.boardSizeY = boardSizeY;
	}

	public int[] getCoords() {
		return this.coords;
	}

	public Color getColor() {
		return this.appleColor;
	}

	/**
	 * Updates the updated flag and last coordinates, and sets new coordinates.
	 * This should only be called by Snake's update() method when it detects
	 * that it ate this apple.
	 *
	 * @param snake: The snake to generate coords for
	 */
	public void update(Snake snake) {
		// Update the updated flag and save our previous coordinates
		this.updated = true;
		this.lastCoords[0] = this.coords[0];
		this.lastCoords[1] = this.coords[1];

		// Set our new coords
		Integer[] hardestCoords = this.genHardestCoords(snake);
		this.coords[0] = hardestCoords[0];
		this.coords[1] = hardestCoords[1];

	}

	/**
	 * Returns the previous coordinates for the apple. Used by HateSnake
	 * to know where to erase the previous apple
	 *
	 * @return the previous coordinates
	 */
	public int[] getLastCoords() {
		return this.lastCoords;
	}

	/**
	 * Whether or not our position was updated. Used by HateSnake to know
	 * when to erase the last apple. Also resets the updated flag.
	 *
	 * @return Whether or not we were updated
	 */
	public boolean wasUpdated() {
		this.updated = !this.updated;
		return (!this.updated);
	}

	/**
	 * Generates the hardest coordinates for the next apple to spawn. Uses Dijkstra's
	 * to find the shortest path to all other squares, and then picks the longest of those.
	 * Squares that cannot currently be reached are always chosen.
	 *
	 * NOTE: I used http://www.baeldung.com/jgrapht for help with jgrapht
	 *
	 * @param snake: The snake for this game board
	 * @return The hardest coordinates for the new apple (hopefully)
	 */
	public Integer[] genHardestCoords(Snake snake) {
		// Create a new JGraphT Graph
		Graph<String, DefaultEdge> g = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);

		// Add vertices based off the board
		for (int x = 0; x < this.boardSizeX; x++) {
			for (int y = 0; y < this.boardSizeY; y++) {
				// Create the name of the vertex ("x y")
				String verName = "" + x + " " + y;
				g.addVertex(verName);

				// Try to add edges with all 4 neighbors
				// Exceptions here are expected because we aren't doing
				// any error checking before trying to add an edge
				String leftVer = "" + (x - 1) + " " + y;
				try {
					g.addEdge(verName, leftVer);
				} catch (Exception e) {
				}
				String rightVer = "" + (x + 1) + " " + y;
				try {
					g.addEdge(verName, rightVer);
				} catch (Exception e) {
				}
				String upVer = "" + x + " " + (y - 1);
				try {
					g.addEdge(verName, upVer);
				} catch (Exception e) {
				}
				String downVer = "" + x + " " + (y + 1);
				try {
					g.addEdge(verName, downVer);
				} catch (Exception e) {
				}
			}
		}

		// Figure out the snake vertices
		// Remove all the vertices EXCEPT for the snake head
		for (int i = 0; i < (snake.getSnake().size() - 1); i++) {
			Integer[] snakePart = snake.getSnake().get(i);
			String snakeVer = "" + snakePart[0] + " " + snakePart[1];
			g.removeVertex(snakeVer);
		}
		Integer[] snakeHead = snake.getSnake().get(snake.getSnake().size() - 1);
		String headVert = "" + snakeHead[0] + " " + snakeHead[1];

		// Use Dijkstras to find the shortest path to all other squares
		DijkstraShortestPath<String, DefaultEdge> djk = new DijkstraShortestPath<String, DefaultEdge>(g);
		ShortestPathAlgorithm.SingleSourcePaths<String, DefaultEdge> paths = djk.getPaths(headVert);

		// Find which of the shortest paths is the longest
		int longestLength = -1;
		String longestVertex = "";
		for (String s : g.vertexSet()) {

			// If the path is null, there's no way to get to that square
			// This should be harder than anything you can get to, so we should do this
			if (paths.getPath(s) == null) {
				longestLength = 500;
				longestVertex = s;
			} else if (paths.getPath(s).getLength() > longestLength) {
				longestLength = paths.getPath(s).getLength();
				longestVertex = s;
			}
		}

		String[] hardestAppleStr = longestVertex.split(" ");
		Integer[] hardestApple = new Integer[2];
		hardestApple[0] = Integer.parseInt(hardestAppleStr[0]);
		hardestApple[1] = Integer.parseInt(hardestAppleStr[1]);

		return hardestApple;
	}
}
