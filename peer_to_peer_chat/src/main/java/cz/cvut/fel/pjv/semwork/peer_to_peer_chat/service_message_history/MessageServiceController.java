package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_message_history;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.*;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_event_handler.EventHandler;
import javafx.util.Pair;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.ICommand;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.IListen;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_message_history.commands.CommandAddMessage;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_message_history.commands.CommandAddPrivateMessage;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_message_history.commands.CommandGetMessageHistory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessageServiceController implements IListen {
    private static final Logger logger = LoggerFactory.getLogger(MessageServiceController.class);
    private static MessageServiceController instance;
    private final HashMap<String, ArrayList<Message>> messageHistory;
    private final HashMap<Peer, ArrayList<Pair<Peer, Message>>> privateMessageHistory;
    private final Map<EventType, ICommand> commandsRegistry;
    private Peer localPeer = null;
    //private final PeerManager peerManager; //TODO: is it necessary?

    private MessageServiceController() {
        //peerManager = PeerManager.getInstance();
        messageHistory = new HashMap<String, ArrayList<Message>>();
        privateMessageHistory = new HashMap<Peer, ArrayList<Pair<Peer, Message>>>();
        commandsRegistry = new HashMap<EventType, ICommand>(
                Map.of(
                        EventType.ADD_MESSAGE_TO_HISTORY, new CommandAddMessage(this),
                        EventType.ADD_PRIVATE_MESSAGE_TO_HISTORY, new CommandAddPrivateMessage(this),
                        EventType.GET_MESSAGE_HISTORY, new CommandGetMessageHistory(this),
                        EventType.GET_PRIVATE_MESSAGE_HISTORY, new ICommand() {

                            @Override
                            public void execute(Event<?, ?> input) {
                                if (!(input instanceof GetPeerPrivateMessageHistoryEvent)) return;
                                GetPeerPrivateMessageHistoryEvent temp = (GetPeerPrivateMessageHistoryEvent) input;
                                Peer target = temp.getInput();
                                temp.setOutput(getPeerPrivateMessage(target));
                            }
                        },
                        EventType.ADD_MORE_MESSAGES, new ICommand() {
                            @Override
                            public void execute(Event<?, ?> input) {
                                if (!(input instanceof AddMoreMessagesToHistoryEvent temp)) return;
                                Pair<String, Message[]> pair = temp.getInput();
                                String peerId = pair.getKey();
                                for (Message message : pair.getValue()) {
                                    addMessage(peerId, message);
                                }
                            }
                        }
                ));
    }

    /**
     * Returns the singleton instance of the MessageServiceController.
     * This method ensures that the instance is only accessed from the allowed class.
     * If the method is called from a non-allowed class, a {@link SecurityException} will be thrown.
     *
     * @return the singleton instance of the MessageServiceController
     * @throws SecurityException if the method is called from a class that is not allowed to access it
     */
    public static MessageServiceController getInstance() {
        // Block non main calls
        String allowedCaller = "cz.cvut.fel.pjv.semwork.peer_to_peer_chat._main.Application"; // fully-qualified class name

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 2) {
            String callerClassName = stackTrace[2].getClassName();
            if (!callerClassName.equals(allowedCaller)) {
                throw new SecurityException("Unauthorized caller: " + callerClassName + " PLEASE USE EVENTS OR START FROM " + allowedCaller);
            }
        }
        // end
        if (instance == null) {
            instance = new MessageServiceController();
        }
        return instance;
    }

    /**
     * Adds a new message to the message history for the specified peer.
     * If the peer does not already have a history, a new entry will be created.
     *
     * @param message the message to be added
     */
    public synchronized void addMessage(String peerId, Message message) {
        if (peerId == null || message == null) {
            return; // TODO: log
        }
        logger.info("Adding new message from peer '{}'", peerId);
        if (!messageHistory.containsKey(peerId)) {
            messageHistory.put(peerId, new ArrayList<Message>());
        }
        for (Message m : messageHistory.get(peerId)) {
            if (m.getMessageId().equals(message.getMessageId())) {
                return;
            }
        }
        messageHistory.get(peerId).add(message);
    }

    /** 
     * Adds a new private message to the private message history for the specified peer.
     * If the peer does not already have a private message history, a new entry will be created.
     *
     * @param peer             the peer sending the private message
     * @param peerMessagePair a pair containing the recipient peer and the message
     */
    public void addPrivateMessage(Peer peer, Pair<Peer, Message> peerMessagePair) {
        if (peer == null || peerMessagePair == null) {
            return; // TODO: log
        }
        logger.info("Adding new private message from peer '{}'", peer.getPeerId());
        if (!privateMessageHistory.containsKey(peer)) {
            privateMessageHistory.put(peer, new ArrayList<Pair<Peer, Message>>());
        }
        privateMessageHistory.get(peer).add(peerMessagePair);
    }

    public HashMap<String, ArrayList<Message>> getMessageHistory() {
        return messageHistory;
    }

    public HashMap<Peer, ArrayList<Pair<Peer, Message>>> getPrivateMessageHistory() {
        return privateMessageHistory;
    }

    @Override
    public void handleEvent(Event<?, ?> event) {
        EventType type = event.getType();
        if (this.commandsRegistry.containsKey(type)) {
            ICommand command = this.commandsRegistry.get(type);
            command.execute(event);
        }
    }

    public ArrayList<Pair<Peer, Message>> getPeerPrivateMessage(Peer peer) {
        return privateMessageHistory.get(peer);
    }

    private void removePrivatePeerMessages(Peer peer) {
        if (!privateMessageHistory.containsKey(peer)) return;
        privateMessageHistory.remove(peer);
    }

}
