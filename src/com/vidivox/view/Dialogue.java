package com.vidivox.view;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Created by Jayden on 26/10/2015.
 */
public abstract class Dialogue {

    protected final Stage warningStage;
    /**
     * Handles normal errors.
     * @param warningText - The text to present to the user.
     */
    public Dialogue(String warningText) {
        this(warningText, "Warning!", false);
    }

    /**
     * Dialog box with a customisable layout. Subclasses implement the setUpLayout method to change the look.
     * @param warningText - The words to present to the user
     * @param titleText - The title of the dialog box
     * @param hasTextField - Whether or not to include the text input field.
     */
    public Dialogue(String warningText, String titleText, Boolean hasTextField) {
        //Initialise the window
        warningStage = new Stage();
        warningStage.initModality(Modality.APPLICATION_MODAL);
        warningStage.setTitle(titleText);
        warningStage.setMinWidth(250);
        Scene scene = setUpLayout(warningText,hasTextField);
        warningStage.setScene(scene);
        warningStage.showAndWait();
    }

    protected abstract Scene setUpLayout(String warningText, boolean hasTextField);

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
