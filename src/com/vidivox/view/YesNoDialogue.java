package com.vidivox.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Created by Jayden on 26/10/2015.
 */
public class YesNoDialogue extends Dialogue{

    private boolean outcome;

    public YesNoDialogue(String message, String title){
        super(message, title);
    }

    protected Scene setUpLayout(String warningText){
        Label warningLabel = new Label(warningText);
        VBox layout = new VBox(10);
        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        layout.getChildren().add(warningLabel);
        Button yesButton = new Button("Yes");
        yesButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                outcome = true;
                warningStage.close();
            }
        });
        Button noButton = new Button("No");
        noButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                outcome=false;
                warningStage.close();
            }
        });
        buttons.getChildren().add(yesButton);
        buttons.getChildren().add(noButton);
        layout.getChildren().add(buttons);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20,20,20,20));
        Scene scene = new Scene(layout);
        return scene;
    }
    public boolean getOutcome(){return outcome;}
}
