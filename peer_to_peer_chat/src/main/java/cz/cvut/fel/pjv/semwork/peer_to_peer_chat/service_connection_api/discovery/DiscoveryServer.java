package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.discovery;

import com.fasterxml.jackson.core.JsonProcessingException;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.configs.ApiConfig;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.coroutine.CoroutineType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.*;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.ICorutine;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.sending.MessageClient;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_coroutine.CoroutineService;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_event_handler.EventHandler;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.utils.ip.IpUtils;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.utils.json.JsonUtils;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.utils.message.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.Date;

/**
 * {@code DiscoveryServer} listens for incoming UDP broadcast messages (e.g. from {@link DiscoveryClient}).
 * If a message from a new peer is received, it adds the peer and sends a connection request.
 *
 * <p>This is used in peer-to-peer discovery during the initial network handshake.
 */
public class DiscoveryServer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(DiscoveryServer.class);

    private final Thread thread;
    private final int discoveryServerSourcePort;
    private final int defaultPacketSize;
    private final String localIp;
    private final int discoveryTimeout;

    private boolean runFlag = false;
    private DatagramSocket serverSocket;
    private EventHandler eventHandler;
    private String localPeerId;

    /**
     * Constructs a {@code DiscoveryServer} that listens on the given port.
     *
     * @param discoveryServerPort    port to bind the UDP socket to
     * @param defaultPacketSize      size of the expected incoming packet
     * @param discoveryTimeout       socket timeout in milliseconds
     * @param discoveryServerTargetPort unused param (for future use)
     */
    public DiscoveryServer(int discoveryServerPort, int defaultPacketSize, int discoveryTimeout, int discoveryServerTargetPort) {
        this.discoveryServerSourcePort = discoveryServerPort;
        this.defaultPacketSize = defaultPacketSize;
        this.discoveryTimeout = discoveryTimeout;
        this.thread = new Thread(this, "DiscoveryServer");
        this.localIp = IpUtils.getLocalIp();
    }

    /**
     * Starts the discovery listening loop on a UDP socket.
     * Processes incoming broadcast messages and attempts to connect to discovered peers.
     */
    @Override
    public void run() {
        try {
            serverSocket = new DatagramSocket(this.discoveryServerSourcePort);
            serverSocket.setSoTimeout(this.discoveryTimeout);
        } catch (SocketException e) {
            logger.error("Error when creating discovery server socket: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        logger.info("Discovery server started on port {}", this.discoveryServerSourcePort);

        while (this.runFlag) {
            byte[] buffer = new byte[this.defaultPacketSize];
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            try {
                serverSocket.receive(request);
            } catch (SocketTimeoutException e) {
                continue;
            } catch (IOException e) {
                logger.error("Error during discovery server request receive: {}", e.getMessage());
                continue;
            }

            if (request.getAddress().getHostAddress().equals(this.localIp)) {
                continue; // Ignore self-broadcast
            }

            String message = new String(request.getData()).replace("\0", "");
            InetAddress address = request.getAddress();
            String addressString = address.getHostAddress();
            int port = request.getPort();

            Message messageObj;
            try {
                messageObj = JsonUtils.fromJson(message, Message.class);
            } catch (JsonProcessingException e) {
                logger.warn("Failed to parse discovery message JSON", e);
                continue;
            }

            if (messageObj.getPeerId().equals(localPeerId)) continue;

            int peerMessagePort;
            try {
                peerMessagePort = MessageUtils.getPortFromJson(messageObj);
            } catch (JsonProcessingException e) {
                logger.error("Failed to extract peer message port from message", e);
                continue;
            }

            Peer newPeer = new Peer(messageObj.getPeerId(), messageObj.getSenderName(), address, peerMessagePort);

            IsPeerConnectedEvent event = new IsPeerConnectedEvent();
            event.setInput(newPeer);
            eventHandler.handleEvent(event);
            if (event.getOutput()) {
                continue; // Already connected
            }

            logger.info("Received new broadcast request {} from {}:{}", messageObj.getType(), addressString, port);

            AddPeerEvent addPeerEvent = new AddPeerEvent();
            addPeerEvent.setInput(newPeer);
            EventHandler.getInstance().handleEvent(addPeerEvent);

            SendConReqEvent sendConReqEvent = new SendConReqEvent();
            sendConReqEvent.setInput(newPeer);
            EventHandler.getInstance().handleEvent(sendConReqEvent); // TODO: Implement sending conack
        }

        serverSocket.close();
        logger.info("Stopped discovery server");
    }

    /**
     * Starts the discovery server thread and initializes the peer ID.
     */
    public void start() {
        this.eventHandler = EventHandler.getInstance();
        GetLocalPeerIDEvent peerIDEvent = new GetLocalPeerIDEvent();
        eventHandler.handleEvent(peerIDEvent);
        this.localPeerId = peerIDEvent.getOutput();
        this.runFlag = true;
        this.thread.start();
    }

    /**
     * Stops the server gracefully and schedules a coroutine to kill it if not already terminated.
     */
    public void stop() {
        if (!runFlag) return;
        logger.info("Stopping DiscoveryServer thread...");
        this.runFlag = false;
        CoroutineService.getInstance().addCoroutine(new ICorutine() {
            final Date curTime = new Date();
            final int endDeltaTimeMs = ApiConfig.getDiscoveryKillTimeMs();

            @Override
            public CoroutineType getCoroutineType() {
                return CoroutineType.FINISHABLE;
            }

            @Override
            public boolean resume() {
                if (new Date().getTime() >= curTime.getTime() + endDeltaTimeMs) {
                    logger.info("Killing discovery server");
                    kill();
                    return false;
                }
                return true;
            }
        });
    }

    /**
     * Immediately terminates the discovery server and interrupts the thread.
     */
    public void kill() {
        logger.info("Killing DiscoveryServer thread...");
        this.runFlag = false;
        this.serverSocket.close();
        this.thread.interrupt();
    }
}
