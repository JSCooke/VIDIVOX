package com.vidivox.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 */
public class WarningDialogue{

    public WarningDialogue(String warningText) {
        final Stage warningStage = new Stage();

        warningStage.initModality(Modality.APPLICATION_MODAL);
        warningStage.setTitle("Warning!");
        warningStage.setMinWidth(250);

        Label warningLabel = new Label(warningText);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                warningStage.close();
            }
        });


        VBox layout = new VBox(10);
        layout.getChildren().addAll(warningLabel, closeButton);
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
