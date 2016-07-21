package ptui;

/**
 * File: ControllerPTUI.java
 */

import model.LasersModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This class represents the controller portion of the plain text UI.
 * It takes the model from the view (LasersPTUI) so that it can perform
 * the operations that are input in the run method.
 *
 * @author Sean Strout @ RIT CS
 * @author Ayana Adylova, axa2111@rit.edu
 * @author Jonathan So, jds7523@rit.edu
 */
public class ControllerPTUI  {

    /** The UI's connection to the model */
    private LasersModel model;

    /**
     * Construct the PTUI.  Create the model and initialize the view.
     * @param model The laser model
     */
    public ControllerPTUI(LasersModel model) {
        this.model = model;
    }

    /**
     * Run the main loop.  This is the entry point for the controller
     * @param inputFile The name of the input command file, if specified
     */
    public void run(String inputFile) {
        System.out.println(model.toString());
        if (inputFile != null) {
            try {
                Scanner in = new Scanner(new File(inputFile));
                while (in.hasNextLine()) {
                    String line = in.nextLine();
                    System.out.println("> " + line);
                    String[] command = line.split("\\s+");
                    if (command.length == 0 || command[0].isEmpty()) {
                        continue;
                    }
                    cmdProcess(command);
                }
                in.close();
            } catch (FileNotFoundException fnfe) {
                System.out.println(fnfe.getMessage());
                System.exit(-1);
            }
        }
        while (true) {
            Scanner newIn = new Scanner(System.in);
            System.out.print("> ");
            if (!newIn.hasNextLine()) {
                break;
            }
            String commandString = newIn.nextLine();
            String[] command = commandString.split("\\s+");
            if (command.length == 0 || command[0].isEmpty()) {
                continue;
            }
            cmdProcess(command);
        }
    }


    /**
     * Processes the user's commands to the console.
     * @param command is the String array representing the user's command.
     */
    private void cmdProcess(String[] command) {
        String firstCharacter = Character.toString(command[0].charAt(0));
        if (firstCharacter.equals("a") || command[0].equals("add")) {
            if (command.length != 3) {
                System.out.println("Incorrect coordinates");
            } else {
                model.Add(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
            }
        } else if (firstCharacter.equals("d") || command[0].equals("display")) {
            System.out.println(model);
        } else if (firstCharacter.equals("h") || command[0].equals("help")) {
            System.out.println("a|add r c: Add laser to (r,c)\n" +
                    "d|display: Display safe\n" +
                    "h|help: Print this help message\n" +
                    "q|quit: Exit program\n" +
                    "r|remove r c: Remove laser from (r,c)\n" +
                    "v|verify: Verify safe correctness");
        } else if (firstCharacter.equals("q") || command[0].equals("quit")) {
            System.exit(0);
        } else if (firstCharacter.equals("r") || command[0].equals("remove")) {
            if (command.length != 3) {
                System.out.println("Incorrect coordinates");
            }
            model.Remove(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
        } else if (firstCharacter.equals("v") || command[0].equals("verify")) {
            model.Verify();
        } else {
            System.out.println("Unrecognized command: " + command[0]);
        }
    }
}
