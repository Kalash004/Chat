package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.discovery;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.configs.ApiConfig;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.generic_messages.DiscoveryHelloMessage;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.coroutine.CoroutineType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.ICorutine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_coroutine.CoroutineService;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.utils.json.JsonUtils;

import java.net.*;
import java.util.Date;

/**
 * The {@code DiscoveryClient} is responsible for broadcasting discovery messages
 * over the network to detect other peers. It sends periodic {@link DiscoveryHelloMessage}
 * packets over a broadcast UDP connection.
 *
 * <p>Discovery messages are sent at regular intervals until the client is stopped or killed.
 */
public class DiscoveryClient implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(DiscoveryClient.class);

    private final int messagingPort;
    private final Thread thread;
    private final int localPort;
    private final int discoveryPortTarget;
    private final int defaultPacketBufferSize;
    private final String peerId;
    private final int timeoutMs;
    private final int socketSleepMs;
    private final InetAddress broadcastAddress;
    private final String userName;

    private DatagramSocket testSocket = null;
    private DatagramSocket clientSocket;
    private Socket discoverySocket;
    private boolean runFlag;

    /**
     * Constructs a new {@code DiscoveryClient}.
     *
     * @param localReaderPort         target discovery port
     * @param defaultPacketBufferSize buffer size for packets
     * @param localPeerId             ID of this peer
     * @param timeoutMs               socket timeout in milliseconds
     * @param socketSleepMs           sleep time between broadcasts in milliseconds
     * @param ipAddress               broadcast IP address
     * @param localPort               port to bind for sending
     * @param messagingPort           port used for messaging
     * @param userName                user's name
     */
    public DiscoveryClient(int localReaderPort, int defaultPacketBufferSize, String localPeerId, int timeoutMs, int socketSleepMs, String ipAddress, int localPort, int messagingPort, String userName) {
        try {
            this.broadcastAddress = InetAddress.getByName(ipAddress);
        } catch (UnknownHostException e) {
            throw new RuntimeException("Invalid ip address", e);
        }
        this.discoveryPortTarget = localReaderPort;
        this.defaultPacketBufferSize = defaultPacketBufferSize;
        this.peerId = localPeerId;
        this.timeoutMs = timeoutMs;
        this.socketSleepMs = socketSleepMs;
        this.thread = new Thread(this, "DiscoveryClient");
        this.runFlag = true;
        this.localPort = localPort;
        this.messagingPort = messagingPort;
        this.userName = userName;
    }

    /**
     * Runs the discovery loop, sending discovery packets periodically.
     * Each message is serialized to JSON and sent via UDP broadcast.
     */
    @Override
    public void run() {
        logger.info("running DiscoveryClient");

        try (DatagramSocket temp = new DatagramSocket(this.localPort)) {
            clientSocket = temp;
            clientSocket.setBroadcast(true);
            clientSocket.setSoTimeout(this.timeoutMs);

            logger.info("Started sending discovery packets on port: {}", this.localPort);

            while (runFlag) {
                String request;
                try {
                    request = JsonUtils.toJson(new DiscoveryHelloMessage(this.peerId, this.messagingPort, this.userName).getMessage());
                } catch (Exception e) {
                    logger.error("Failed to create discovery message", e);
                    throw new RuntimeException(e);
                }

                byte[] sendBuffer = request.getBytes();
                if (sendBuffer.length > this.defaultPacketBufferSize) {
                    logger.warn("Buffer size exceeds the default packet buffer size: {} bytes", sendBuffer.length);
                    throw new RuntimeException("Buffer size exceeds the default packet buffer size");
                }

                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, this.broadcastAddress, this.discoveryPortTarget);
                clientSocket.send(sendPacket);

                try {
                    Thread.sleep(socketSleepMs);
                } catch (InterruptedException e) {
                    logger.warn("Sleep interrupted, continuing with next iteration");
                    Thread.currentThread().interrupt();
                }
            }
            logger.info("Stopped discovery client");
        } catch (Exception e) {
            logger.error("Error in DiscoveryClient run", e);
        }
    }

    /**
     * Starts the discovery client in a separate thread.
     */
    public void start() {
        logger.info("Starting DiscoveryClient thread...");
        thread.start();
    }

    /**
     * Signals the discovery client to stop and schedules coroutine cleanup.
     */
    public void stop() {
        if (!runFlag) {
            return;
        }
        logger.info("Stopping DiscoveryClient thread...");
        runFlag = false;
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
                    kill();
                    return false;
                }
                return true;
            }
        });
    }

    /**
     * Immediately kills the client, closes the socket, and interrupts the thread.
     */
    public void kill() {
        logger.info("Killing DiscoveryClient thread...");
        runFlag = false;
        if (clientSocket == null) {
            logger.error("No client socket available");
            return;
        }
        this.clientSocket.close();
        this.thread.interrupt();
    }

    /**
     * Returns the thread used by this discovery client.
     *
     * @return the internal thread
     */
    public Thread getThread() {
        return thread;
    }

    /**
     * Injects a test socket, primarily for unit testing purposes.
     *
     * @param socket the test socket to use
     */
    public void setTestSocket(DatagramSocket socket) {
        this.testSocket = socket;
    }
}
