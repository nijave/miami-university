import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Search {
	private Maze maze;
	
	public static void main(String[] args) {
		if(args.length < 1) {
			for(int i = 1; i <= 8; i++) {
				System.out.println("Searching maze " + i);
				new Search("maze" + i + ".txt");
				System.out.println();
			}
		}
		else {
			new Search(args[0]);
		}
	}
	
	public Search(String filename) {
		maze = new Maze(filename);
		System.out.println("Breadth First Search");
		System.out.println(this.search("Breadth"));
		System.out.println("Depth First Search");
		System.out.println(this.search("Depth"));
		System.out.println("Uniform Cost Search");
		System.out.println(this.search("UniformCost"));
	}
	
	private Node search(String searchType) {
		//int iterationCount = 0;
		// Create the starting node or "root"
		Node start = new Node(maze.getStart(), 0, "", 0);
		// Create a queue
		Queue<Node> frontier = new PriorityQueue<Node>();
		// Add start point as the only item in the frontier
		frontier.add(start);
		
		// Explorered list
		List<Point> explored = new LinkedList<Point>();
		
		while(!frontier.isEmpty()) {
			// Always remove the oldest element
			Node dequeue = frontier.remove();
			
			// Check for the goal/finish
			if(dequeue.getPoint().equals(maze.getFinish())) {
				// Return found solution
				return dequeue;
			}
			
			// Add to explored list
			explored.add(dequeue.getPoint());
			
			for(Action a : maze.getActions(dequeue.getPoint())) {
				int cost = dequeue.getCost() + Cost.getCost(maze, a);
				String path = dequeue.getPath() + a.getAction();
				int priority = 0;
				switch(searchType.charAt(0)) {
					case 'B':
						priority = 0;
						break;
					case 'D':
						priority = dequeue.getPriority()+1;
						break;
					case 'U':
						priority = -1*cost;
						break;
				}
				// Create a new node with the action and cost information
				Node enqueue = new Node(
						a.getTarget(), 
						cost, 
						path,
						priority);
				// Check if node has been explored
				if(this.explored(explored, enqueue.getPoint())) {
					// do nothing, already explored this location
				}
				else {
					// Add the node to the queue
					frontier.add(enqueue);
				}
				//iterationCount++;
			}
		}
		
		// No solution found
		return null;
	}
	
	private boolean explored(List<Point> explored, Point p) {
		for(Point e : explored) {
			if(p.equals(e))
				return true;
		}
		return false;
	}
	
	private class Node implements Comparable<Node> {
		private Point point;
		private int cost;
		private String path;
		private int priority;
		
		public Node(Point pnt, int c, String pth, int priority) {
			this.point = pnt;
			this.cost = c;
			this.path = pth;
			this.priority = priority;
		}
		
		public Point getPoint() {
			return this.point;
		}
		
		public int getCost(){
			return this.cost;
		}
		
		public String getPath() {
			return this.path;
		}
		
		public int getPriority() {
			return this.priority;
		}
		
		public String toString() {
			return "Node: " + this.getPoint() + "; Cost: " + this.getCost() + "; Path: " + this.getPath();
		}

		@Override
		public int compareTo(Node other) {
			return Math.abs(this.priority) - Math.abs(other.priority);
		}
	}
}
