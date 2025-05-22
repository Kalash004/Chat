package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.fxml_controllers;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.discovery.DiscoveryClient;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.format_utils.RegexUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.SendPublicMessageEvent;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_event_handler.EventHandler;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.ui_link_utils.Page;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.UiController;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.format_utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class PublicChatController {
    @FXML private Label chatLabel;
    @FXML private AnchorPane rootPane;
    @FXML private TextArea messageArea;
    @FXML private Button sendButton;
    @FXML private ListView<String> userList;
    @FXML private ListView<String> messageHistory;
    public void initialize() {
        messageArea.setOnKeyPressed(event -> onKeyPress(event));
    }
    private static final Logger logger = LoggerFactory.getLogger(PublicChatController.class);


    public void setNewUserList(Peer[] peerList) {
        Platform.runLater(() -> { // NOTE: used when another thread needs to update the JavaFX user interface
            userList.getItems().clear();
            for(Peer peer : peerList) {
                userList.getItems().add(peer.getUserName());
            }
            userList.scrollTo(userList.getItems().size() - 1);
        });
    }

    /**
     * Sets the label (which is above message history) text based on the number of connected peers.
     * If there are no connected peers, the label will display "No connected users",
     * otherwise it will display "Public chat".
     *
     * @param peerListLength the number of connected peers
     */
    public void setLabel(int peerListLength) {
        Platform.runLater(() -> {
            if (peerListLength == 0) {
                chatLabel.setText("No connected users");
            } else {
                chatLabel.setText("Public chat");
            }
        });
    }

    /**
     * Updates the message history displayed in the user interface with new messages.
     * The messages are added to the message history list, and the list is scrolled to the most recent message.
     * This method runs on the JavaFX Application Thread to safely update the UI.
     *
     * @param messageList the list of message pairs, where each pair consists of a Peer and the associated Message
     */
    public void setNewMessageHistory(ArrayList<Pair<String, Message>> messageList) {
        Platform.runLater(() -> { // NOTE: used when another thread needs to update the JavaFX user interface
            logger.info("Now list length: " + messageHistory.getItems().size());
            messageHistory.getItems().clear();
            logger.info("Message list length: " + messageList.size());
            //HashMap<Peer, Message> sortedMessageMap = new HashMap<>();
            for(Pair<String, Message> pair : messageList) {
                addMessage(pair.getValue());
            }

            messageHistory.scrollTo(messageHistory.getItems().size() - 1);
        });
    }


    protected void sendMessage(String content) {
        SendPublicMessageEvent messageSendEvent = new SendPublicMessageEvent();
        messageSendEvent.setInput(content);
        EventHandler.getInstance().handleEvent(messageSendEvent);
    }

    public void setDarkMode(boolean darkMode) {
        styleListView(userList, darkMode);
        styleListView(messageHistory, darkMode);
        rootPane.getStyleClass().removeAll("light-mode", "dark-mode");
        if(darkMode) {
            rootPane.getStyleClass().add("dark-mode");
        } else {
            rootPane.getStyleClass().add("light-mode");
        }
    }

    /**
     * Styles a ListView to display items with customized colors depending on the selected mode (dark or light).
     * The method sets the background color and text color for each item in the list based on the mode.
     *
     * @param listView the ListView to apply the styles to
     * @param darkMode if true, applies dark mode styles (dark background, white text); if false, applies light mode styles (white background, black text)
     */
    protected void styleListView(ListView<String> listView, boolean darkMode
    ) {
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                // Force dark mode colors manually (can be customized)
                if(darkMode) {
                    setStyle("-fx-background-color: #252526; -fx-text-fill: white;");
                } else {
                    setStyle("-fx-background-color: white; -fx-text-fill: black;");
                }

            }
        });
    }

    @FXML
    private void onUserListClick(MouseEvent event) {
        String selected = userList.getSelectionModel().getSelectedItem();
        if(selected == null) { // if an empty list element was selected
            return;
        }
        int selectedIndex = userList.getSelectionModel().getSelectedIndex();
        Stage stage = (Stage) sendButton.getScene().getWindow();
        UiController.getInstance().setCurrentPeer(selectedIndex);
        UiController.getInstance().openPage(Page.PRIVATE_CHAT, stage);
        //UiController.getInstance().setChatUser(selected);
        /*
        send to backend, switch FMXL to private chat, show the chat history with a selected active user
         */
    }

    @FXML
    protected void onOptionsButtonClick(ActionEvent event) {
        Stage stage = (Stage) sendButton.getScene().getWindow();
        UiController.getInstance().openPage(Page.OPTIONS, stage);
    }

    protected void sendHandle() {
        String content = RegexUtils.removeNewlines(messageArea.getText());;

        if(!content.isBlank()) {
            //messageHistory.getItems().add("You: (" + DateUtils.timestampToString(new Date()) + "): " + content);
            messageArea.clear();
            //messageHistory.getItems().add("You (" + new Date() + "): " + content);
            sendMessage(content);
        }
    }

    @FXML
    protected void onKeyPress(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {
            sendHandle();
        }
    }

    @FXML
    protected void onSendButtonClick(ActionEvent event) {
        sendHandle();
    }

    private void addMessage(Message message) {
        messageHistory.getItems().add(message.getSenderName() + " (" + DateUtils.timestampToString(message.getTimestamp()) + "): " + message.getContent());
        messageHistory.scrollTo(messageHistory.getItems().size() - 1);
    }

    public Stage getStage() {
        return (Stage) sendButton.getScene().getWindow();
    }
}
