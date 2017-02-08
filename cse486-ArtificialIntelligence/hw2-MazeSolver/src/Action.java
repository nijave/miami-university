import java.awt.Point;

public class Action {
		/**
		* The cost of performing the Action
		**/
		private int cost;

		/**
		* What the action is (i.e. N, S, E, W)
		**/
		private char action;

		/**
		* The result of the action (i.e. a new point)
		**/
		private Point target;

		public Action(Maze m, char action, Point target) {
			this.action = action;
			this.target = target;
			this.cost = Cost.getCost(m, this);
		}

		public int getCost() {
			return this.cost;
		}

		public char getAction() {
			return this.action;
		}

		public Point getTarget() {
			return this.target;
		}

		public String toString() {
			return "Action: " + this.getAction() + "; Cost: " + this.getCost() + "; Target: " + this.getTarget();
		}
	}