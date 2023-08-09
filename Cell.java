import java.util.ArrayList;

/**
 * Cell class for each cell in the puzzle
 */
public class Cell {

    /**
     * The possible numbers for the cell
     */
    private final ArrayList<Integer> candidates;

    /**
     * Default constructor, gives all possibilities (1-9) to the cell
     */
    public Cell() {
        candidates = new ArrayList<>();
        for(int i=1; i<=9; i++) {
            candidates.add(i);
        }
    }

    /**
     * Loaded constructor, puts a number in the cell
     * @param n the number to put in the cell
     */
    public Cell(int n) {
        candidates = new ArrayList<>();
        candidates.add(n);
    }

    /**
     * Alternative loaded constructor, sets candidates to a predetermined set
     * @param candidates the candidates to input
     */
    public Cell(ArrayList<Integer> candidates) {
        this.candidates = (ArrayList<Integer>) candidates.clone();
    }

    /**
     * Compares this cell to this same cell after performing logic to test whether it has changed
     * @param test This cell after performing logic
     * @return true if nothing changed
     *         false if it has changed
     */
    public boolean equals(Cell test) {return candidates.size() == test.candidates.size();}

    /**
     * Returns whether this cell is solved (size 1)
     * @return true if it is solved
     *         false if it is not solved
     */
    public boolean isSolved() {return candidates.size() == 1;}

    /**
     * Returns whether this cell is solvable (size>0)
     * @return true if it is possible
     *         false if it is not possible
     */
    public boolean isPossible() {return candidates.size() > 0;}

    /**
     * Returns the value of the cell [NOT ERROR CHECKED DUE TO ERROR CHECKING PRIOR TO USING THIS METHOD]
     * @return value of the cell
     */
    public int value() {return candidates.get(0);}

    /**
     * Removes a candidate from the list of candidates
     * @param k candidate to remove
     */
    public void remove(int k) {candidates.remove((Integer) k);}

    /**
     * Clones this cell
     * @return A copy of the cell
     */
    public ArrayList<Integer> clone() {return (ArrayList<Integer>) candidates.clone();}

    /**
     * Returns the amount of candidates for the cell
     * @return size of candidates
     */
    public int size() {return candidates.size();}

    /**
     * Returns whether a number is a candidate in this cell
     * @param n the number to check
     * @return true if n is a candidate
     *         false if n is not a candidate
     */
    public boolean contains(int n) {
        for(int i : candidates) {
            if(n==i) {
                return true;
            }
        } return false;
    }

    /**
     * Sets this cell to a given value
     * @param n the value to set to
     */
    public void set(int n) {
        candidates.clear();
        candidates.add(n);
    }

    /**
     * Returns whether this cell's candidates are a subset of another list
     * @param sup the potential superset of this cell
     * @return true if candidates is a subset of sup
     *         false if candidates is not a subset of sup
     */
    public boolean isSubset(ArrayList<Integer> sup) {
        for(int n : candidates) {
            if(!sup.contains(n)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the union of candidates and another given list
     * @param other the other list to perform the union
     * @return the union of the two sets
     */
    public ArrayList<Integer> union(ArrayList<Integer> other) {
        ArrayList<Integer> un = new ArrayList<>();
        for(int i : other) {
            for(int j : candidates) {
                if(i==j) {
                    un.add(i);
                }
            }
        }
        return un;
    }

    /**
     * toString method
     * @return value of the cell if it is solved
     *         "x" if it is not solved
     */
    public String toString() {
        if(this.isSolved()) {
            return String.valueOf(this.value());
        }
        return "x";
    }
}
