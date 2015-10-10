package com.vidivox.view;

import com.vidivox.controller.CurrentDirectory;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * @author Matthew Canham, Jayden Cooke
 * A class to generate dialog boxes, which are lacking in JavaFX.
 * Mainly used to notify the user of errors.
 */
public class WarningDialogue{

    private String textInput;

    /**
     * Handles normal errors.
     * @param warningText - The text to present to the user.
     */
    public WarningDialogue(String warningText) {
        this(warningText, "Warning!", false);
    }

    /**
     * Warning dialogue box with a customisable title and text input field.
     * As this is only used once, it is very intertwined with the New Project process. This will be changed.
     * @param warningText - The words to present to the user
     * @param titleText - The title of the dialog box
     * @param hasTextField - Whether or not to include the text input field.
     */
    public WarningDialogue(String warningText, String titleText, Boolean hasTextField) {
        //Initialise the window
        final Stage warningStage = new Stage();
        warningStage.initModality(Modality.APPLICATION_MODAL);
        warningStage.setTitle(titleText);
        warningStage.setMinWidth(250);
        Label warningLabel = new Label(warningText);
        VBox layout = new VBox(10);
        layout.getChildren().add(warningLabel);
        Button closeButton = new Button("OK");
        if (hasTextField){
            //only add and format the text field if it needs to be there.
            final TextArea inputField = new TextArea();
            inputField.setMaxWidth(240);
            inputField.setMaxHeight(30);
            layout.getChildren().add(inputField);
            closeButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    //An affirmative close needs to save the input text into the inputText field.
                    textInput=inputField.getText();
                    warningStage.close();
                }
            });
        }else {
            //Close action is slightly different when there is no text to add.
            closeButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    warningStage.close();
                }
            });
        }
        layout.getChildren().add(closeButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20,20,20,20));
        Scene scene = new Scene(layout);
        warningStage.setScene(scene);
        warningStage.showAndWait();
    }

    /**
     * Can be called from anywhere an error has occurred.
     * @param exceptionMessage - The text to display.
     */
    public static void genericError(String exceptionMessage){
        String message = "Whoops, something has gone wrong.\n";
        message += exceptionMessage;
        new WarningDialogue(message);
    }

    public String getText(){ return textInput; }
}
