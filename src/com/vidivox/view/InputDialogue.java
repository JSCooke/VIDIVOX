package com.vidivox.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * @author Jayden Cooke
 * A dialog box with a text input box, and facilities to get the text.
 */
public class InputDialogue extends Dialogue {

    private String textInput;

    //All of Dialogue's subclasses use this constructor.  It just calls the Dialogue constructor.
    public InputDialogue(String warningText, String titleText){
        super(warningText,titleText);
    }

    /**
     * This box has a textfield for getting input from the user where a FileChooser couldn't.
     * @param messageText - The text to be presented.
     * @return - Returns the Scene to be added to the Dialogue.
     */
    protected Scene setUpLayout(String messageText){
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20,20,20,20));

        Label label = new Label(messageText);
        layout.getChildren().add(label);

        final TextField inputField = new TextField();
        inputField.setPrefWidth(240);
        layout.getChildren().add(inputField);

        Button closeButton = new Button("OK");
        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //An affirmative close needs to save the input text into the inputText field.
                textInput = inputField.getText();
                stage.close();
            }
        });
        layout.getChildren().add(closeButton);

        return new Scene(layout);
    }

    /**
     * Gets the text entered by the user.
     * @return Returns whatever was saved when the box was closed.
     */
    public String getText(){ return textInput; }
}
