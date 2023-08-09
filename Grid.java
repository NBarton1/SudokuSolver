import java.util.ArrayList;
import java.util.HashMap;

/**
 * Grid class for the entire puzzle
 */
public class Grid {

    /**
     * The grid used for logic
     */
    private final Cell[][] grid;
    /**
     * A copy of the grid used to test if applied logic has made a step towards the solution
     */
    private Cell[][] gridCopy;
    /**
     * Columns in the grid [CONSTANT]
     */
    private final int X;
    /**
     * Rows in the grid [CONSTANT]
     */
    private final int Y;
    /**
     * General length of puzzle [CONSTANT]
     */
    private final int N;
    /**
     * List of numbers 1-N [UNCHANGING]
     */
    private final ArrayList<Integer> NUMS;

    /**
     * Default constructor, sets the constants and adds a Cell to each coordinate in grid
     */
    public Grid() {
        Y = X = N = 9;
        NUMS = new ArrayList<>();
        for(int n=1; n<=N; n++) {
            NUMS.add(n);
        }
        grid = new Cell[Y][X];
        for (int i = 0; i < Y; i++) {
            Cell[] row = new Cell[X];
            grid[i] = row;
        }
    }

    /**
     * Loaded constructor, sets constants and copies the grid
     * @param grid the grid to use and copy
     */
    public Grid(Cell[][] grid) {
        Y = X = N = 9;
        NUMS = new ArrayList<>();
        for(int n=1; n<=N; n++) {
            NUMS.add(n);
        }
        this.grid = grid;
        gridCopy = this.copy();
    }

    /**
     * Sets the grid using the puzzle
     * @param puzzle the puzzle used to set the grid
     */
    public void setGrid(Puzzle puzzle) {
        for (int i = 0; i < Y; i++) {
            for (int j = 0; j < X; j++) {
                String cell = String.valueOf(puzzle.puzzle[i][j]);
                if (!cell.equals("x")) {
                    grid[i][j] = new Cell(Integer.parseInt(cell));
                } else {
                    for (int k = 1; k <= 9; k++) {
                        grid[i][j] = new Cell();
                    }
                }
            }
        }
        gridCopy = this.copy();
    }

    /**
     * Returns whether a Cell can see a number
     * @param y y coordinate of Cell
     * @param x x coordinate of Cell
     * @param z number to test if it sees
     * @return true if the Cell can see z
     *         false if the Cell cannot see z
     */
    private boolean seeNumber(int y, int x, int z) {
        for (int i = 0; i < N; i++) {
            Cell rowCell = grid[y][i];
            Cell colCell = grid[i][x];
            if ((i != x && rowCell.isSolved() && rowCell.value() == z) || (i != y && colCell.isSolved() && colCell.value() == z)) {
                return true;
            }
        }
        for (int i = y / 3 * 3; i < y / 3 * 3 + 3; i++) {
            for (int j = x / 3 * 3; j < x / 3 * 3 + 3; j++) {
                Cell cell = grid[i][j];
                if (cell.isSolved() && cell.value() == z && !(i == y && j == x)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns whether the sudoku is solved (all Cells are size 1)
     * @return true if solved
     *         false if unsolved
     */
    public boolean isSolved() {
        for (int i=0; i<Y; i++) {
            for (int j=0; j<X; j++) {
                if (!grid[i][j].isSolved()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns whether the sudoku is possible (no Cells are size 0)
     * @return true if possible
     *         false if impossible
     */
    public boolean isPossible() {
        for(int i=0; i<Y; i++) {
            for(int j=0; j<X; j++) {
                if(!grid[i][j].isPossible()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Used to tell if a change has been made (grid != gridCopy)
     * @param test The grid to test equality
     * @return true if the grids are equal
     *         false if the grids are different
     */
    public boolean equals(Cell[][] test) {
        for (int i = 0; i < Y; i++) {
            for (int j = 0; j < X; j++) {
                if (!gridCopy[i][j].equals(test[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Copies grid to gridCopy
     * @return gridCopy
     */
    public Cell[][] copy() {
        gridCopy = new Cell[Y][X];
        for (int i=0; i<Y; i++) {
            for (int j=0; j<X; j++) {
                gridCopy[i][j] = new Cell(grid[i][j].clone());
            }
        }
        return gridCopy;
    }

    /**
     * Applies logic of naked singles to the grid
     * [This removes all numbers that each Cell can see from the candidates list of that Cell]
     * @return updated grid
     */
    public Cell[][] nakedSingle() {
        for (int i = 0; i < Y; i++) {
            for (int j = 0; j < X; j++) {
                ArrayList<Integer> cell = grid[i][j].clone();
                ArrayList<Integer> toRemove = new ArrayList<>();
                for (int k : cell) {
                    if (seeNumber(i, j, k)) {
                        toRemove.add(k);
                    }
                }
                for (int k : toRemove) {
                    grid[i][j].remove(k);
                }
            }
        }
        return grid;
    }

    /**
     * Counts the number of times that a candidate appears in a line
     * @param i which line to check [row or col depending on diff]
     * @param diff whether to check a row or column
     * @return a HashMap recording the number of times each candidate shows up
     */
    private HashMap<Integer,Integer> countLine(int i, String diff) {
        HashMap<Integer,Integer> line = new HashMap<>();
        for(int j = 0; j< N; j++) {
            Cell cell;
            if(diff.equals("row")) {
                cell = grid[i][j];
            } else {
                cell = grid[j][i];
            }
            for (int k : cell.clone()) {
                if (!line.containsKey(k)) {
                    line.put(k, 1);
                } else {
                    line.put(k, line.get(k) + 1);
                }
            }
        }
        return line;
    }

    /**
     * Sets a number in a line if it is a hidden single
     * @param i row or col depending on diff
     * @param line the candidates HashMap from countLine()
     * @param diff whether a row or column is being checked
     */
    private void setLine(int i, HashMap<Integer,Integer> line, String diff) {
        for(int k : line.keySet()) {
            if(line.get(k) == 1) {
                for(int j = 0; j< N; j++) {
                    Cell cell;
                    if(diff.equals("row")) {
                        cell = grid[i][j];
                    } else {
                        cell = grid[j][i];
                    }
                    if(cell.contains(k)) {
                        cell.set(k);
                    }
                }
            }
        }
    }

    /**
     * Counts the number of times a candidate appears in a box
     * @param x x coordinate of box to check (0-2)
     * @param y y coordinate of box to check (0-2)
     * @return HashMap containing the number of times each candidate appears
     */
    private HashMap<Integer,Integer> countBox(int x, int y) {
        HashMap<Integer,Integer> box = new HashMap<>();
        for(int i=3*y; i<3*y+3; i++) {
            for(int j=3*x; j<3*x+3; j++) {
                for(int k : grid[i][j].clone()) {
                    if(!box.containsKey(k)) {
                        box.put(k, 1);
                    } else {
                        box.put(k, box.get(k)+1);
                    }
                }
            }
        }
        return box;
    }

    /**
     * Sets a number in a box if it is a hidden single
     * @param x x coordinate of box
     * @param y y coordinate of box
     * @param box HashMap returned from countBox()
     */
    private void setBox(int x, int y, HashMap<Integer,Integer> box) {
        for(int k : box.keySet()) {
            if(box.get(k) == 1) {
                for(int i=y*3; i<y*3+3; i++) {
                    for (int j = x*3; j < x*3+3; j++) {
                        if (grid[i][j].contains(k)) {
                            grid[i][j].set(k);
                        }
                    }
                }
            }
        }
    }

    /**
     * Uses logic Hidden Singles
     * [A hidden single is when a digit appears exactly once in a row, column, or box]
     * @return updated grid
     */
    public Cell[][] hiddenSingle() {
        for(int i = 0; i< N; i++) {
            setLine(i, countLine(i, "row"), "row");
            setLine(i, countLine(i, "col"), "col");
        }
        for(int y=0; y<Y/3; y++) {
            for(int x=0; x<X/3; x++) {
                setBox(x, y, countBox(x, y));
            }
        }
        return grid;
    }

    /**
     * Checks if all of a certain candidate in a box appear in a line
     * @param x x coordinate of box
     * @param y y coordinate of box
     * @param k number to check
     * @param n amount of times that number is expected to appear
     * @param diff whether a row or column is being checked
     * @return the line that all candidates share
     *         -1 if a line is not shared
     */
    private int areInLine(int x, int y, int k, int n, String diff) {
        for(int i=3*y; i<3*y+3; i++) {
            int count = 0;
            for(int j=3*x; j<3*x+3; j++) {
                Cell cell;
                if(diff.equals("row")) {
                    cell = grid[i][j];
                } else {
                    cell = grid[j][i];
                }
                if(cell.contains(k)) {
                    count++;
                }
            }
            if(count==n) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Removes a candidate from all other cells in a line if locked candidate logic applies
     * @param xORy x or y coordinate depending on whether the line from areInLine() is vertical or horizontal
     * @param i line that is shared from areInLine()
     * @param k the value that is in the line
     * @param diff whether a row or column is being checked
     */
    private void removeFromLine(int xORy, int i, int k, String diff) {
        for (int j = 0; j < N; j++) {
            if (!(j >= xORy * 3 && j < xORy * 3 + 3)) {
                Cell cell;
                if(diff.equals("row")) {
                    cell = grid[i][j];
                } else {
                    cell = grid[j][i];
                }
                cell.remove(k);
            }
        }
    }

    /**
     * Uses logic Locked Candidates
     * [A locked candidate is when all of one candidate in a box appears in one line, which removes that candidate from the rest of the line]
     * @return updated grid
     */
    public Cell[][] lockedCandidate() {
        for(int y=0; y<Y/3; y++) {
            for(int x=0; x<X/3; x++) {
                HashMap<Integer,Integer> box = countBox(x, y);
                for(int k : box.keySet()) {
                    if(box.get(k) > 1) {
                        int icommon = areInLine(x, y, k, box.get(k), "row");
                        int jcommon = areInLine(y, x, k, box.get(k), "col");
                        if (icommon != -1) {
                            removeFromLine(x, icommon, k, "row");
                        }
                        if (jcommon != -1) {
                            removeFromLine(y, jcommon, k, "col");
                        }
                    }
                }
            }
        }
        return grid;
    }

    /**
     * Gets all combinations of n choose r (nCr)
     * @param n n input in nCr
     * @param r r input in nCr
     * @return a list of boolean lists, each containing a combination for nCr
     */
    private ArrayList<boolean[]> getCombinations(int n, int r) {
        ArrayList<boolean[]> combos = new ArrayList<>();
        if(r==0) {
            boolean[] combo = new boolean[n];
            for(int i=0; i<n; i++) {
                combo[i] = false;
            }
            combos.add(combo);
            return combos;
        }
        for(int d=1; d<=n-r+1; d++) {
            for(boolean[] append : getCombinations(n-d, r-1)) {
                boolean[] combo = new boolean[n];
                combo[n-d] = true;
                for(int i=0; i< append.length; i++) {
                    combo[i] = append[i];
                }
                combos.add(combo);
            }
        }
        return combos;
    }

    /**
     * Uses return from getCombinations() to return a list of all possible combinations of numbers
     * @param combinations the return from getCombinations()
     * @return A translated list to numbers from booleans
     */
    private ArrayList<ArrayList<Integer>> getNumCombos(ArrayList<boolean[]> combinations) {
        ArrayList<ArrayList<Integer>> numCombos = new ArrayList<>();
        for(boolean[] boolCombo : combinations) {
            ArrayList<Integer> combo = new ArrayList<>();
            for (int n = 0; n < N; n++) {
                if (boolCombo[n]) {
                    combo.add(NUMS.get(n));
                }
            }
            numCombos.add(combo);
        }
        return numCombos;
    }

    /**
     * Takes a candidate and removes it from the rest of the line if naked pair/trip/quad logic applies
     * @param i the line to remove from
     * @param combo the combination that fits the logic
     * @param diff whether a row or column is being checked
     */
    private void removeCandidateFromLine(int i, ArrayList<Integer> combo, String diff) {
        int r = combo.size();
        for(int j=0; j<N; j++) {
            Cell cell;
            if(diff.equals("row")) {
                cell = grid[i][j];
            } else {
                cell = grid[j][i];
            }
            if(!(cell.size() <= r && cell.isSubset(combo))) {
                for(int k : combo) {
                    cell.remove(k);
                }
            }
        }
    }

    /**
     * Removes a candidate from a box if a pair/trip/quad is present
     * @param x x coordinate of box
     * @param y y coordinate of box
     * @param combo the combination that fits the logic
     */
    private void removeCandidateFromBox(int x, int y, ArrayList<Integer> combo) {
        int r = combo.size();
        for(int i=3*y; i<3*y+3; i++) {
            for(int j=3*x; j<3*x+3; j++) {
                if(!(grid[i][j].size() <= r && grid[i][j].isSubset(combo))) {
                    for(int k : combo) {
                        grid[i][j].remove(k);
                    }
                }
            }
        }
    }

    /**
     * Uses logic Naked Pairs/Triplets/Quadruplets
     * [A naked n is when n cells contain exactly n different candidates total in a line or box, removing those candidates from anywhere else respectively]
     * @return updated grid
     */
    public Cell[][] nakedPair() {
        for(int i = 0; i< N; i++) {
            for(int r=2; r<=4; r++) {
                ArrayList<ArrayList<Integer>> combos = getNumCombos(getCombinations(N, r));
                for (ArrayList<Integer> combo : combos) {
                    int rowCount = 0;
                    int colCount = 0;
                    for(int j=0; j<N; j++) {
                        if(grid[i][j].isSubset(combo)) {
                            rowCount++;
                        }
                        if(grid[j][i].isSubset(combo)) {
                            colCount++;
                        }
                    }
                    if(rowCount==r) {
                        removeCandidateFromLine(i, combo, "row");
                    }
                    if(colCount==r) {
                        removeCandidateFromLine(i, combo, "col");
                    }
                }
            }
        }

        for(int y=0; y<Y/3; y++) {
            for(int x=0; x<X/3; x++) {
                for(int r=2; r<=4; r++) {
                    ArrayList<ArrayList<Integer>> combos = getNumCombos(getCombinations(N, r));
                    for (ArrayList<Integer> combo : combos) {
                        int count = 0;
                        for(int i=3*y; i<3*y+3; i++) {
                            for(int j=3*x; j<3*x+3; j++) {
                                if(grid[i][j].isSubset(combo)) {
                                    count++;
                                }
                            }
                        }
                        if(count==r) {
                            removeCandidateFromBox(x, y, combo);
                        }
                    }
                }
            }
        }
        return grid;
    }

    /**
     * Applies logic Hidden Pairs/Triplets/Quadruplets
     * [A hidden n is when n candidates are locked in n cells in a line or box, removing any other candidates from those cells]
     * @return updated grid
     */
    public Cell[][] hiddenPair() {
        for(int i = 0; i< N; i++) {
            for(int r=2; r<=4; r++) {
                ArrayList<ArrayList<Integer>> combos = getNumCombos(getCombinations(N, r));
                for (ArrayList<Integer> combo : combos) {
                    int rowCount = 0;
                    int colCount = 0;
                    for(int j=0; j<N; j++) {
                        boolean foundRow = false;
                        boolean foundCol = false;
                        for(int k : combo) {
                            if (grid[i][j].contains(k) && !foundRow) {
                                rowCount++;
                                foundRow = true;
                            }
                            if (grid[j][i].contains(k) && !foundCol) {
                                colCount++;
                                foundCol = true;
                            }
                        }
                    }
                    if(rowCount==r) {
                        for(int j=0; j<X; j++) {
                            for(int k : combo) {
                                if (grid[i][j].contains(k)) {
                                    grid[i][j] = new Cell(grid[i][j].union(combo));
                                }
                            }
                        }
                    }
                    if(colCount==r) {
                        for(int j=0; j<Y; j++) {
                            for(int k : combo) {
                                if (grid[j][i].contains(k)) {
                                    grid[j][i] = new Cell(grid[j][i].union(combo));
                                }
                            }
                        }
                    }
                }
            }
        }

        for(int y=0; y<Y/3; y++) {
            for(int x=0; x<X/3; x++) {
                for(int r=2; r<=4; r++) {
                    ArrayList<ArrayList<Integer>> combos = getNumCombos(getCombinations(N, r));
                    for (ArrayList<Integer> combo : combos) {
                        int count = 0;
                        for(int i=3*y; i<3*y+3; i++) {
                            for(int j=3*x; j<3*x+3; j++) {
                                for(int k : combo) {
                                    if (grid[i][j].contains(k)) {
                                        count++;
                                        break;
                                    }
                                }
                            }
                        }
                        if(count==r) {
                            for(int i=3*y; i<3*y+3; i++) {
                                for(int j=3*x; j<3*x+3; j++) {
                                    for(int k : combo) {
                                        if (grid[i][j].contains(k)) {
                                            grid[i][j] = new Cell(grid[i][j].union(combo));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return grid;
    }

    /**
     * Checks if one list is a subset of another
     * @param sub potential subset
     * @param sup potential superset
     * @return true if sub is a subset of sup
     *         false if sub is not a subset of sup
     */
    private boolean isSubset(ArrayList<Integer> sub, ArrayList<Integer> sup) {
        for(int k : sub) {
            if(!sup.contains(k)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Applies X-Wing logic
     * [An X-Wing is when a digit appears in the same n spots in n different lines, removing that candidate from the lines in the other axis]
     * @return updated grid
     */
    public Cell[][] xwing() {
        for(int n : NUMS) {
            ArrayList<ArrayList<Integer>> rowLocs = new ArrayList<>();
            ArrayList<ArrayList<Integer>> colLocs = new ArrayList<>();
            for(int i=0; i<N; i++) {
                rowLocs.add(new ArrayList<>());
                colLocs.add(new ArrayList<>());
            }
            for(int i=0; i<N; i++) {
                for(int j=0; j<N; j++) {
                    if(grid[i][j].contains(n)) {
                        rowLocs.get(i).add(j);
                        colLocs.get(j).add(i);
                    }
                }
            }
            for(int r=2; r<=4; r++) {
                ArrayList<ArrayList<Integer>> combos = getNumCombos(getCombinations(N, r));
                for (ArrayList<Integer> combo : combos) {
                    for(int i=0; i<r; i++) {
                        combo.set(i, combo.get(i)-1);
                    }
                    ArrayList<Integer> rowCount = new ArrayList<>();
                    ArrayList<Integer> colCount = new ArrayList<>();
                    for (int i = 0; i < N; i++) {
                        if (isSubset(rowLocs.get(i), combo)) {
                            rowCount.add(i);
                        }
                        if (isSubset(colLocs.get(i), combo)) {
                            colCount.add(i);
                        }
                    }
                    if(rowCount.size()==r) {
                        for(int i=0; i<Y; i++) {
                            for(int j : combo) {
                                boolean ignore = false;
                                for(int k : rowCount) {
                                    if(i==k) {
                                        ignore = true;
                                        break;
                                    }
                                }
                                if(!ignore) {
                                    grid[i][j].remove(n);
                                }
                            }
                        }
                    }
                    if(colCount.size()==r) {
                        for(int j=0; j<X; j++) {
                            for(int i : combo) {
                                boolean ignore = false;
                                for(int k : colCount) {
                                    if(j==k) {
                                        ignore = true;
                                        break;
                                    }
                                }
                                if(!ignore) {
                                    grid[i][j].remove(n);
                                }
                            }
                        }
                    }
                }
            }
        }
        return grid;
    }

    /**
     * When no other logic applies a change to the grid, guess a digit
     * @return updated grid
     */
    public int guess() {
        for(int r=2; r<=N; r++) {
            for(int i=0; i<Y; i++) {
                for(int j=0; j<X; j++) {
                    if(grid[i][j].size() == r) {
                        grid[i][j].set(grid[i][j].value());
                        return 9*i+j;
                    }
                }
            }
        }
        return -1; // Should never reach this point
    }

    /**
     * If a guess was incorrect, remove that candidate as a guess
     * @param spot The location of the guess
     */
    public void removeCandidate(int spot) {
        grid[spot/9][spot%9].remove(grid[spot/9][spot%9].value());
    }

    /**
     * If a guess was correct, set that Cell as the guessed digit
     * @param spot the location of the guess
     */
    public void setCandidate(int spot) {
        grid[spot/9][spot%9].set(grid[spot/9][spot%9].value());
    }

    /**
     * toString method
     * @return a String representation of the grid
     */
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for(int i=0; i<Y; i++) {
            ret.append("\n");
            if(i%3 == 0) {
                ret.append("-------------------------\n");
            }
            for(int j=0; j<X; j++) {
                if(j%3 == 0) {
                    ret.append("| ");
                }
                ret.append(grid[i][j]).append(" ");
            }
            ret.append("|");
        }
        ret.append("\n-------------------------");
        return ret.toString();
    }
}