import java.awt.Point;

/**
 * Keep track of the costs of different moves
 **/
public class Cost {
	/**
	 * Returns the cost given an Action
	 * @param a maze Action
	 * @return the cost of performing the Action
	 **/
	public static int getCost(Maze m, Action a) {
		// Get the logical target point of the action
		Point logical = a.getTarget();

		int cost = 1;

		if (logical.x == 0 || logical.y == 0 || logical.x == m.getHeight() - 1 || logical.y == m.getWidth() - 1) {
			cost += 10;
		}

		return cost;
	}
}