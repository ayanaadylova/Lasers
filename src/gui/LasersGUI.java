package gui;

/**
 * File: LasersGUI.java
 */

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Observable;
import java.util.Observer;
import javafx.scene.text.Font;
import model.*;

/**
 * The main class that implements the JavaFX UI.   This class represents
 * the view/controller portion of the UI.  It is connected to the model
 * and receives updates from it.
 *
 * @author Sean Strout @ RIT CS
 * @author Ayana Adylova, axa2111@rit.edu
 * @author Jonathan So, jds7523@rit.edu
 */
public class LasersGUI extends Application implements Observer {

    /** The UI's connection to the model */
    private LasersModel model;

    /** GridPane with the main board game */
    private GridPane grid;

    /** Label at the top of the game that displays current progress */
    private Label valueDisplay;

    /**
     * Background staff
     */
    Image image = new Image("gui/resources/background.png");
    BackgroundSize bgSize = new BackgroundSize (60,60,false,false,false,false);
    BackgroundImage bgImage = new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, bgSize);
    Background bg = new Background(bgImage);

    @Override
    public void init() throws Exception {
        // the init method is run before start.  the file name is extracted
        // here and then the model is created.
        try {
            Parameters params = getParameters();
            String filename = params.getRaw().get(0);
            this.model = new LasersModel(filename);
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
            System.exit(-1);
        }
        this.model.addObserver(this);
    }

    /**
     * A private utility function for setting the background of a button to
     * an image in the resources subdirectory.
     *
     * @param button the button control
     * @param bgImgName the name of the image file
     */
    private void setButtonBackground(Button button, String bgImgName) {
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image( getClass().getResource("resources/" + bgImgName).toExternalForm()),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT);
        Background background = new Background(backgroundImage);
        button.setBackground(background);
    }

    /**
     * The function which "sets up" the GUI.
     * @param stage the stage to add UI components into
     */
    private void init(Stage stage) {
        int pad = 20;
        GridPane border = new GridPane();
        border.setPadding(new javafx.geometry.Insets( pad ));
        // Set center
        this.grid = makeGrid();
        updateGrid();
        grid.setAlignment(Pos.CENTER);
        border.add(grid,0,1);
        // Set top
        this.valueDisplay = new Label(model.getMessage());
        //this.valueDisplay.setStyle("-fx-font-size: 14px; -fx-font-weight: bold");
        //this.valueDisplay.setFont(Font.font("Georgia"));
        valueDisplay.autosize();
        valueDisplay.setAlignment(Pos.CENTER);
        valueDisplay.setPadding(new javafx.geometry.Insets( pad ));
        valueDisplay.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        valueDisplay.setMinSize(valueDisplay.USE_PREF_SIZE, valueDisplay.USE_PREF_SIZE);
        border.setHalignment(valueDisplay, HPos.CENTER);
        border.add(valueDisplay,0,0);
        // Create HBox with Buttons and then set it to the bottom
        HBox hbox = new HBox();
        Button check = new Button();
        setImageOfButton(check, "resources/Check.png");
        check.setOnAction(event -> model.Verify());
        Button hint = new Button();
        setImageOfButton(hint, "resources/Hint.png");
        hint.setOnAction(event -> model.placeOneRightLaser());
        Button solve = new Button();
        setImageOfButton(solve, "resources/Solve.png");
        solve.setOnAction(event -> model.changeGridToDisplaySolution());
        Button restart = new Button();
        setImageOfButton(restart, "resources/Restart.png");
        restart.setOnAction(event -> model.reset());
        Button load = new Button();
        setImageOfButton(load,"resources/Load.png");
        load.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select a safe file");
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                String filename = selectedFile.getName();
                File file = selectedFile.getAbsoluteFile();
                try {
                    model.changeSafe(filename,file );
                    this.grid.getChildren().clear();
                    this.grid = makeGrid();
                    updateGrid();
                    grid.setAlignment(Pos.CENTER);
                    border.add(grid,0,1);
                    stage.sizeToScene();
                } catch (FileNotFoundException exp) {
                    System.out.println(exp.getMessage());
                    System.exit(-1);
                }
            }

        });
        hbox.getChildren().add(check);
        hbox.getChildren().add(hint);
        hbox.getChildren().add(solve);
        hbox.getChildren().add(restart);
        hbox.getChildren().add(load);
        border.add(hbox, 0, 2);
        hbox.setPadding( new javafx.geometry.Insets( pad ) );
        hbox.setSpacing(pad);
        //Set background
        border.setBackground(bg);
        Scene scene = new Scene(border);
        stage.sizeToScene();
        stage.setScene(scene);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        init(primaryStage);  // all UI initialization here
        primaryStage.setTitle("Lasers");
        primaryStage.show();
    }

    @Override
    public void update(Observable o, Object arg) {
        this.valueDisplay.setText(model.getMessage());
        if (!model.getSafeIsChanged()) {
            updateGrid();
        } else {
            model.changeSafeIsChanged();
        }
        if (model.getLastVerifiedRow() != null && model.getLastVerifiedColumn() != null) {
            Button btn = (Button)getNodeByRowColumnIndex(model.getLastVerifiedRow() ,model.getLastVerifiedColumn(), grid);
            setButtonBackground(btn, "red.png");
            model.resetVerifyCoordinates();
        }

    }

    /**
     * Constructs a main GridPane of Buttons which represents a safe
     * @return main GridPane
     */
    private GridPane makeGrid () {
        double screenH = Screen.getPrimary().getVisualBounds().getHeight();
        double size = screenH / 25 ;
        GridPane grid = new GridPane();
        int gap = 10;
        grid.setVgap( gap ); // gap between grid cells
        grid.setHgap( gap );
        int pad = 10;
        grid.setPadding( new javafx.geometry.Insets( pad ) );
        for (int i = 0; i < model.getSafe().getNumberOfRows(); i++) {
            for (int j = 0; j < model.getSafe().getNumberOfColumns(); j++) {
                Button square = new Button();
                square.setMinSize(size,size);
                square.setMaxSize(size,size);
                int row = i;
                int column = j;
                square.setOnAction(event -> {
                    char[][] ourGrid = model.getSafe().getGrid();
                    if (ourGrid[row][column] != Safe.LASER) {
                        model.Add(row,column);
                    } else {
                        model.Remove(row,column);
                    }
                });
                grid.add(square, j, i);
            }
        }
        return grid;
    }

    /**
     * Method updates grid based on the model's safe
     */
    private void updateGrid() {
        char[][] ourGrid = model.getSafe().getGrid();
        for (int i = 0; i < model.getSafe().getNumberOfRows(); i++) {
            for (int j = 0; j < model.getSafe().getNumberOfColumns(); j++) {
                Button btn = (Button)getNodeByRowColumnIndex(i ,j ,grid);
                btn.setGraphic(null);
                if (ourGrid[i][j] == Safe.LASER) {
                    setImageOfButton(btn,"resources/laser.png");
                    setButtonBackground(btn, "yellow.png");
                } else if (ourGrid[i][j] == Safe.LASER_BEAM) {
                    setImageOfButton(btn,"resources/beam.png");
                } else if (ourGrid[i][j] == Safe.X) {
                    setButtonBackground(btn,"white.png");
                    setImageOfButton(btn,"resources/pillarX.png");
                } else if (ourGrid[i][j] == Safe.ZERO) {
                    setButtonBackground(btn,"white.png");
                    setImageOfButton(btn,"resources/pillar0.png");
                } else if (ourGrid[i][j] == Safe.ONE) {
                    setButtonBackground(btn,"white.png");
                    setImageOfButton(btn,"resources/pillar1.png");
                } else if (ourGrid[i][j] == Safe.TWO) {
                    setButtonBackground(btn,"white.png");
                    setImageOfButton(btn,"resources/pillar2.png");
                } else if (ourGrid[i][j] == Safe.THREE) {
                    setButtonBackground(btn,"white.png");
                    setImageOfButton(btn,"resources/pillar3.png");
                } else if (ourGrid[i][j] == Safe.FOUR) {
                    setButtonBackground(btn,"white.png");
                    setImageOfButton(btn,"resources/pillar4.png");
                } else if (ourGrid[i][j] == Safe.EMPTY) {
                    setButtonBackground(btn,"white.png");
                }
            }
        }
    }

    /**
     * Method to set image of button
     * @param btn - button where we want to set an image
     * @param filename - name of the file with an image
     */
    private void setImageOfButton(Button btn, String filename){
        Image laserImg = new Image(getClass().getResourceAsStream(filename));
        ImageView laserIcon = new ImageView(laserImg);
        btn.setGraphic(laserIcon);
    }

    /**
     * Returns node from GridPane that is located at (row, column) cell
     * @param row - row of the node
     * @param column - column of the node
     * @param gridPane - GridPane where we try to find a node
     * @return node from GridPane that is located at (row, column) cell
     */
    private Node getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        Node result = null;
        ObservableList<Node> childrens = gridPane.getChildren();
        for(Node node : childrens) {
            if(gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }
        return result;
    }
}
