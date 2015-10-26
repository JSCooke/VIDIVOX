package com.vidivox.view;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author Jayden Cooke
 * JavaFX has no dialog box class, so this allows me to generate dialog boxes.
 * This implements the template design pattern, with the setUpLayout method as the hook.
 * Concept based on the WarningDialogue class created by Matthew Canham for assignment 3.
 */
public abstract class Dialogue {

    protected final Stage stage;

    /**
     * Dialog box with a customisable layout. Subclasses implement the setUpLayout method to change the look and function.
     * @param messageText - The words to present to the user
     * @param titleText - The title of the dialog box
     */
    public Dialogue(String messageText, String titleText) {
        //Initialise the window
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(titleText);
        stage.setMinWidth(250);

        //Call the hook for the layout
        Scene scene = setUpLayout(messageText);

        //Show the window
        stage.setScene(scene);
        stage.showAndWait();
    }

    //Details of the layout are decided by the subclasses.
    protected abstract Scene setUpLayout(String messageText);

    /**
     * Can be called from anywhere an error has occurred.
     * @param exceptionMessage - The text to display.
     */
    public static void genericError(String exceptionMessage){
        String message = "Whoops, something has gone wrong.\nThis may have caused issues with your project.\n";
        message += exceptionMessage;
        new WarningDialogue(message);
    }
}
