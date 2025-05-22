package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.fxml_controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class FatalErrorController {

    @FXML private Label errorLabel;

    public void setErrorLabel(String errorMessage) {
        Platform.runLater(() -> {
            errorLabel.setText(errorMessage);
        });
    }
}
