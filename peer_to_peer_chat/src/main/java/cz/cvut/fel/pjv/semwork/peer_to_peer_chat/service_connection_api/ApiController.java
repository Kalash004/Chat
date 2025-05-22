package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.configs.ApiConfig;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.generic_messages.*;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.peer.SynchronizationStatus;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.*;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.commands.*;
import javafx.util.Pair;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.message_content_classes.PeerAndMessageTextHolder;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.message_content_classes.TicTacToeGameStateHolder;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.user_messages.PrivateMessage;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.user_messages.PublicMessage;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.connection.MessageType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.coroutine.CoroutineType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.tictactoe.TictactoeSpaceStatus;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.ICommand;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.ICorutine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api._factory.ConnectionApiFactory;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.discovery.DiscoveryController;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.reading.MessageServer;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.sending.MessageClient;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_coroutine.CoroutineService;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_event_handler.EventHandler;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.IListen;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_message_history.MessageServiceController;

import java.util.*;

/**
 * This is an interface for the communication between peers
 */
public class ApiController implements IListen {
    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    private static ApiController instance = null;

    private DiscoveryController discoveryController;
    private MessageClient messageClient;
    private MessageServer messageServer;

    private EventHandler eventHandler;
    private final HashMap<EventType, ICommand> commandsRegistry;
    private String localPeerId;
    private int messagingPort;
    private String userName;
    private MessageServiceController messageServiceController;

    public static ApiController getInstance() {
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
            instance = new ApiController();
        }
        return instance;
    }

    private ApiController() {
        // TODO: maybe return

        this.commandsRegistry = new HashMap<>(Map.ofEntries(
                Map.entry(EventType.PROGRAM_START, new CommandStartApi(this)),
                Map.entry(EventType.SEND_CONACK, new CommandSendConAck(this)),
                Map.entry(EventType.SEND_CONREQ, new CommandSendConReq(this)),
                Map.entry(EventType.SEND_HELLO, new CommandSendHello(this)),
                Map.entry(EventType.SEND_PUBLIC_MESSAGE, new CommandSendPublicMessage(this)),
                Map.entry(EventType.PROGRAM_END, new CommandProgramEnd(this)),
                Map.entry(EventType.CONNECTION_REQUESTED, new ICommand() {
                    @Override
                    public void execute(Event<?, ?> input) {
                        if (!(input instanceof ConnectionRequestEvent connectionRequestEvent)) {
                            return;
                        }
                        Boolean res = sendConAck(connectionRequestEvent.getInput());
                        connectionRequestEvent.setOutput(res);
                    }
                }),
                Map.entry(EventType.PRIVATE_MESSAGE_SEND, new ICommand() {
                    @Override
                    public void execute(Event<?, ?> input) {
                        if (!(input instanceof PrivateMessageSendEvent)) return;
                        Pair<Peer, String> dataInput = ((PrivateMessageSendEvent) input).getInput();
                        sendPrivateMessage(dataInput.getKey(), dataInput.getValue());
                    }
                }),
                Map.entry(EventType.SEND_TTT_INVITE, new ICommand() {
                    @Override
                    public void execute(Event<?, ?> input) {
                        if (!(input instanceof SendTTTInviteEvent retype)) return;
                        Peer target = retype.getInput();
                        sendGameInvite(target, MessageType.TIC_TAC_TOE_INVITE);
                    }
                }),
                Map.entry(EventType.TTT_STATUS_SEND, new ICommand() {
                    @Override
                    public void execute(Event<?, ?> input) {
                        if (!(input instanceof TTTStatusSendEvent retype)) return;
                        Peer target = retype.getInput().getKey();
                        sendGameState(target, retype.getInput().getValue(), MessageType.TIC_TAC_TOE_STATUS);
                    }
                }),
                Map.entry(EventType.CONFIRM_SEND_TTT_INVITE, new ICommand() {

                    @Override
                    public void execute(Event<?, ?> input) {
                        if (!(input instanceof ConfirmSendTTTInviteEvent retype)) return;
                        Peer target = retype.getInput().getKey();
                        Boolean status = retype.getInput().getValue();
                        sendGameInviteConfirmation(target, status);
                    }
                })
        ));
    }


    public void launch() {
        //TODO:
        this.eventHandler = EventHandler.getInstance();
        this.messageClient = ConnectionApiFactory.getMessagingClient();
        this.userName = ApiConfig.getUserName();
        this.messagingPort = ApiConfig.getMessagingPort();
        this.discoveryController = DiscoveryController.getInstance();
        this.messageServer = MessageServer.getInstance();
        GetLocalPeerIDEvent getLocalPeerIDEvent = new GetLocalPeerIDEvent();
        eventHandler.handleEvent(getLocalPeerIDEvent);
        this.localPeerId = getLocalPeerIDEvent.getOutput();
        this.start();
        CoroutineService.getInstance().addCoroutine(new ICorutine() {
            Date now = new Date();
            int sleeptimeSec = 1;

            @Override
            public CoroutineType getCoroutineType() {
                return CoroutineType.INFINITE;
            }

            @Override
            public boolean resume() {
                if (new Date().getTime() >= now.getTime() + sleeptimeSec * 1000) {
                    sendAliveCheck();
                    now = new Date();
                }
                return true;
            }
        });
    }

    @Override
    public void handleEvent(Event<?, ?> event) {
        EventType type = event.getType();
        if (this.commandsRegistry.containsKey(type)) {
            ICommand command = this.commandsRegistry.get(type);
            command.execute(event);
        }
    }

    /**
     * Send a messages of disconnection
     */
    public void sendBye() {
        logger.info("Sending bye");
        String peerId = this.localPeerId;
        ByeMessage byeMessage = new ByeMessage(peerId);
        if (this.messageClient != null) {
            this.messageClient.sendMessageToAllPeers(byeMessage.getMessage());
        }
    }

    /**
     * Initiate connection with other users
     * @param userName
     */
    public void sendHello(String userName) {
        ApiConfig.setUserName(userName);
        logger.info("Username set to {}", userName);
        this.discoveryController = DiscoveryController.getInstance();
    }

    /**
     * Send connection acknowledgment. After connection request handling
     * @param peer
     * @return
     */
    public boolean sendConAck(Peer peer) {
        GetMessageHistoryEvent getMessageHistoryEvent = new GetMessageHistoryEvent();
        eventHandler.handleEvent(getMessageHistoryEvent);
        HashMap<String, ArrayList<Message>> message_history = getMessageHistoryEvent.getOutput();
        Peer[] connected_peers;
        GetConnectedPeerListEvent event = new GetConnectedPeerListEvent();
        eventHandler.handleEvent(event);
        ConAckMessage message = new ConAckMessage(this.localPeerId, message_history, event.getOutput());
        logger.info("Sending connection acknowledgement to peer: {} | TXT: {}", peer, message.getMessage().toString());
        IsPeerConnectedEvent peerExists = (IsPeerConnectedEvent) new IsPeerConnectedEvent().setInput(peer);
        eventHandler.handleEvent(peerExists);
        if (peerExists.getOutput()) {
            GetConnectedPeerEvent getPeer = (GetConnectedPeerEvent) new GetConnectedPeerEvent().setInput(peer.getPeerId());
            eventHandler.handleEvent(getPeer);
            Peer savedPeer = getPeer.getOutput();
            if (! savedPeer.getStatus().equals(SynchronizationStatus.SYNCHRONIZED)) {
                savedPeer.setStatus(SynchronizationStatus.SYNCHRONIZED);
                logger.info("Sending conreq");
                sendConReq(savedPeer);
            }

        }
        return this.messageClient.sendMessage(message.getMessage(), peer);
    }

    /**
     * Send public message to all peers
     * @param content
     */
    public void sendPublicMessage(String content) {
        PublicMessage message = new PublicMessage(this.localPeerId, content, userName);
        this.messageClient.sendMessageToAllPeers(message.getMessage());
        logger.info("Public message sent to all peers | Text: {}", message.getMessage().toString());

        GetLocalPeerEvent getLocalPeerEvent = new GetLocalPeerEvent();
        eventHandler.handleEvent(getLocalPeerEvent);
        Peer thisPeer = getLocalPeerEvent.getOutput();
        AddMessageToHistoryEvent addMessageToHistoryEvent = new AddMessageToHistoryEvent();
        addMessageToHistoryEvent.setInput(new Pair<String,Message>(thisPeer.getPeerId(), message.getMessage()));
        eventHandler.handleEvent(addMessageToHistoryEvent);
    }

    /**
     * Send private message to one peer
     * @param targetPeer
     * @param content
     */
    public void sendPrivateMessage(Peer targetPeer, String content) {
        PeerAndMessageTextHolder holder = new PeerAndMessageTextHolder(targetPeer.getPeerId(), content);
        PrivateMessage message = new PrivateMessage(this.localPeerId, holder.toJson(), userName);
        this.messageClient.sendMessage(message.getMessage(), targetPeer);
        logger.info("Private message sent | Peer: {} | Text: {}", targetPeer, content);
        GetLocalPeerEvent getLocalPeerEvent = new GetLocalPeerEvent();
        eventHandler.handleEvent(getLocalPeerEvent);
        Peer thisPeer = getLocalPeerEvent.getOutput();
        AddPrivateMessageToHistoryEvent addMessageToHistoryEvent = new AddPrivateMessageToHistoryEvent();
        addMessageToHistoryEvent.setInput(new Pair<>(targetPeer, new Pair<Peer, Message>(thisPeer, message.getMessage())));
        eventHandler.handleEvent(addMessageToHistoryEvent);
    }

    /**
     * Sends alive check to keep the connection or remove dead peers
     */
    public void sendAliveCheck() {
        Message message = new PublicMessage(this.localPeerId, null, null).getMessage();
        message.setType(MessageType.ALIVE_CHECK);
        Peer[] connected_peers;
        GetConnectedPeerListEvent event = new GetConnectedPeerListEvent();
        eventHandler.handleEvent(event);
        connected_peers = event.getOutput();
        for (Peer peer : connected_peers) {
            boolean status = this.messageClient.sendMessage(message, peer);
            if (status) continue;
            RemovePeerEvent removePeerEvent = new RemovePeerEvent();
            removePeerEvent.setInput(peer.getPeerId());
            eventHandler.handleEvent(removePeerEvent);
        }
    }

    /**
     * Send connection request
     * @param peer
     */
    public void sendConReq(Peer peer) {
        ConReqMessage message = new ConReqMessage(this.localPeerId, this.messagingPort, this.userName);
        this.messageClient.sendMessage(message.getMessage(), peer);
    }

    public void sendMessage(Message message, Peer peer) {
        this.messageClient.sendMessage(message, peer);
    }

    public void sendGameInvite(Peer peer, MessageType messageType) {
        Message message = new GameInviteMessage(this.localPeerId, UUID.randomUUID().toString(), this.userName, messageType).getMessage();
        this.messageClient.sendMessage(message, peer);
    }

    private void sendGameState(Peer target, TictactoeSpaceStatus[][] gameState, MessageType messageType) {
        Message message = new GameStatusUpdateMessage<TicTacToeGameStateHolder>(localPeerId, userName,
                MessageType.TIC_TAC_TOE_STATUS, new TicTacToeGameStateHolder(gameState)).getMessage();
        this.messageClient.sendMessage(message, target);
    }

    private void sendGameInviteConfirmation(Peer peer, Boolean state) {
        Message message = new GameConfirmationMessage(state, localPeerId, userName).getMessage();
        this.messageClient.sendMessage(message, peer);
    }

    public void stop() {
        endDiscovery();
        endMessaging();
    }

    private void endDiscovery() {
        if (discoveryController == null) {
            return;
        }
        this.discoveryController.stop();
    }

    private void endMessaging() {
        if (messageServer == null) {
            logger.warn("[endMessaging()] No message server available to stop");
            return;
        }
        messageServer.stop();
    }

    public void start() {
        this.discoveryController.startDiscovery();
        this.messageServer.start();
    }
}
