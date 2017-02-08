import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Solves Sudoku puzzles using recursive backtracking with MRV heuristic
 * 
 * @author Nick Venenga
 *
 */
public class Sudoku implements Cloneable {
  /**
   * Create a multi dimensional array to hold the puzzle spots.
   */
  private int[][] puzzle;
  /**
   * Keep track of the overall puzzle dimensions (width and height).
   */
  private int puzzleSize;
  /**
   * Keep track of the dimensions of each region/quadrant.
   */
  private int quadrantSize;
  /**
   * Keep track of how many squares have been completed.
   */
  private int squaresLeft;

  // Sets to keep track of the values of rows, columns, and quadrants
  private List<Set<Integer>> rows;
  private List<Set<Integer>> cols;
  private List<Set<Integer>> quads;

  /**
   * Start a new puzzle using command line arguments.
   * 
   * @param args puzzle text file
   */
  public static void main(String[] args) {
    if (args.length != 1) {
      throw new IllegalArgumentException("Usage: java Sudoku puzzles/puzzleName.txt");
    }
    // System.out.println("\n\n\nSolving: " + args[0]);
    Sudoku puz = new Sudoku(args[0]);
    puz.solve();
  }

  /**
   * Private constructor for cloning.
   * 
   * @param puzzle that's already filled in
   */
  private Sudoku(int[][] puzzle) {
    this.puzzle = puzzle;
  }

  /**
   * Creates a new Sudoku puzzle given a file with puzzle information.
   * 
   * @param filename containing Sudoku puzzle
   */
  public Sudoku(String filename) {
    // Open the input file
    Scanner in = null;
    try {
      in = new Scanner(new File(filename));
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
      // Exit if the input file can't be opened
      System.exit(1);
    }
    // Get the first int which is puzzle size
    this.puzzleSize = in.nextInt();
    // Get the second int which is quadrant/region size
    this.quadrantSize = in.nextInt();
    // Initialize squares left counter
    this.squaresLeft = 0;
    // Create a new array of this size
    this.puzzle = new int[this.puzzleSize][this.puzzleSize];
    // Skip over the blank line
    in.nextLine();
    // Initialize all sets
    this.rows = new LinkedList<Set<Integer>>();
    this.cols = new LinkedList<Set<Integer>>();
    this.quads = new LinkedList<Set<Integer>>();
    for (int i = 0; i < this.getSize(); i++) {
      rows.add(this.getFullDomain());
      cols.add(this.getFullDomain());
      quads.add(this.getFullDomain());
    }
    // Keep track of which row/line we're on
    int row = 0;
    // Loop over each row/line
    while (in.hasNextLine()) {
      // Get the line contents
      String line = in.nextLine();
      // Replaces - with 0 and change to char array
      String[] parts = line.replaceAll("-", "0").split("(?!^)");
      // Populate the puzzle array
      for (int i = 0; i < parts.length; i++) {
        int val = Integer.parseInt(parts[i]);
        this.puzzle[row][i] = val;
        this.rows.get(row).remove(val);
        this.cols.get(i).remove(val);
        this.quads.get(this.getQuadrant(row, i) - 1).remove(val);
        // Check to see if the square is blank
        if (val == 0) {
          // Increment squares left counter
          this.squaresLeft++;
        }
      }
      // This line is complete, increment to next
      row++;
    }

    // Close the input file
    in.close();

    // Check to see if the initial puzzle is valid
    try {
      this.checkValid();
    } catch (IllegalStateException e) {
      System.out.println("Invalid puzzle provided.");
      System.out.println(e.getMessage());
      System.exit(1);
    }
  }

  /**
   * Fills the square at row, col with the value val
   * 
   * @param row of the sudoku board
   * @param col of the sudoku board
   * @param val to fill in
   */
  public boolean fill(int row, int col, int val) {
    // Save the old value before changes
    int old = this.puzzle[row][col];

    // Fill in the new value
    this.puzzle[row][col] = val;

    try {
      // Check to see if the puzzle follows the puzzle rules
      // ** It turns out there's no reason to do this since accurately
      // keeping track of domains remains that there should never
      // even be the possibility for an illegal move
      // Having an illegal move implies there're problems elsewhere.
      // this.quickCheckValid(row, col);
      // Update constraints
      this.rows.get(row).remove(val);
      this.cols.get(col).remove(val);
      this.quads.get(this.getQuadrant(row, col) - 1).remove(val);
      // There is one less square left now
      this.squaresLeft--;
      return true;
    } catch (IllegalStateException e) {
      // System.out.println(e.getMessage());
      // Undo the last value that brought the puzzle into an illegal state
      this.puzzle[row][col] = old;
      return false;
    }
  }

  /**
   * Gets the size of the puzzle 9x9 -> 9
   * 
   * @return puzzle width/height
   */
  public int getSize() {
    return this.puzzleSize;
  }

  /**
   * Returns a domain set of all possible values for this game
   * 
   * @return domain with all values
   */
  private Set<Integer> getFullDomain() {
    Set<Integer> fullDomain = new HashSet<Integer>();
    for (int i = 1; i <= this.getSize(); i++) {
      fullDomain.add(i);
    }
    return fullDomain;
  }

  /**
   * Returns the size of the Sudoku quadrant (3 for 9x9)
   * 
   * @return int quadrant size
   */
  private int getQuadrantSize() {
    return this.quadrantSize;
  }

  /**
   * Returns the Sudoku "quadrant" a given row and column are in
   * 
   * @param row
   * @param col
   * @return sudoku quadrant
   */
  private int getQuadrant(int row, int col) {
    return row / this.getQuadrantSize() * this.getQuadrantSize() + col / this.getQuadrantSize() + 1;
  }

  /**
   * Returns the number of unassigned squares on the sudoku puzzle
   * 
   * @return squares left
   */
  private int getSquaresLeft() {
    return this.squaresLeft;
  }

  /**
   * Returns the next point that should be worked on
   * 
   * @return coordinates of first available tile
   */
  @SuppressWarnings("unused")
  private Point getNextVariable() {
    // Find the first unassigned variable
    for (int i = 0; i < this.getSize(); i++) {
      for (int j = 0; j < this.getSize(); j++) {
        if (this.puzzle[i][j] == 0) {
          return new Point(j, i);
        }
      }
    }
    // Couldn't find a point
    return null;
  }

  /**
   * Returns the next point with the most constraints
   * 
   * @return Point with most constraints
   */
  private Point getNextVariableMRV() {
    Point next = new Point();
    int domainSize = this.getSize() + 1;
    for (int i = 0; i < this.getSize(); i++) {
      for (int j = 0; j < this.getSize(); j++) {
        // Check for blank square
        if (this.puzzle[i][j] == 0) {
          // Get the domain size of the current square
          int _domainSize = this.getDomain(i, j).size();
          if (_domainSize < domainSize) {
            next.y = i;
            next.x = j;
            domainSize = _domainSize;
          }
          // Stop searching if min found
          if (domainSize == 1) {
            break;
          }
        }
      }
      // Can't be less than 1--stop looking
      if (domainSize == 1) {
        break;
      }
    }

    return next;
  }

  /**
   * Returns the domain of a square by finding the intersection of all
   * relevent constraints
   * 
   * @param row
   * @param col
   * @return domain of possible values
   */
  private Set<Integer> getDomain(int row, int col) {
    Set<Integer> domain = this.getFullDomain();
    domain.retainAll(this.rows.get(row));
    domain.retainAll(this.cols.get(col));
    domain.retainAll(this.quads.get(this.getQuadrant(row, col) - 1));
    return domain;
  }

  /**
   * Have puzzle solve itself
   * 
   * @return whether the puzzle was solved
   */
  public boolean solve() {
    return this.solve(this);
  }

  /**
   * Uses recursive backtracking to try to solve the puzzle
   * 
   * @return whether the puzzle was solved
   */
  private boolean solve(Sudoku puz) {
    // **Check to see if all squares have been assigned (base case/break loop)
    if (puz.getSquaresLeft() == 0) {
      // System.out.println("Solved:");
      System.out.println(puz);
      return true;
    }

    // **Get the next tile to work with
    // Point next = puz.getNextVariable();
    Point next = puz.getNextVariableMRV();

    // **Get the domain of the tile
    Set<Integer> domain = puz.getDomain(next.y, next.x);

    // **Iterate over domain values
    for (int i : domain) {
      // **Create a copy of the puzzle
      Sudoku copy = puz.clone();

      // **Make the change based on the domain value
      // **Forward checking (check constraints are satisfied)
      if (copy.fill(next.y, next.x, i)) {
        puz.solve(copy);
      }
    }
    return false;
  }

  /**
   * Checks to make sure there are valid values in each
   * 
   * @throws IllegalStateException if the puzzle violates Sudoku rules
   */
  private void checkValid() throws IllegalStateException {
    // Sets to keep track of the values of rows, columns, and quadrants
    List<Set<Integer>> rows = new LinkedList<Set<Integer>>();
    List<Set<Integer>> cols = new LinkedList<Set<Integer>>();
    List<Set<Integer>> quads = new LinkedList<Set<Integer>>();

    // Initialize all sets
    for (int i = 0; i < this.getSize(); i++) {
      rows.add(new HashSet<Integer>());
      cols.add(new HashSet<Integer>());
      quads.add(new HashSet<Integer>());
    }

    // Make sure rows don't have duplicate values
    for (int i = 0; i < this.puzzle.length; i++) {
      for (int j = 0; j < this.puzzle[i].length; j++) {
        // Check to see if the square if set
        if (this.puzzle[i][j] != 0) {
          // Check row
          if (!rows.get(i).add(this.puzzle[i][j])) {
            throw new IllegalStateException("Invalid row " + i + " at column " + j);
          }

          // Check column
          if (!cols.get(j).add(this.puzzle[i][j])) {
            throw new IllegalStateException("Invalid column " + j + " at row " + i);
          }

          // Check quadrant
          if (!quads.get(this.getQuadrant(i, j) - 1).add(this.puzzle[i][j])) {
            throw new IllegalStateException(
                "Invalid quadrant " + this.getQuadrant(i, j) + " at value (" + (i + 1) + "," + (j + 1) + ")");
          }
        }
      }
    }
  }

  /**
   * Checks if a move is vlaid by only checking affected tiles
   * 
   * @param row row of move
   * @param col column of move
   * @throws IllegalStateException if move creates an illegal board
   */
  @SuppressWarnings("unused")
  private void quickCheckValid(int row, int col) throws IllegalStateException {
    // Check row
    if (!this.rows.get(row).contains(this.puzzle[row][col])) {
      throw new IllegalStateException("Invalid row " + row + " at column " + col);
    }

    // Check column
    if (!this.cols.get(col).contains(this.puzzle[row][col])) {
      throw new IllegalStateException("Invalid column " + col + " at row " + row);
    }

    // Check quadrant
    if (!this.quads.get(this.getQuadrant(row, col) - 1).contains(this.puzzle[row][col])) {
      throw new IllegalStateException(
          "Invalid quadrant " + this.getQuadrant(row, col) + " at value (" + (row + 1) + "," + (col + 1) + ")");
    }
  }

  /**
   * Creates a deep copy of the Sudoku puzzle.
   */
  public Sudoku clone() {
    // Clone the puzzle array
    int[][] puzzleClone = new int[this.getSize()][this.getSize()];
    // Deep copy
    for (int i = 0; i < puzzleClone.length; i++) {
      for (int j = 0; j < puzzleClone[i].length; j++) {
        puzzleClone[i][j] = this.puzzle[i][j];
      }
    }

    // Create a new puzzle object
    Sudoku clone = new Sudoku(puzzleClone);

    // Copy puzzle information
    clone.puzzleSize = this.puzzleSize;
    clone.quadrantSize = this.quadrantSize;
    clone.squaresLeft = this.squaresLeft;

    // Copy constraint sets
    clone.rows = new LinkedList<Set<Integer>>();
    clone.cols = new LinkedList<Set<Integer>>();
    clone.quads = new LinkedList<Set<Integer>>();
    for (int i = 0; i < this.getSize(); i++) {
      clone.rows.add(new HashSet<Integer>(this.rows.get(i)));
      clone.cols.add(new HashSet<Integer>(this.cols.get(i)));
      clone.quads.add(new HashSet<Integer>(this.quads.get(i)));
    }

    // Return a new puzzle
    return clone;
  }

  /**
   * Create a string representation of the sudoku puzzle
   */
  public String toString() {
    // Create a new stringbuilder to build the output string
    StringBuilder out = new StringBuilder();
    // Loop through all the rows
    for (int i = 0; i < this.getSize(); i++) {
      // Loop through all the columns
      for (int j = 0; j < this.getSize(); j++) {
        // Get each file value and add to string
        out.append(this.puzzle[i][j]);
      }
      // Current working row is finished and moving to next row (or end)
      out.append("\n");
    }
    // Convert stringbuilder to string and return
    return out.toString();
  }
}
