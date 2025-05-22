package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.fxml_controllers;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.ui_link_utils.Page;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.UiController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.tictactoe_service.TictactoeGameHandler;

public class TictactoeController {
    @FXML private Button returnButton;
    @FXML private Label gameWithLabel;
    @FXML private Label statusLabel;
    @FXML private AnchorPane rootPane;
    @FXML private GridPane grid;
    @FXML private Button btn00;
    @FXML private Button btn01;
    @FXML private Button btn02;
    @FXML private Button btn10;
    @FXML private Button btn11;
    @FXML private Button btn12;
    @FXML private Button btn20;
    @FXML private Button btn21;
    @FXML private Button btn22;
    private TictactoeGameHandler middleWare;
    private boolean hasFirstTurn;
    private Peer peer;

    public void initialize() {

    }

    public void setMiddleWare() {
        middleWare = TictactoeGameHandler.getInstance();
        middleWare.launchGame();
        hasFirstTurn = middleWare.getHasFirstTurn();
    }

    public void setDarkMode(boolean darkMode) {
        rootPane.getStyleClass().removeAll("light-mode", "dark-mode");
        if(darkMode) {
            rootPane.getStyleClass().add("dark-mode");
        } else {
            rootPane.getStyleClass().add("light-mode");
        }
    }


    @FXML
    private void onSpaceClick(ActionEvent actionEvent) {
        Button spaceButton = (Button) actionEvent.getSource();
        if(spaceButton.getText().isEmpty() && middleWare.isStillPlaying()) {
            spaceButton.setText(hasFirstTurn ? "X": "O");
            String space = spaceButton.getId();
            int x = space.charAt(3) - '0';
            int y = space.charAt(4) - '0';
            middleWare.sendBoard(x, y);
        }

    }

    public void setBoardStatusLabel(String status) {
        statusLabel.setText(status);
    }

    /**
     * Updates the specified button on the user interface to reflect the received move in the game board.
     * The button's text is set to the specified sign (either "X" or "O") and the button is disabled to prevent further interaction.
     *
     * @param buttonName the name of the button representing the space on the game board
     * @param sign the sign ("X" or "O") to be displayed on the button
     */
    public void onReceiveBoard(String buttonName, String sign) {
        Button spaceButton = (Button) rootPane.lookup("#" + buttonName);
        spaceButton.setText(sign);
        spaceButton.setDisable(true);
    }

    public void closePage() {
        middleWare.setTTTPeer(null);
        middleWare.removePeerFromPending(null);
        middleWare.setHasFirstTurn(true);
        Platform.runLater(() -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            UiController.getInstance().openPage(null, stage);
        });
    }

    @FXML
    private void onReturnClick(ActionEvent actionEvent) {
        closePage();
    }

    public void openPublicChat() {
        Platform.runLater(() -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            UiController.getInstance().openPage(Page.PUBLIC_CHAT, stage);
        });
    }

    public void setGameWithLabel(String userName) {
        gameWithLabel.setText("Playing with: " + userName);
    }
}
