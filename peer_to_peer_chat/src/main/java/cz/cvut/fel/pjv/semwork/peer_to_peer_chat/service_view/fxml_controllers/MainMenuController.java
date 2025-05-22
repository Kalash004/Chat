package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.fxml_controllers;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.configs.ApiConfig;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.ui_link_utils.Page;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.SendHelloEvent;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.StartEvent;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_event_handler.EventHandler;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.UiController;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.format_utils.RegexUtils;

public class MainMenuController {
    @FXML private AnchorPane rootPane;
    @FXML private TextField usernameField;

    @FXML
    public void initialize() {
        usernameField.setOnKeyPressed(event -> handleKeyPress(event));
    }


    private void connectHandle() {
        if(usernameField.getText().isEmpty() || !RegexUtils.usernameCheck(usernameField.getText())) {
            usernameField.clear();
            usernameField.setPromptText("Username is not valid!");
        } else {
            sendHelloMessage(usernameField.getText());
            Stage stage = (Stage) rootPane.getScene().getWindow();
            UiController.getInstance().openPage(Page.PUBLIC_CHAT, stage);
            /*
            send to backend, client discovery & connection
             */
        }
    }

    private void handleKeyPress(KeyEvent event) {
        if(KeyCode.ENTER == event.getCode()) {
            connectHandle();
        }
    }


    public void setDarkMode(boolean darkMode) {
        rootPane.getStyleClass().removeAll("light-mode", "dark-mode");
        if(darkMode) {
            rootPane.getStyleClass().add("dark-mode");
        } else {
            rootPane.getStyleClass().add("light-mode");
        }
    }

    private void sendHelloMessage(String userName) {
        ApiConfig.setUserName(userName);
        StartEvent startEvent = new StartEvent();
        EventHandler.getInstance().handleEvent(startEvent);
        SendHelloEvent sendHelloEvent = new SendHelloEvent();
        sendHelloEvent.setInput(userName);
        EventHandler.getInstance().handleEvent(sendHelloEvent);
    }

    @FXML
    private void onConnectButtonClick() {
        connectHandle();
    }

    @FXML
    private void onOptionsButtonClick(ActionEvent actionEvent) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        UiController.getInstance().openPage(Page.OPTIONS, stage);
    }
}
