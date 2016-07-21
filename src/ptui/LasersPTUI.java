package ptui;

/**
 * File: LasersPTUI.java
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Observable;
import java.util.Observer;
import model.LasersModel;

/**
 * This class represents the view portion of the plain text UI.  It
 * is initialized first, followed by the controller (ControllerPTUI).
 * You should create the model here, and then implement the update method.
 *
 * @author Sean Strout @ RIT CS
 * @author Ayana Adylova, axa2111@rit.edu
 * @author Jonathan So, jds7523@rit.edu
 */
public class LasersPTUI implements Observer {

    /** The UI's connection to the model */
    private LasersModel model;

    /**
     * Construct the PTUI.  Create the model and initialize the view.
     * @param filename the safe file name
     * @throws FileNotFoundException
     */
    public LasersPTUI(String filename) throws FileNotFoundException {
        this.model = new LasersModel(filename);
        this.model.addObserver(this);
    }

    /**
     * @return lasers model
     */
    public LasersModel getModel() { return this.model; }

    @Override
    public void update(Observable o, Object arg) {
        if (model.getMessage() != null) {
            System.out.println(model.getMessage());
        }
        System.out.println(model);
    }
}
