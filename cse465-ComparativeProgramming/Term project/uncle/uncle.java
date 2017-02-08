import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Comparative program great uncle/aunt problem
 * @author Nick Venenga et al (see comments in code for sources)
 */
public class uncle {
	public static Node<String> Tree; //easier to store the free up here so it's not getting passed all over the place
	
	public static void main(String args[]) {
		Tree = buildFamilyTree();
		
		//System.out.println(greatUncleAunt("", "", tree));
		System.out.println(greatUncleAunt("male6", "female7"));	
		System.out.println(greatUncleAunt("male6", "male8"));
		System.out.println(greatUncleAunt("female7", "male3"));
		System.out.println(greatUncleAunt("female4", "female7"));
		System.out.println(greatUncleAunt("female4", "male3"));
		System.out.println(greatUncleAunt("male3", "male7"));
		System.out.println(greatUncleAunt("male6", "female7"));
		System.out.println(greatUncleAunt("female7", "male6"));
		System.out.println(greatUncleAunt("male5", "female8"));
		System.out.println(greatUncleAunt("male5", "male8"));
		System.out.println(greatUncleAunt("male8", "female8"));
	}
	
	/**
	 * True if different of two generations
	 * @param uncleAunt
	 * @param person
	 * @return whether it's a great uncle/aunt
	 */
	public static boolean greatUncleAunt(String uncleAunt, String person) {
		return Tree.findDepth(uncleAunt)+2 == Tree.findDepth(person);
	}

	/**
	 * Builds a family free using information about relations
	 * from homework
	 * @return root node of family tree
	 */
	public static Node<String> buildFamilyTree() {
		Node<String> root = new Node<String>("root");
		
		//Create an object for search male
		Node<String> male1 = new Node<String>("male1");
		Node<String> male2 = new Node<String>("male2");
		Node<String> male3 = new Node<String>("male3");
		Node<String> male4 = new Node<String>("male4");
		Node<String> male5 = new Node<String>("male5");
		Node<String> male6 = new Node<String>("male6");
		Node<String> male7 = new Node<String>("male7");
		Node<String> male8 = new Node<String>("male8");
		
		//Create an object for each female
		Node<String> female1 = new Node<String>("female1");
		Node<String> female2 = new Node<String>("female2");
		Node<String> female3 = new Node<String>("female3");
		Node<String> female4 = new Node<String>("female4");
		Node<String> female5 = new Node<String>("female5");
		Node<String> female6 = new Node<String>("female6");
		Node<String> female7 = new Node<String>("female7");
		Node<String> female8 = new Node<String>("female8");
		Node<String> female9 = new Node<String>("female9");
		Node<String> female10 = new Node<String>("female10");
		
		//Level 1
		root.addChild(male1);
		male1.setPartner(female1);
		
		//Level 2
		male1.addChild(male2);
		male2.setPartner(female3);
		
		male1.addChild(male3);
		male3.setPartner(female4);
		
		male1.addChild(female2);
		female2.setPartner(male6);
		
		//Level 3
		male2.addChild(male4);
		male4.setPartner(female6);
		
		male2.addChild(male8);
		male8.setPartner(female8);
		
		male3.addChild(female5);
		male3.addChild(male5);
		
		//Level 4
		male4.addChild(female7);
		
		male4.addChild(male7);
		male7.setPartner(female10);
		
		male8.addChild(female9);
		
		return root;
	}
	
	/**
	 * Node/tree class inspired and heavily modified from:
	 * http://stackoverflow.com/a/3522481
	 * http://opendatastructures.org/
	 * @author Nick Venenga et al. (links above)
	 * @param <String> person's name
	 */
	@SuppressWarnings("hiding")
	public static class Node<String> {
		private String data;
        private Node<String> parent;
        private Node<String> partner;
        private ArrayList<Node<String>> children;
        
        /**
         * Construct a new node with a name
         * and empty list of children
         * @param name of the person (the node represents)
         */
        public Node(String name) {
			data = name;
			children = new ArrayList<Node<String>>();
		}
        
        public String getName() {
        	return data;
        }
        
        public void setPartner(Node<String> n) {
        	partner = n;
        }
        
        /**
         * Adds a child to the node and
         * sets the child's parent as self
         * @param n child to add
         */
        public void addChild(Node<String> n) {
        	children.add(n);
        	n.parent = this;
        }
        
        /**
         * Gets the depth of a node in the tree
         * @param node to find depth of
         * @return depth of node
         */
        public int depth(Node<String> node) {
        	Node<String> root = this;
    		int d = 0;
    		while (node != root) { //count parents
    			node = node.parent;
    			d++;
    		}
    		return d;
    	}
        
        /**
         * Finds a node in the tree
         * Modified from opendatastructures breadth-first search
         * @param name of node to find
         * @return node or null if not found
         */
        public Node<String> find(String name) {
        	//make a pile of nodes
    		Queue<Node<String>> q = new LinkedList<Node<String>>();
    		
    		//put the root in the pile
    		if (this != null) q.add(this);
    		
    		//start looking through the pile
    		while (!q.isEmpty()) {
    			//grab a node off the pile
    			Node<String> u = q.remove();
    			
    			//have we found the node???
    			if(u.data.equals(name) || (u.partner != null && u.partner.data.equals(name))) return u;
    			
    			//this isn't the node you're looking for
    			///...so add some more
    			for(Node<String> c : u.children)
    				q.add(c);
    		}
    		
    		//failure.
    		return null;
    	}
        
        /**
         * Finds a node then returns its depth
         * @param name of node to find
         * @return depth of node
         */
        public int findDepth(String name) {
        	Node<String> n = find(name);
        	if(n == null) return -1; //something went wrong (couldn't find node)
        	return depth(n);
        }
    }
}