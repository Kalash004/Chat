package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.reading;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.message_content_classes.InviteConfirmationHolder;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.message_content_classes.TicTacToeGameStateHolder;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.tictactoe.TictactoeSpaceStatus;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.*;
import javafx.util.Pair;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.generic_messages.ResponseMessage;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.message_content_classes.MessageHistoryAndPeersContent;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.message_content_classes.PeerAndMessageTextHolder;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.connection.MessageType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.message_status.MessageStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_event_handler.EventHandler;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.utils.json.JsonUtils;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.utils.message.MessageUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Handles communication with a single peer in a peer-to-peer chat system.
 * This includes reading and parsing incoming messages, handling them,
 * and sending appropriate responses.
 */
public class ClientServer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ClientServer.class);

    private final String userName;
    private final Socket socket;
    private final Thread thread;
    private final String localPeerId;
    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream objectOutputStream;
    private final MessageServer messageServer;
    private final EventHandler eventHandler;

    /**
     * Constructs a ClientServer instance for a connected peer.
     *
     * @param messageServer the message server managing clients
     * @param clientSocket  the connected socket
     * @param peerId        local peer ID
     * @param userName      local username
     * @param eventHandler  event handler for communication and state changes
     */
    public ClientServer(MessageServer messageServer, Socket clientSocket, String peerId, String userName, EventHandler eventHandler) {
        this.messageServer = messageServer;
        this.socket = clientSocket;
        this.localPeerId = peerId;
        this.userName = userName;
        this.eventHandler = eventHandler;
        try {
            this.objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
            this.objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            this.objectOutputStream.flush();
        } catch (IOException e) {
            logger.error("Failed to initialize streams for client", e);
            throw new RuntimeException(e);
        }
        thread = new Thread(this, "ClientServer");
    }

    /**
     * Starts the message receiving and processing loop.
     */
    @Override
    public void run() {
        String receivedJson = receiveMessage();

        Message receivedMessage;
        try {
            receivedMessage = JsonUtils.fromJson(receivedJson, Message.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        boolean isGood = dealWithMessage(receivedMessage);
        MessageStatus status = (isGood) ? MessageStatus.OK : MessageStatus.FAIL;

        sendResponse(status);
        closeSocket(socket);

        this.messageServer.removeClientServer(this);
    }

    /**
     * Reads an incoming message from the peer.
     *
     * @return JSON string representing the message, or null on error
     */
    private String receiveMessage() {
        try {
            return (String) this.objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Failed to receive message from peer", e);
        }
        return null;
    }

    /**
     * Sends a status response message to the peer.
     *
     * @param status status to send (OK/FAIL)
     */
    private void sendResponse(MessageStatus status) {
        Message resp = new ResponseMessage(localPeerId, userName, status).getMessage();
        String responseJson = JsonUtils.toJson(resp);
        try {
            this.objectOutputStream.writeObject(responseJson);
            this.objectOutputStream.flush();
        } catch (IOException e) {
            logger.error("Failed to send response to peer ", e);
        }
    }

    /**
     * Closes the socket connection.
     *
     * @param socket the socket to close
     */
    private void closeSocket(Socket socket) {
        try {
            socket.close();
        } catch (IOException e) {
            logger.error("Error while closing socket", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Processes the received message according to its type.
     *
     * @param message the received message
     * @return true if message handled successfully
     */
    private boolean dealWithMessage(Message message) {
        if (message == null) return false;
        if (message.getPeerId().equals(localPeerId)) return false;
        if (message.getType() != MessageType.ALIVE_CHECK) {
            logger.info("Got message | M: {}", message);
        }
        try {
            switch (message.getType()) {
                case MessageType.MESSAGE_PRIVATE -> {
                    return handlePrivateMessage(message);
                }
                case MessageType.CONNECTION_REQUEST -> {
                    return handleConnectionRequest(message);
                }
                case MessageType.MESSAGE_PUBLIC -> {
                    return handlePublicMessage(message);
                }
                case MessageType.BYE -> {
                    return handleByeMessage(message);
                }
                case MessageType.CONNECTION_ACKNOWLEDGEMENT -> {
                    return handleConnectionAcknowledgement(message);
                }
                case MessageType.ALIVE_CHECK -> {
                    return true;
                }
                case MessageType.TIC_TAC_TOE_STATUS -> {
                    return handleTicTacToeStatus(message);
                }
                case MessageType.TIC_TAC_TOE_INVITE -> {
                    return handleTicTacToeInvite(message);
                }
                case MessageType.GAME_CONFIRMATION -> {
                    return handleGameConfirmation(message);
                }
                default -> {
                    logger.warn("Unknown message type '{}'", message.getType());
                    return false;
                }
            }
        } catch (Exception e) {
            logger.error("Error while dealing with message", e);
            return false;
        }
    }

    /**
     * Handles a confirmation for a Tic Tac Toe game invite.
     */
    private boolean handleGameConfirmation(Message message) {
        ConfirmReceiveTTTInviteEvent targetEvent = new ConfirmReceiveTTTInviteEvent();
        GetConnectedPeerEvent connectedPeerEvent = (GetConnectedPeerEvent) new GetConnectedPeerEvent().setInput(message.getPeerId());
        eventHandler.handleEvent(connectedPeerEvent);
        Peer targetPeer = connectedPeerEvent.getOutput();
        InviteConfirmationHolder holder;
        try {
            holder = JsonUtils.fromJson(message.getContent(), InviteConfirmationHolder.class);
        } catch (JsonProcessingException e) {
            logger.error("Failed to deserialize invite confirmation", e);
            return false;
        }
        targetEvent.setInput(new Pair<>(targetPeer, holder.confirmation));
        eventHandler.handleEvent(targetEvent);
        return true;
    }

    /**
     * Handles an invite to a Tic Tac Toe game.
     */
    private boolean handleTicTacToeInvite(Message message) {
        RecieveTTTInviteEvent targetEvent = new RecieveTTTInviteEvent();
        GetConnectedPeerEvent connectedPeerEvent = (GetConnectedPeerEvent) new GetConnectedPeerEvent().setInput(message.getPeerId());
        eventHandler.handleEvent(connectedPeerEvent);
        Peer peerHolder = connectedPeerEvent.getOutput();
        targetEvent.setInput(peerHolder);
        eventHandler.handleEvent(targetEvent);
        return true;
    }

    /**
     * Handles game state updates for Tic Tac Toe.
     */
    private boolean handleTicTacToeStatus(Message message) {
        ReceiveTTTStatusEvent targetEvent = new ReceiveTTTStatusEvent();
        TicTacToeGameStateHolder gameStateHolder;
        try {
            gameStateHolder = JsonUtils.fromJson(message.getContent(), TicTacToeGameStateHolder.class);
        } catch (JsonProcessingException e) {
            logger.error("Error while dealing with message", e);
            throw new RuntimeException(e);
        }
        GetConnectedPeerEvent getPeer = (GetConnectedPeerEvent) new GetConnectedPeerEvent().setInput(message.getPeerId());
        eventHandler.handleEvent(getPeer);
        Peer peer = getPeer.getOutput();
        TictactoeSpaceStatus[][] spaceHolder = gameStateHolder.spaceStatus.clone();
        logger.info("Space holder : {}", Arrays.deepToString(spaceHolder));
        targetEvent.setInput(new Pair<Peer, TictactoeSpaceStatus[][]>(peer, spaceHolder));
        eventHandler.handleEvent(targetEvent);
        return true;
    }

    /**
     * Handles a private chat message.
     */
    private boolean handlePrivateMessage(Message message) {
        PeerAndMessageTextHolder holder;
        try {
            holder = JsonUtils.fromJson(message.getContent(), PeerAndMessageTextHolder.class);
        } catch (JsonProcessingException e) {
            logger.error("Error during Json parsing private message in dealWithMessage | Message:{}", message);
            throw new RuntimeException(e);
        }
        if (!this.localPeerId.equals(holder.peerIdHolder.peerId)) return false;

        GetConnectedPeerEvent getConnectedPeerEvent = new GetConnectedPeerEvent();
        getConnectedPeerEvent.setInput(message.getPeerId());
        eventHandler.handleEvent(getConnectedPeerEvent);

        logger.info("Received a message with type {} | TXT {} | FROM {}",
                message.getType(), message.getContent(), getConnectedPeerEvent.getOutput().getUserName());

        AddPrivateMessageToHistoryEvent event = new AddPrivateMessageToHistoryEvent();
        event.setInput(new Pair<>(getConnectedPeerEvent.getOutput(), new Pair<>(getConnectedPeerEvent.getOutput(), message)));
        eventHandler.handleEvent(event);
        return true;
    }

    /**
     * Handles a public broadcast message.
     */
    private boolean handlePublicMessage(Message message) {
        GetConnectedPeerEvent getConnectedPeerEvent = new GetConnectedPeerEvent();
        getConnectedPeerEvent.setInput(message.getPeerId());
        eventHandler.handleEvent(getConnectedPeerEvent);

        logger.info("Received a message with type {} | TXT {} | FROM {}",
                message.getType(), message.getContent(), getConnectedPeerEvent.getOutput().getUserName());

        AddMessageToHistoryEvent event = new AddMessageToHistoryEvent();
        event.setInput(new Pair<>(message.getPeerId(), message));
        eventHandler.handleEvent(event);
        return true;
    }

    /**
     * Handles a disconnect (BYE) message.
     */
    private boolean handleByeMessage(Message message) {
        RemovePeerEvent removePeerEvent = new RemovePeerEvent();
        removePeerEvent.setInput(message.getPeerId());
        eventHandler.handleEvent(removePeerEvent);
        return true;
    }

    /**
     * Handles a message acknowledging connection and sharing peers/message history.
     */
    private boolean handleConnectionAcknowledgement(Message message) {
        MessageHistoryAndPeersContent holder;
        try {
            holder = JsonUtils.fromJson(message.getContent(), MessageHistoryAndPeersContent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        for (Peer peer : holder.peers) {
            if (peer.getPeerId().equals(this.localPeerId)) continue;
            AddPeerEvent event = new AddPeerEvent();
            event.setInput(peer);
            eventHandler.handleEvent(event);
            IsPeerConnectedEvent temp = new IsPeerConnectedEvent();
            temp.setInput(peer);
            eventHandler.handleEvent(temp);
            if (!temp.getOutput()) {
                SendConReqEvent sendConReqEvent = new SendConReqEvent();
                sendConReqEvent.setInput(peer);
                eventHandler.handleEvent(sendConReqEvent);
                logger.info("Sending connection req event for peer {} - {}", peer.getPeerId(), peer.getUserName());
            }
        }

        for (String peerId : holder.messages.keySet()) {
            ArrayList<Message> temp = holder.messages.get(peerId);
            for (Message m : temp) {
                eventHandler.handleEvent((AddMessageToHistoryEvent) new AddMessageToHistoryEvent().setInput(new Pair<>(peerId, m)));
            }
        }

        eventHandler.handleEvent(new RefreshMessageHistoryEvent());
        return true;
    }

    /**
     * Handles a new connection request message.
     */
    private boolean handleConnectionRequest(Message message) {
        int port;
        try {
            port = MessageUtils.getPortFromJson(message);
        } catch (RuntimeException e) {
            return false;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Peer tempPeer = new Peer(message.getPeerId(), message.getSenderName(), socket.getInetAddress(), port);
        IsPeerConnectedEvent peerConnectedEvent = new IsPeerConnectedEvent();
        peerConnectedEvent.setInput(tempPeer);
        eventHandler.handleEvent(peerConnectedEvent);
        if (peerConnectedEvent.getOutput()) {
            GetConnectedPeerEvent getPeer = new GetConnectedPeerEvent();
            getPeer.setInput(message.getPeerId());
            eventHandler.handleEvent(getPeer);
            tempPeer = getPeer.getOutput();
            tempPeer.setLastCheckTime(new Date());
        }

        ConnectionRequestEvent event = new ConnectionRequestEvent();
        event.setInput(tempPeer);
        EventHandler.getInstance().handleEvent(event);
        boolean result = false;
        result = (event.getOutput() != null) ? event.getOutput() : false;
        return result;
    }

    /**
     * Starts this ClientServer thread.
     */
    public void start() {
        thread.start();
    }

    /**
     * Stops the thread gracefully.
     */
    public void stop() {
        this.thread.interrupt();
    }

    /**
     * Waits for the thread to finish.
     */
    public void join() {
        try {
            this.thread.join();
        } catch (InterruptedException e) {
            logger.error("Join interrupted for ClientServer thread of peer", e);
            throw new RuntimeException(e);
        }
    }
}
