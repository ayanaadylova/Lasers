package backtracking;

/**
 * File: SafeConfig.java
 */

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import model.Safe;
import java.util.Arrays;


/**
 * The class represents a single configuration of a safe.  It is
 * used by the backtracker to generate successors, check for
 * validity, and eventually find the goal.
 *
 * This class is given to you here, but it will undoubtedly need to
 * communicate with the model.  You are free to move it into the model
 * package and/or incorporate it into another class.
 *
 * @author Sean Strout @ RIT CS
 * @author Ayana Adylova, axa2111@rit.edu
 * @author Jonathan So, jds7523@rit.edu
 */
public class SafeConfig implements Configuration {

    /** A representation of a safe */
    private Safe safe;
    /** Current row */
    private int currentRow;
    /** Current column */
    private int currentColumn;

    /**
     * Constructor with a filename as a parameter
     * @param filename - name of the file to read
     * @throws FileNotFoundException
     */
    public SafeConfig(String filename) throws FileNotFoundException {
        safe = new Safe(filename);
        currentColumn = -1;
        currentRow = 0;
    }

    /**
     * Constructor with Safe as a parameter
     * @param other - an instance of Safe
     */
    public SafeConfig(Safe other){
        safe = other;
        currentColumn = -1;
        currentRow = 0;
    }

    /**
     * Constructor which makes a deep copy
     * @param other - an instance of SafeConfig class
     */
    public SafeConfig(SafeConfig other)  {
        this.safe = new Safe(other.safe);
        this.currentRow = other.currentRow;
        this.currentColumn = other.currentColumn;
    }

    @Override
    public Collection<Configuration> getSuccessors() {
        Collection<Configuration> successors = new ArrayList<Configuration>();
        updateCurr();
        char cell = safe.getGrid()[currentRow][currentColumn];
        if (cell != Safe.X && cell != Safe.ONE && cell != Safe.TWO &&
           cell != Safe.THREE && cell != Safe.FOUR && cell != Safe.ZERO && cell != Safe.LASER ) {
            SafeConfig safeConfig1 = new SafeConfig(this);
            if (safeConfig1.safe.getGrid()[safeConfig1.currentRow][safeConfig1.currentColumn] != Safe.LASER_BEAM) {
                safeConfig1.safe.getGrid()[safeConfig1.currentRow][safeConfig1.currentColumn] = Safe.EMPTY;
            }
            SafeConfig safeConfig2 = new SafeConfig(this);
            safeConfig2.safe.Add(safeConfig2.currentRow, safeConfig2.currentColumn);
            successors.add(safeConfig2);
            successors.add(safeConfig1);
        } else {
            SafeConfig safeConfig1 = new SafeConfig(this);
            successors.add(safeConfig1);

        }
        return successors;
    }

    @Override
    public boolean isValid() {
        boolean answer = true;
        if (safe.getGrid()[currentRow][currentColumn] == Safe.LASER) {
            answer = checkForLasers();
        }
        // if we are at the last cell, check for empty cells and for every numbered pillar
        if (currentRow == safe.getNumberOfRows() - 1 && currentColumn == safe.getNumberOfColumns() - 1) {
            mainloop:
            for (int i = 0; i < safe.getNumberOfRows(); i++) {
                for (int j = 0; j < safe.getNumberOfColumns(); j++) {
                    char cell = safe.getGrid()[i][j];
                    if (cell == Safe.EMPTY) {
                        // if we have an empty cell, the safe is not valid
                        answer = false;
                        break mainloop;
                    } else if (cell == Safe.ZERO || cell == Safe.ONE ||
                            cell == Safe.TWO ||
                            cell == Safe.THREE || cell == Safe.FOUR) {
                        int numberOfLasersNearby = safe.numberOfLasersNearby(i,j);
                        // if the cell is a numbered pillar, check for number of lasers nearby
                        if (cell == Safe.ZERO) {
                            if (numberOfLasersNearby != 0) {
                                answer = false;
                                break mainloop;
                            }
                        }
                        if (cell == Safe.ONE) {
                            if (numberOfLasersNearby != 1) {
                                answer = false;
                                break mainloop;
                            }
                        }
                        if (cell == Safe.TWO) {
                            if (numberOfLasersNearby != 2) {
                                answer = false;
                                break mainloop;
                            }
                        }
                        if (cell == Safe.THREE) {
                            if (numberOfLasersNearby != 3) {
                                answer = false;
                                break mainloop;
                            }
                        }
                        if (cell == Safe.FOUR) {
                            if (numberOfLasersNearby != 4) {
                                answer = false;
                                break mainloop;
                            }
                        }
                    }
                }
            }
        }
        return answer;
    }

    /**
     * Method checks whether there are other lasers near current laser (in the same row and column
     * but before pillars)
     * @return true if there are no lasers nearby, false otherwise
     */
    private boolean checkForLasers () {
        int i = this.currentRow;
        int j = this.currentColumn;
        char[][]  grid = this.safe.getGrid();
        //Check for other Lasers in the North direction
        int numberOfLasersTop = 0;
        for (int k = i - 1; k >= 0; k--) {
            if (grid[k][j] == Safe.X || grid[k][j] == Safe.ZERO ||
                    grid[k][j] == Safe.ONE || grid[k][j] == Safe.TWO ||
                    grid[k][j] == Safe.THREE || grid[k][j] == Safe.FOUR ) {
                break;
            } else if (grid[k][j] == Safe.LASER){
                numberOfLasersTop ++;
            }
        }
        if (numberOfLasersTop != 0 ){
            return false;
        }
        //Check for other Lasers in the South direction
        int numberOfLasersBottom = 0;
        for (int k = i + 1; k < this.safe.getNumberOfRows() ; k++) {
            if (grid[k][j] == Safe.X || grid[k][j] == Safe.ZERO ||
                    grid[k][j] == Safe.ONE || grid[k][j] == Safe.TWO ||
                    grid[k][j] == Safe.THREE || grid[k][j] == Safe.FOUR ) {
                break;
            } else if (grid[k][j] == Safe.LASER){
                numberOfLasersBottom ++;
            }
        }
        if (numberOfLasersBottom != 0 ){
            return false;
        }
        //Check for other Lasers in the West direction
        int numberOfLasersLeft = 0;
        for (int k = j - 1; k >= 0; k--) {
            if (grid[i][k] == Safe.X || grid[i][k] == Safe.ZERO ||
                    grid[i][k] == Safe.ONE || grid[i][k] == Safe.TWO ||
                    grid[i][k] == Safe.THREE || grid[i][k] == Safe.FOUR  ) {
                break;
            } else if (grid[i][k] == Safe.LASER){
                numberOfLasersLeft ++;
            }
        }
        if (numberOfLasersLeft != 0 ){
            return  false;
        }
        //Check for other Lasers in the East direction
        int numberOfLasersRight = 0;
        for (int k = j + 1; k < this.safe.getNumberOfColumns(); k++) {
            if (grid[i][k] == Safe.X || grid[i][k] == Safe.ZERO ||
                    grid[i][k] == Safe.ONE || grid[i][k] == Safe.TWO ||
                    grid[i][k] == Safe.THREE || grid[i][k] == Safe.FOUR  ) {
                break;
            } else if (grid[i][k] == Safe.LASER){
                numberOfLasersRight ++;
            }
        }
        if (numberOfLasersRight != 0 ){
            return false;
        }
        return true;
    }

    @Override
    public boolean isGoal() {
        return (currentRow == safe.getNumberOfRows() - 1 && currentColumn == safe.getNumberOfColumns() -1);
    }

    /**
     * Updates current row and column
     */
    private void updateCurr() {
        if (this.currentColumn == safe.getNumberOfColumns() - 1) {
            this.currentColumn = 0;
            this.currentRow += 1;
        } else {
            this.currentColumn += 1;
        }
    }

    /**
     * @return String representation of our SafeConfig
     */
    public String toString() {
        return safe.toString();
    }

    /**
     * Two SafeConfigs are equal if they have the same grids
     * @param obj - object to compare with
     * @return true if objects are equal
     */
    public boolean equals(Object obj) {
        if (obj instanceof Configuration) {
            SafeConfig object = (SafeConfig)obj;
            for(int i = 0; i < this.safe.getNumberOfRows(); i++) {
                if (!Arrays.equals(object.safe.getGrid()[i],safe.getGrid()[i])) {
                    return false;
                }
            }
            return true;
        } else {
            return false;

        }
    }

    /**
     * @return instance of a Safe class
     */
    public Safe getSafe(){
        return safe;
    }

}
