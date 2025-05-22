package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.fxml_controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.tictactoe.TictactoeInvitationStatus;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.ConfirmSendTTTInviteEvent;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.tictactoe_service.TictactoeGameHandler;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.tictactoe_service.TictactoeInvitationMessages;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.message_content_classes.PeerAndMessageTextHolder;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.PrivateMessageSendEvent;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.SendTTTInviteEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.discovery.DiscoveryClient;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.ui_link_utils.Page;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_event_handler.EventHandler;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.UiController;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.format_utils.DateUtils;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.utils.json.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class PrivateChatController extends PublicChatController {
    @FXML private AnchorPane rootPane;
    @FXML private Label chatLabel;
    @FXML private Button sendButton;
    @FXML private TextArea messageArea;
    @FXML private ListView<String> userList;
    @FXML private ListView<String> messageHistory;
    private static final Logger logger = LoggerFactory.getLogger(PrivateChatController.class);
    private Peer currentPeer;
    private final HashMap<String, ArrayList<String>> userMap = new HashMap<>();

    @Override
    protected void sendMessage(String content) {
        PrivateMessageSendEvent messageSendEvent = new PrivateMessageSendEvent();
        messageSendEvent.setInput(new Pair<Peer, String>(currentPeer, content));
        EventHandler.getInstance().handleEvent(messageSendEvent);
    }

    /**
     * Adds a new message to the chat UI if the message is from the currently selected peer.
     * Parses the message content from JSON and displays it with timestamp and sender username.
     *
     * @param message  the message object containing content and timestamp
     * @param peer     the peer who sent the message (for displaying the username)
     */
    public void addMessage(Message message, Peer peer) {
        Platform.runLater(() -> {
            PeerAndMessageTextHolder holder;
            try {
                holder = JsonUtils.fromJson(message.getContent(), PeerAndMessageTextHolder.class);
            } catch (JsonProcessingException e) {
                logger.error("Error while parsing json in addMessage | {}", message);
                throw new RuntimeException(e);
            }
            String content = holder.messageTextHolder.text;
            messageHistory.getItems().add(peer.getUserName() + " (" + DateUtils.timestampToString(message.getTimestamp()) + "): " + content);
            messageHistory.scrollTo(messageHistory.getItems().size() - 1);
        });
    }

    /**
     * Sets the entire message history for the current user in the UI.
     * Clears the current chat and loads messages from the provided list, sorted or unsorted.
     *
     * @param messageArrayList a list of message and peer pairs representing the chat history
     */
    public void setNewUserMessageHistory(ArrayList<Pair<Peer, Message>> messageArrayList) {
        Platform.runLater(() -> {
            ArrayList<String> thisUserStringMessageHistory = new ArrayList<>();
            messageHistory.getItems().clear();
            if (messageArrayList == null) {
                return;
            }
            for(Pair<Peer, Message> messagePair : messageArrayList) {
                PeerAndMessageTextHolder holder;
                try {
                    holder = JsonUtils.fromJson(messagePair.getValue().getContent(), PeerAndMessageTextHolder.class);
                } catch (JsonProcessingException e) {
                    logger.error("Error while parsing json in addMessage | {}", messagePair.getValue());
                    throw new RuntimeException(e);
                }
                String content = holder.messageTextHolder.text;
                messageHistory.getItems().add(messagePair.getKey().getUserName() + " (" + DateUtils.timestampToString(messagePair.getValue().getTimestamp()) + "): " + content);

                thisUserStringMessageHistory.add(content);
            }
        });
    }


    @FXML
    private void onUserListClick(MouseEvent event) throws IOException {
        String selected = userList.getSelectionModel().getSelectedItem();
        if(selected == null) { // if an empty list element was selected
            return;
        }
        int selectedIndex = userList.getSelectionModel().getSelectedIndex();
        UiController.getInstance().setCurrentPeer(selectedIndex);
    }

    public void openPublicChat() {
        Platform.runLater(() -> {
            Stage stage = (Stage) sendButton.getScene().getWindow();
            UiController.getInstance().openPage(Page.PUBLIC_CHAT, stage);
        });
    }

    @FXML
    private void onPublicChatButtonClick(ActionEvent event) {
        Stage stage = (Stage) sendButton.getScene().getWindow();
        UiController.getInstance().openPage(Page.PUBLIC_CHAT, stage);
    }

    private void sendInvite(Peer peer) {
        SendTTTInviteEvent sendTTTInviteEvent = new SendTTTInviteEvent();
        sendTTTInviteEvent.setInput(peer);
        EventHandler.getInstance().handleEvent(sendTTTInviteEvent);
    }

    private void confirmInvite(Peer peer) {
        ConfirmSendTTTInviteEvent confirmTTTInviteEvent = new ConfirmSendTTTInviteEvent();
        confirmTTTInviteEvent.setInput(new Pair<Peer, Boolean>(peer, true));
        EventHandler.getInstance().handleEvent(confirmTTTInviteEvent);
    }

    @FXML
    private void onTicButtonClick(ActionEvent actionEvent) throws IOException {
        TictactoeGameHandler gameHandler = TictactoeGameHandler.getInstance();
        String TTTStatus = gameHandler.getTttPeerID();
        if(TTTStatus == null && gameHandler.checkPendingPeer(currentPeer)) {
            confirmInvite(currentPeer);
            gameHandler.removePeerFromPending(currentPeer);
            Stage stage = (Stage) sendButton.getScene().getWindow();
            gameHandler.setHasFirstTurn(false);
            gameHandler.setTTTPeer(currentPeer);
            UiController.getInstance().openPage(Page.TICTACTOE, stage);
        } else if(TTTStatus == null) {
            sendInvite(currentPeer);
            sendMessage(TictactoeInvitationMessages.INVITATION_MESSAGES.get(TictactoeInvitationStatus.SENT));
            gameHandler.setTTTPeer(currentPeer);
        }
    }

    public void setCurrentPeer(Peer currentPeer) {
        this.currentPeer = currentPeer;
        chatLabel.setText("Chat with " + currentPeer.getUserName());
    }
}
