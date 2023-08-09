import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Puzzle class for importing the file
 */
public class Puzzle {

    /**
     * A character array to read the file
     */
    public char[][] puzzle;

    /**
     * Default constructor, setting the grid to blank spaces
     */
    public Puzzle() {puzzle = new char[9][9];}

    /**
     * Reads the inputted file and sets the puzzle accordingly
     * @param file file to read
     */
    public void readFile(File file) {
        try {
            Scanner read = new Scanner(file);
            for(int i=0; i<9; i++) {
                char[] charLine = new char[9];
                String line = read.nextLine();
                for(int j=0; j<line.length(); j++) {
                    charLine[j] = line.charAt(j);
                }
                puzzle[i] = charLine;
            }
        } catch(FileNotFoundException ignored) {}
    }
}
