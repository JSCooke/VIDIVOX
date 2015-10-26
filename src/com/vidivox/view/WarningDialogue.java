package com.vidivox.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;


/**
 * @author Matthew Canham, Jayden Cooke
 * A class to generate simple text dialog boxes.
 * These typically aren't stored as variables.
 * Mainly used to notify the user of errors.
 */
public class WarningDialogue extends Dialogue{

    /**
     * Handles normal errors.
     * @param warningText - The text to present to the user.
     */
    public WarningDialogue(String warningText) {
        this(warningText, "Warning!");
    }

    //All of Dialogue's subclasses use this constructor.  It just calls the Dialogue constructor.
    public WarningDialogue(String warningText, String titleText) { super(warningText,titleText); }

    /**
     * This box has just got a message and an OK button.
     * @param messageText - The text to be presented.
     * @return - Returns the Scene to be added to the Dialogue.
     */
    protected Scene setUpLayout(String messageText){
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20,20,20,20));

        Label warningLabel = new Label(messageText);
        layout.getChildren().add(warningLabel);

        Button closeButton = new Button("OK");
        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                stage.close();
            }
        });
        layout.getChildren().add(closeButton);

        return new Scene(layout);
    }
}
