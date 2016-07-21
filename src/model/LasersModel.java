package model;

/**
 * File: LasersModel.java
 * A representation of the safe interactable with the user.
 * @author Ayana Adylova, axa2111@rit.edu
 * @author Jonathan So, jds7523@rit.edu
 */

import backtracking.Backtracker;
import backtracking.Configuration;
import backtracking.SafeConfig;
import java.io.FileNotFoundException;
import java.util.*;
import java.io.File;
import java.nio.file.Paths;

/**
 * Class that represents Lasers model.
 */
public class LasersModel extends Observable {

    // Private states
    private Safe safe;
    /** Message that displays state of a safe */
    private String message;
    /** Name of the file with initial safe representation */
    private String fileName;
    /** Boolean that indicates whether safe was changed or not */
    private boolean safeIsChanged;
    /** Solution of the safe */
    private Optional<Configuration> solution;


    /**
     * Constructs a safe using data from a text file.
     * @param filename is the name of the (extant) text file representing the safe layout.
     * @throws FileNotFoundException if the file does not exist in the directory.
     */
    public LasersModel(String filename) throws FileNotFoundException {
        safeIsChanged = false;
        safe = new Safe(filename);
        fileName = Paths.get(filename).getFileName().toString();
        this.message = this.fileName + " loaded";
        this.solution = null;
    }

    /**
     * Returns a user-friendly string displaying the safe layout.
     * @return a String representation of our safe-puzzle.
     */
    public String toString() {
        return safe.toString();
    }

    /**
     * @return a boolean that indicates whether safe was changed or not
     */
    public boolean getSafeIsChanged() {
        return safeIsChanged;
    }

    /**
     * Method changes a boolean that indicates whether safe was changed or not
     */
    public void changeSafeIsChanged(){
        this.safeIsChanged = !safeIsChanged;
    }

    /**
     *  Either adds a laser to our puzzle or changes a message that indicates that  the
     *  laser cannot be added.
     * @param row is the row of the laser we're adding.
     * @param column is the column of that same laser we're adding.
     */
    public void Add (int row, int column) {
        safe.Add(row,column);
        message = safe.getMessage();
        announceChange();
    }


    /**
     *  Verifies that the safe is either valid or invalid, and changes message to indicate
     *  user feedback showing where the safe becomes invalid or that
     *  the safe is indeed valid.
     */
    public void Verify () {
        safe.Verify();
        message = safe.getMessage();
        announceChange();
    }


    /**
     * Removes a laser at a specified location OR shows that there
     *  exists no laser at said location.
     * @param row is the row of the laser to remove.
     * @param column is the column of the laser to remove.
     */
    public void Remove(int row, int column) {
        safe.Remove(row,column);
        message = safe.getMessage();
        announceChange();
    }


    /**
     * A utility method that indicates the model has changed and
     * notifies observers
     */
    private void announceChange() {
        setChanged();
        notifyObservers();
    }

    /**
     * @return String message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Resets last verified row, column states of the safe
     */
    public void resetVerifyCoordinates() {
        safe.resetVerifyCoordinates();
    }

    /**
     * @return last verified row
     */
    public Integer getLastVerifiedRow() {
        return safe.getLastVerifiedRow();
    }

    /**
     * @return  last verified column
     */
    public Integer getLastVerifiedColumn() {
        return safe.getLastVerifiedColumn();
    }

    /**
     * Changed grid to display a solution of the safe
     */
    public void changeGridToDisplaySolution(){
        safe.reset();
        if (solution == null) {
            Backtracker bt = new Backtracker(false);
            solution = bt.solve(new SafeConfig(this.safe));
        }
        if (solution.isPresent()) {
            message = fileName + " solved!";
            SafeConfig solutionConfig = (SafeConfig) solution.get();
            char [][] solutionGrid = solutionConfig.getSafe().getGrid();
            for (int i = 0; i < safe.getNumberOfRows(); i++) {
                safe.getGrid()[i] = Arrays.copyOf(solutionGrid[i],solutionGrid.length);
            }
        } else {
            message = fileName + " has no solution!";

        }
        announceChange();
    }

    /**
     * Places one more right laser if the current grid is part of solution path
     */
    public void placeOneRightLaser() {
        Backtracker bt = new Backtracker(false);
        List<Configuration> solutionPath = bt.solveWithPath(new SafeConfig(this.safe));
        ArrayList<Configuration> lst = new ArrayList<>();
        for (Configuration config: solutionPath) {
            if (!lst.contains(config)) {
                lst.add(config);
            }
        }
        SafeConfig answer = null;
        if (lst.size() > 1 ) {
            answer = (SafeConfig) lst.get(1);
        }
        if (answer != null){
            safe = new Safe(answer.getSafe());
            message = "Hint: added laser to (" + answer.getSafe().getRowOfLastPlacedLaser() +
                    ", " + answer.getSafe().getColumnOfLastPlacedLaser() + ")";
        } else {
            message = "Hint: no next step!";
        }
        announceChange();
    }

    /**
     * Resets safe
     */
    public void reset() {
        safe.reset();
        message = fileName + " has been reset";
        announceChange();
    }

    /**
     * Changes safe to another one from the given file
     * @param filename - name of the file
     * @param file - the file in question
     * @throws FileNotFoundException
     */
    public void changeSafe(String filename, File file) throws FileNotFoundException{
        this.fileName = filename;
        this.safeIsChanged = true;
        this.message = filename + " loaded";
        this.safe = new Safe(file);
        this.solution = null;
        announceChange();
    }

    /**
     * @return Safe instance
     */
    public Safe getSafe(){
        return safe;
    }


}
