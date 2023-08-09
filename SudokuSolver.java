import java.io.File;
import java.util.Scanner;

/**
 * Sudoku solver!
 */
public class SudokuSolver {
    public static void main(String[] args) {
        Puzzle puzzle = new Puzzle();
        Scanner in = new Scanner(System.in);
        //puzzle.readFile(errorFile(in));
        puzzle.readFile(new File("src\\nothing"));
        Grid grid = new Grid();
        grid.setGrid(puzzle);
        System.out.println("Original grid:");

        grid = logic(grid, 0);

        String solvable = "impossible";
        if(grid.isSolved())
            solvable = "possible";
        System.out.println(grid + "\nThis sudoku is "+ solvable + "!");
    }

    /**
     * Gets a user inputted file
     * @param in Scanner object
     * @return File associated with user input
     */
    public static File errorFile(Scanner in) {
        String fileName;
        File file;
        do {
            System.out.print("Enter a file to read (start with src\\): ");
            fileName = in.nextLine();
            file = new File(fileName);
        } while(!file.exists());
        return file;
    }

    /**
     * The logic order used to solve the puzzle
     * @param grid The puzzle to be solved
     * @param layer How deep in recursion the program is, starting with 0
     * @return -1 for impossible
     *          0 for unsolved
     *          1 for solved
     */
    public static Grid logic(Grid grid, int layer) {
        while(!grid.isSolved()) {
            if(layer==0)
                System.out.println(grid);
            boolean changed = false;
            while(!changed) {
                if(!grid.equals(grid.hiddenSingle())) {
                    if(layer==0)
                        System.out.println("Hidden single");
                    grid.copy();
                    changed = true;
                } else if(!grid.equals(grid.nakedSingle())) {
                    if(layer==0)
                        System.out.println("Naked single");
                    grid.copy();
                    changed = true;
                } else if(!grid.equals(grid.lockedCandidate())) {
                    if(layer==0)
                        System.out.println("Locked candidate");
                    grid.copy();
                    changed = true;
                } else if(!grid.equals(grid.nakedPair())) {
                    if(layer==0)
                        System.out.println("Naked pair");
                    grid.copy();
                    changed = true;
                } else if(!grid.equals(grid.hiddenPair())) {
                    if(layer==0)
                        System.out.println("Hidden pair");
                    grid.copy();
                    changed = true;
                } else if(!grid.equals(grid.xwing())) {
                    if(layer==0)
                        System.out.println("X Wing");
                    grid.copy();
                    changed = true;
                } else if(grid.isPossible()) {
                    if(layer==0)
                        System.out.println("Brute force");
                    Grid gridCopy = new Grid(grid.copy());
                    int guessingSpot = gridCopy.guess();
                    gridCopy = logic(gridCopy, layer+1);
                    if(gridCopy.isPossible()) {
                        return gridCopy;
                    } else {
                        grid.removeCandidate(guessingSpot);
                    }
                } else {
                    return grid;
                }
            }
        }
        if(layer==0)
            System.out.println(grid);
        return grid;
    }
}