package model;

/**
 * File: Safe.java
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


/**
 * Class that represents a safe. It includes all operations and states.
 * @author Ayana Adylova, axa2111@rit.edu
 * @author Jonathan So, jds7523@rit.edu
 */
public class Safe {
    // CONSTANTS
    /** A horizontal divider */
    public final static char HORI_DIVIDE = '-';
    /** A vertical divider */
    public final static char VERT_DIVIDE = '|';
    /** A laser */
    public final static char LASER = 'L';
    /** A laser beam */
    public final static char LASER_BEAM = '*';
    /** A pillar that can have any number of adjacent lasers, including none */
    public final static char X = 'X';
    /** A pillar that requires zero adjacent lasers */
    public final static char ZERO = '0';
    /** A pillar that requires one adjacent laser */
    public final static char ONE = '1';
    /** A pillar that requires two adjacent lasers */
    public final static char TWO = '2';
    /** A pillar that requires three adjacent lasers */
    public final static char THREE = '3';
    /** A pillar that requires four adjacent lasers */
    public final static char FOUR = '4';
    /** An empty tile */
    public final static char EMPTY = '.';

    // Private states
    /** 2D array that represents a safe */
    private char[][] grid;
    /** Number of rows in the safe */
    private int numberOfRows;
    /** Number of columns in the safe */
    private int numberOfColumns;
    /** Row coordinate of last verified method */
    private Integer lastVerifiedRow;
    /** Column coordinate of last verified method */
    private Integer lastVerifiedColumn;
    /** Message that displays state of a safe */
    private String message;
    /** Row of the last placed laser */
    private Integer RowOfLastPlacedLaser;
    /** Column of the last placed laser */
    private Integer ColumnOfLastPlacedLaser;

    /**
     * Constructs a safe using data from a text file.
     * @param filename is the name of the (extant) text file representing the safe layout.
     * @throws FileNotFoundException if the file does not exist in the directory.
     */
    public Safe(String filename) throws FileNotFoundException {
        Scanner in = new Scanner(new File(filename));
        lastVerifiedRow = null;
        lastVerifiedColumn = null;
        RowOfLastPlacedLaser = null;
        ColumnOfLastPlacedLaser = null;
        this.numberOfRows = in.nextInt();
        this.numberOfColumns = in.nextInt();
        this.grid = new char[numberOfRows][numberOfColumns];
        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                grid[i][j] = in.next().charAt(0);
            }
        }
        in.close();
    }

    /**
     * Constructs a safe using data from a text file.
     * @param file is the  file representing the safe layout.
     * @throws FileNotFoundException if the file does not exist in the directory.
     */
    public Safe(File file) throws FileNotFoundException {
        Scanner in = new Scanner(file);
        lastVerifiedRow = null;
        lastVerifiedColumn = null;
        RowOfLastPlacedLaser = null;
        ColumnOfLastPlacedLaser = null;
        this.numberOfRows = in.nextInt();
        this.numberOfColumns = in.nextInt();
        this.grid = new char[numberOfRows][numberOfColumns];
        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                grid[i][j] = in.next().charAt(0);
            }
        }
        in.close();
    }

    /**
     * Creates a deep copy of the Safe
     * @param other
     */
    public Safe (Safe other){
        grid = new char[other.getNumberOfRows()][other.getNumberOfColumns()];
        for (int i = 0; i < other.numberOfRows; i++) {
            System.arraycopy(other.grid[i], 0, this.grid[i], 0, other.getNumberOfColumns());
        }
        this.numberOfRows = other.numberOfRows;
        this.numberOfColumns = other.numberOfColumns;
        this.lastVerifiedColumn = other.getLastVerifiedColumn();
        this.lastVerifiedRow = other.getLastVerifiedRow();
        this.message = other.message;
        this.RowOfLastPlacedLaser = other.getRowOfLastPlacedLaser();
        this.ColumnOfLastPlacedLaser = other.getColumnOfLastPlacedLaser();
    }

    /**
     * Returns a user-friendly string displaying the safe layout.
     * @return a String representation of our safe-puzzle.
     */
    public String toString() {
        String result = "";
        result += "  ";
        // Column numbers
        for (int i = 0; i < numberOfColumns; i++) {
            result += (i % 10) + " ";
        }
        result += "\n";
        result += "  ";
        // Horizontal dividers
        for (int i = 0; i <= 2 * this.numberOfColumns - 2 ; i++) {
            result += HORI_DIVIDE;
        }
        result += "\n";
        // Each horizontal line has the line number, a divider, and the safe data.
        for (int i = 0; i < numberOfRows; i++) {
            result += (i % 10);
            result += VERT_DIVIDE;
            for (int j = 0; j < numberOfColumns; j++) {
                result += grid[i][j];
                if (j != numberOfColumns - 1) {
                    result += " ";
                }
            }
            if (i != numberOfRows - 1) {
                result += "\n";
            }
        }
        return result;
    }

    /**
     * Either adds a laser to our puzzle or changes a message to one that the
     * laser cannot be added.
     * @param row is the row of the laser we're adding.
     * @param column is the column of that same laser we're adding.
     */
    public void Add (int row, int column) {
        if ((row < 0 || column < 0) || (row >= numberOfRows || column >= numberOfColumns) || (grid[row][column] == X || grid[row][column] == ZERO ||
                grid[row][column] == ONE || grid[row][column] == TWO ||
                grid[row][column] == THREE || grid[row][column] == FOUR ||
                grid[row][column] == LASER)) {
            message = "Error adding laser at: (" + row + ", " + column + ")";
        } else {
            grid[row][column] = LASER;
            RowOfLastPlacedLaser = row;
            ColumnOfLastPlacedLaser = column;
            // Set up a laser beam to the North direction until we reach a pillar, laser or the top
            for (int i = row - 1; i >= 0; i--) {
                if (grid[i][column] == X || grid[i][column] == ZERO ||
                        grid[i][column] == ONE || grid[i][column] == TWO ||
                        grid[i][column] == THREE || grid[i][column] == FOUR ||
                        grid[i][column] == LASER) {
                    break;
                } else {
                    grid[i][column] = LASER_BEAM;
                }
            }
            // Set up a laser beam to the South direction until we reach a pillar, laser or the bottom
            for (int i = row + 1; i < numberOfRows; i++) {
                if (grid[i][column] == X || grid[i][column] == ZERO ||
                        grid[i][column] == ONE || grid[i][column] == TWO ||
                        grid[i][column] == THREE || grid[i][column] == FOUR ||
                        grid[i][column] == LASER) {
                    break;
                } else {
                    grid[i][column] = LASER_BEAM;
                }
            }
            // Set up a laser beam to the West direction until we reach a pillar, laser or the left corner
            for (int i = column - 1; i >= 0; i--) {
                if (grid[row][i] == X || grid[row][i]== ZERO ||
                        grid[row][i] == ONE || grid[row][i] == TWO ||
                        grid[row][i] == THREE || grid[row][i] == FOUR ||
                        grid[row][i] == LASER) {
                    break;
                } else {
                    grid[row][i] = LASER_BEAM;
                }
            }
            // Set up a laser beam to the East direction until we reach a pillar, laser or the right direction
            for (int i = column + 1; i < numberOfColumns; i++) {
                if (grid[row][i] == X || grid[row][i]== ZERO ||
                        grid[row][i] == ONE || grid[row][i] == TWO ||
                        grid[row][i] == THREE || grid[row][i] == FOUR ||
                        grid[row][i] == LASER) {
                    break;
                } else {
                    grid[row][i] = LASER_BEAM;
                }
            }
            message = "Laser added at: (" + row + ", " + column + ")";
        }
    }
    /**
     *  Verifies that the safe is either valid or invalid, and changes message to
     *  user feedback showing where the safe becomes invalid or that
     *  the safe is indeed valid.
     */
    public void Verify () {
        boolean breaked = false;
        outerloop:
        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                if (grid[i][j] == LASER_BEAM || grid[i][j] == X) {
                    continue;
                } else if (grid[i][j] == EMPTY) {
                    message = ("Error verifying at: (" + i + ", " + j + ")");
                    lastVerifiedRow = i;
                    lastVerifiedColumn = j;
                    breaked = true;
                    break outerloop;
                } else if (grid[i][j] == LASER) {
                    //Check for other Lasers in the North direction
                    int numberOfLasersTop = 0;
                    for (int k = i - 1; k >= 0; k--) {
                        if (grid[k][j] == X || grid[k][j] == ZERO ||
                                grid[k][j] == ONE || grid[k][j] == TWO ||
                                grid[k][j] == THREE || grid[k][j] == FOUR ) {
                            break;
                        } else if (grid[k][j] == LASER){
                            numberOfLasersTop ++;
                        }
                    }
                    if (numberOfLasersTop != 0 ){
                        message = ("Error verifying at: (" + i + ", " + j + ")");
                        lastVerifiedRow = i;
                        lastVerifiedColumn = j;
                        breaked = true;
                        break outerloop;
                    }
                    //Check for other Lasers in the South direction
                    int numberOfLasersBottom = 0;
                    for (int k = i + 1; k < numberOfRows ; k++) {
                        if (grid[k][j] == X || grid[k][j] == ZERO ||
                                grid[k][j] == ONE || grid[k][j] == TWO ||
                                grid[k][j] == THREE || grid[k][j] == FOUR ) {
                            break;
                        } else if (grid[k][j] == LASER){
                            numberOfLasersBottom ++;
                        }
                    }
                    if (numberOfLasersBottom != 0 ){
                        message = ("Error verifying at: (" + i + ", " + j + ")");
                        lastVerifiedRow = i;
                        lastVerifiedColumn = j;
                        breaked = true;
                        break outerloop;
                    }
                    //Check for other Lasers in the West direction
                    int numberOfLasersLeft = 0;
                    for (int k = j - 1; k >= 0; k--) {
                        if (grid[i][k] == X || grid[i][k] == ZERO ||
                                grid[i][k] == ONE || grid[i][k] == TWO ||
                                grid[i][k] == THREE || grid[i][k] == FOUR ) {
                            break;
                        } else if (grid[i][k] == LASER){
                            numberOfLasersLeft ++;
                        }
                    }
                    if (numberOfLasersLeft != 0 ){
                        message = ("Error verifying at: (" + i + ", " + j + ")");
                        lastVerifiedRow = i;
                        lastVerifiedColumn = j;
                        breaked = true;
                        break outerloop;
                    }
                    //Check for other Lasers in the East direction
                    int numberOfLasersRight = 0;
                    for (int k = j + 1; k < numberOfColumns; k++) {
                        if (grid[i][k] == X || grid[i][k] == ZERO ||
                                grid[i][k] == ONE || grid[i][k] == TWO ||
                                grid[i][k] == THREE || grid[i][k] == FOUR ) {
                            break;
                        } else if (grid[i][k] == LASER){
                            numberOfLasersRight ++;
                        }
                    }
                    if (numberOfLasersRight != 0 ){
                        message = ("Error verifying at: (" + i + ", " + j + ")");
                        lastVerifiedRow = i;
                        lastVerifiedColumn = j;
                        breaked = true;
                        break outerloop;
                    }
                } else if (grid[i][j] == ZERO || grid[i][j] == ONE || grid[i][j] == TWO ||
                        grid[i][j] == THREE || grid[i][j] == FOUR) {
                    // Check how many lasers near every pillar and if the number given does not
                    // match, print error.
                    int numberOfLasersNearby = numberOfLasersNearby(i,j);
                    if (grid[i][j] == ZERO) {
                        if (numberOfLasersNearby != 0) {
                            message = ("Error verifying at: (" + i + ", " + j + ")");
                            lastVerifiedRow = i;
                            lastVerifiedColumn = j;
                            breaked = true;
                            break outerloop;
                        }
                    }
                    if (grid[i][j] == ONE) {
                        if (numberOfLasersNearby != 1) {
                            message = ("Error verifying at: (" + i + ", " + j + ")");
                            lastVerifiedRow = i;
                            lastVerifiedColumn = j;
                            breaked = true;
                            break outerloop;
                        }
                    }
                    if (grid[i][j] == TWO) {
                        if (numberOfLasersNearby != 2) {
                            message = ("Error verifying at: (" + i + ", " + j + ")");
                            lastVerifiedRow = i;
                            lastVerifiedColumn = j;
                            breaked = true;
                            break outerloop;
                        }
                    }
                    if (grid[i][j] == THREE) {
                        if (numberOfLasersNearby != 3) {
                            message = ("Error verifying at: (" + i + ", " + j + ")");
                            lastVerifiedRow = i;
                            lastVerifiedColumn = j;
                            breaked = true;
                            break outerloop;
                        }
                    }
                    if (grid[i][j] == FOUR) {
                        if (numberOfLasersNearby != 4) {
                            message = ("Error verifying at: (" + i + ", " + j + ")");
                            lastVerifiedRow = i;
                            lastVerifiedColumn = j;
                            breaked = true;
                            break outerloop;
                        }
                    }
                }
            }

        }
        if (!breaked) {
            message = ("Safe is fully verified!");
        }
    }

    /**
     * Helper function which checks the amount of lasers near a pillar's location.
     * @param row is the row of a pillar.
     * @param column is the column of that same pillar.
     * @return an integer representing the amount of lasers adjacent to the pillar.
     */
    public int numberOfLasersNearby(int row, int column) {
        int adjacent = 0;
        if (column > 0) { // We're not at the left edge, so check one space left
            if (grid[row][column - 1] == LASER)
                adjacent++;
        }
        if (column < numberOfColumns - 1) { // We're not at the right edge, so check one space right
            if (grid[row][column + 1] == LASER)
                adjacent++;
        }
        if (row > 0) { // We're not at the top edge, so check one space above
            if (grid[row - 1][column] == LASER)
                adjacent++;
        }
        if (row < numberOfRows - 1) { // We're not at the bottom edge, so check one space below
            if (grid[row + 1][column] == LASER)
                adjacent++;
        }
        return adjacent;
    }

    /**
     * Removes a laser at a specified location OR shows that there
     * exists no laser at said location.
     * @param row is the row of the laser to remove.
     * @param column is the column of the laser to remove.
     */
    public void Remove(int row, int column) {
        if (row < 0 || column < 0 || row > (numberOfRows - 1) || column > (numberOfColumns - 1)
                || grid[row][column] != LASER) {
            message = ("Error removing laser at: (" + row + ", " + column + ")");
        } else {
            grid[row][column] = EMPTY;
            // Remove a laser beam to the North direction until we reach a pillar or the top
            for (int i = row - 1; i >= 0; i--) {
                if (grid[i][column] == X || grid[i][column] == ZERO ||
                        grid[i][column] == ONE || grid[i][column] == TWO ||
                        grid[i][column] == THREE || grid[i][column] == FOUR ||
                        grid[i][column] == LASER) {
                    break;
                } else {
                    grid[i][column] = EMPTY;
                }
            }
            // Remove a laser beam to the South direction until we reach a pillar or the bottom
            for (int i = row + 1; i < numberOfRows; i++) {
                if (grid[i][column] == X || grid[i][column] == ZERO ||
                        grid[i][column] == ONE || grid[i][column] == TWO ||
                        grid[i][column] == THREE || grid[i][column] == FOUR ||
                        grid[i][column] == LASER) {
                    break;
                } else {
                    grid[i][column] = EMPTY;
                }
            }
            // Remove a laser beam to the West direction until we reach a pillar or the left corner
            for (int i = column - 1; i >= 0; i--) {
                if (grid[row][i] == X || grid[row][i] == ZERO ||
                        grid[row][i] == ONE || grid[row][i] == TWO ||
                        grid[row][i] == THREE || grid[row][i] == FOUR ||
                        grid[row][i] == LASER) {
                    break;
                } else {
                    grid[row][i] = EMPTY;
                }
            }
            // Remove a laser beam to the East direction until we reach a pillar or the right direction
            for (int i = column + 1; i < numberOfColumns; i++) {
                if (grid[row][i] == X || grid[row][i] == ZERO ||
                        grid[row][i] == ONE || grid[row][i] == TWO ||
                        grid[row][i] == THREE || grid[row][i] == FOUR ||
                        grid[row][i] == LASER) {
                    break;
                } else {
                    grid[row][i] = EMPTY;
                }
            }
            for (int k = 0; k < numberOfRows; k++) {
                for (int j = 0; j < numberOfColumns; j++ ) {
                    if (grid[k][j] == LASER) {
                        // Set up a laser beam to the North direction until we reach a pillar, laser or the top
                        for (int i = k - 1; i >= 0; i--) {
                            if (grid[i][j] == X || grid[i][j] == ZERO ||
                                    grid[i][j] == ONE || grid[i][j] == TWO ||
                                    grid[i][j] == THREE || grid[i][j] == FOUR ||
                                    grid[i][j] == LASER) {
                                break;
                            } else {
                                grid[i][j] = LASER_BEAM;
                            }
                        }
                        // Set up a laser beam to the South direction until we reach a pillar, laser or the bottom
                        for (int i = k + 1; i < numberOfRows; i++) {
                            if (grid[i][j] == X || grid[i][j] == ZERO ||
                                    grid[i][j] == ONE || grid[i][j] == TWO ||
                                    grid[i][j] == THREE || grid[i][j] == FOUR ||
                                    grid[i][j] == LASER) {
                                break;
                            } else {
                                grid[i][j] = LASER_BEAM;
                            }
                        }
                        // Set up a laser beam to the West direction until we reach a pillar, laser or the left corner
                        for (int i = j - 1; i >= 0; i--) {
                            if (grid[k][i] == X || grid[k][i]== ZERO ||
                                    grid[k][i] == ONE || grid[k][i] == TWO ||
                                    grid[k][i] == THREE || grid[k][i] == FOUR ||
                                    grid[k][i] == LASER) {
                                break;
                            } else {
                                grid[k][i] = LASER_BEAM;
                            }
                        }
                        // Set up a laser beam to the East direction until we reach a pillar, laser or the right direction
                        for (int i = j + 1; i < numberOfColumns; i++) {
                            if (grid[k][i] == X || grid[k][i]== ZERO ||
                                    grid[k][i] == ONE || grid[k][i] == TWO ||
                                    grid[k][i] == THREE || grid[k][i] == FOUR ||
                                    grid[k][i] == LASER) {
                                break;
                            } else {
                                grid[k][i] = LASER_BEAM;
                            }
                        }
                    }
                }
            }
            message = ("Laser removed at: (" + row + ", " + column + ")");
        }
    }

    /**
     * Resets safe, e.g. removes all lasers
     */
    public void reset() {
        for (int i = 0; i < numberOfRows; i++ ) {
            for (int j =0; j < numberOfColumns; j++) {
                if (grid[i][j] == LASER) {
                    this.Remove(i,j);
                }
            }
        }
    }

    /**
     * @return String message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @return number of rows in the safe
     */
    public int getNumberOfRows() {
        return numberOfRows;
    }

    /**
     * @return number of columns in the safe
     */
    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    /**
     * @return 2D array, e.g. grid
     */
    public char[][] getGrid(){
        return grid;
    }

    /**
     * Resets last verified coordinates
     */
    public void resetVerifyCoordinates() {
        lastVerifiedColumn = null;
        lastVerifiedRow = null;
    }

    /**
     * @return last verified row
     */
    public Integer getLastVerifiedRow() {
        return lastVerifiedRow;
    }

    /**
     * @return last verified column
     */
    public Integer getLastVerifiedColumn() {
        return lastVerifiedColumn;
    }

    /**
     * @return row of last placed laser
     */
    public Integer getRowOfLastPlacedLaser() {return RowOfLastPlacedLaser;}

    /**
     * @return column of last placed laser
     */
    public Integer getColumnOfLastPlacedLaser() {return ColumnOfLastPlacedLaser;}

}
