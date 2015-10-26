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
 * Created by Jayden on 26/10/2015.
 */
public class InputDialogue extends Dialogue {

    private String textInput;

    public InputDialogue(String warningText, String titleText){
        super(warningText,titleText);
    }

    protected Scene setUpLayout(String warningText){
        Label warningLabel = new Label(warningText);
        VBox layout = new VBox(10);
        layout.getChildren().add(warningLabel);
        Button closeButton = new Button("OK");
        final TextField inputField = new TextField();
        inputField.setPrefWidth(240);
        layout.getChildren().add(inputField);
        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //An affirmative close needs to save the input text into the inputText field.
                textInput=inputField.getText();
                warningStage.close();
            }
        });
        layout.getChildren().add(closeButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20,20,20,20));
        Scene scene = new Scene(layout);
        return scene;
    }

    public String getText(){ return textInput; }
}
