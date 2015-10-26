package com.vidivox.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author Jayden Cooke
 * A dialog box to get a Yes or No answer from the user, and facilities to get their response.
 */
public class YesNoDialogue extends Dialogue{

    private boolean outcome;

    //All of Dialogue's subclasses use this constructor. It just calls the Dialogue constructor.
    public YesNoDialogue(String message, String title){
        super(message, title);
    }

    protected Scene setUpLayout(String messageText){
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20, 20, 20, 20));

        Label label = new Label(messageText);
        layout.getChildren().add(label);

        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);

        Button yesButton = new Button("Yes");
        yesButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                outcome = true;
                stage.close();
            }
        });
        buttons.getChildren().add(yesButton);

        Button noButton = new Button("No");
        noButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                outcome = false;
                stage.close();
            }
        });
        buttons.getChildren().add(noButton);

        layout.getChildren().add(buttons);

        return new Scene(layout);
    }

    /**
     * @return Returns a boolean representing the user's answer.
     */
    public boolean getOutcome(){return outcome;}
}
