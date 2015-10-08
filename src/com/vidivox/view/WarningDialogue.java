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
 *
 */
public class WarningDialogue{

    String userInput = "";

    public WarningDialogue(String warningText) {
        this(warningText, "Warning!", false); //Legacy code, handles normal error calls written in Matthew's code.
    }
    //Jayden note (Disregard this comment if I forgot to take this out) - this needs to be more modular
    public WarningDialogue(String warningText, String titleText, Boolean hasTextField) {
        final Stage warningStage = new Stage();

        warningStage.initModality(Modality.APPLICATION_MODAL);
        warningStage.setTitle(titleText);
        warningStage.setMinWidth(250);
        warningStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                //The text entry box is only called when performing project save operations, and these must be cancelled.
                CurrentDirectory.interrupted();
                warningStage.close();
            }
        });
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
                    //An affirmative close needs to save the input text into the CurrentDirectory class.
                    CurrentDirectory.addName(inputField.getText());
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

    public static void genericError(String exceptionMessage){
        String message = "Whoops, something has gone wrong.\n";
        message += exceptionMessage;
        new WarningDialogue(message);
    }

}
