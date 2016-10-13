import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Maze {
	/**
	 * An array to store the maze details
	 */
	private char[][] maze;
	private int height;
	private int width;

	// Keep track of start and finish points
	private Point start = null;
	private Point finish = null;

	public enum Tile {
		Wall, // A generic wall with unknown location
		InnerWall, // An inner wall in the maze
		OuterWall, // An outside wall of the maze with a higher cost
		Path, // A space that can be walked on
		IntermediatePath, // A blank space indicating you can move that direction
		Start, // The beginning/initial position in the maze
		Finish // The end/goal of the maze
	}

	/**
	* A map of points around a given points
	**/
	private static Map<Character, Point> neighborPositions;
	static {
		Map<Character, Point> pNeighbors = new LinkedHashMap<Character, Point>();
		pNeighbors.put('N', new Point(-1, 0)); // North
		pNeighbors.put('E',	new Point(0, 1)); // East
		pNeighbors.put('S',	new Point(1, 0)); // South
		pNeighbors.put('W',	new Point(0, -1)); // West
		//TODO convert to an immutable map before setting
		Maze.neighborPositions = pNeighbors;
	}

	public Maze(String fileName) {
		FileReader in = null;
		try {
			// Open up the maze text file
			in = new FileReader(fileName);
			BufferedReader reader = new BufferedReader(in);

			// Get the sizes from the first line
			String sizeLine = reader.readLine();

			// Get height and width of the maze
			this.height = Integer.parseInt(sizeLine.split(" ")[0]);
			this.width = Integer.parseInt(sizeLine.split(" ")[1]);
			maze = new char[height*2 + 1][width*2 + 1];

			// Hold the current line
			String line;

			// Read the maze now
			int i = 0;
			while ((line = reader.readLine()) != null) {
				for (int j = 0; j < line.length(); j++) {
					// Add spot to the maze array
					maze[i][j] = line.charAt(j);

					// Look for maze starting position
					if(maze[i][j] == 'S') {
						this.start = new Point(i,j);
					}
					// Look for maze ending position
					else if (maze[i][j] == 'F') {
						this.finish = new Point(i,j);
					}
				}

				// Line finished
				i++;
			}

			// Close reader
			reader.close();
		} catch (IOException e) {
			// Print debugging information
			e.printStackTrace();
			// Print error for the user
			System.out.println("Error reading the maze file!");
			// Can't continue without the maze
			System.exit(1);
		} finally {
			// Close the file resource
			try {
				if(in != null)
					in.close();
			}
			catch (Exception e) {
				//Well, we're screwed if we make it here and there's really nothing left to do
			}
		}

		// Check to make sure the maze has been initialized correctly
		// Check for start coords
		if(this.start == null) {
			System.out.println("Couldn't find a start point in the supplied maze file! Exiting.");
			System.exit(1);
		}
		// Check for end coords
		if(this.finish == null) {
			System.out.println("Couldn't find an end point in the supplied maze file! Exiting.");
			System.exit(1);
		}
	}

	/**
	 * Convert from a point in the logical maze to a point in
	 * the physical/array representation
	 * @param logical point in the maze
	 * @return physical point in the maze array
	 **/
	private Point coordConvert(Point logical) {
		return new Point(logical.x*2 + 1, logical.y*2 + 1);
	}

	/**
	* Converts a point in the physical/array maze to a logical
	* point used for searching
	* @param physical point in the maze array
	* @return logical point
	**/
	//9,1
	private Point coordConvertRev(Point physical) {
		return new Point((physical.x-1)/2, (physical.y-1)/2);
	}

	/**
	* Returns the starting position of the maze
	* @return logical starting point
	**/
	public Point getStart() {
		return this.coordConvertRev(this.start);
	}

	/**
	* Returns the logical finish point of the maze
	* @return logical finish point
	**/
	public Point getFinish() {
		return this.coordConvertRev(this.finish);
	}

	/**
	* Returns the character at a point in the
	* physical maze
	* @param a physical point
	* @return the character at that location
	**/
	private char getTile(Point physical) {
		return this.maze[physical.x][physical.y];
	}

	/**
	* Returns the width of the logical maze
	* @return int width
	**/
	public int getWidth() {
		return this.width;
	}

	/**
	* Returns the height of the logical maze
	* @return int height
	**/
	public int getHeight() {
		return this.height;
	}

	/**
	* Returns a list of actions
	* @param logical point to find actions around
	* @return list of actions
	**/
	public List<Action> getActions(Point logical) {
		Point physical = this.coordConvert(logical);
		List<Action> availableActions = new LinkedList<Action>();
		for(Character a : Maze.neighborPositions.keySet()) {
			// Get the point offset from the possible neighbor list
			Point pN = Maze.neighborPositions.get(a);
			// Find out the new neighbor point
			Point newPoint = new Point(physical.x + pN.x, physical.y + pN.y);
			// Make sure the point is on the physical maze
			if(this.getTile(newPoint) != ' ') {
				// Skip it, it's not a valid space
				continue;
			}
			// Create an action for the new neighbor
			Action act = new Action(this, a, new Point(logical.x + pN.x, logical.y + pN.y));
			availableActions.add(act);
		}

		return availableActions;
	}

	/**
	* Pretty print the maze
	* @return string representation of the maze
	**/
	public String toString() {
		StringBuilder out = new StringBuilder("Maze contents: \n");

		for(int i = 0; i < maze.length; i++) {
			for(int j = 0; j < maze[i].length; j++) {
				out.append(maze[i][j]);
			}
			out.append("\n");
		}
		out.append("\n");

		return out.toString();
	}
}
