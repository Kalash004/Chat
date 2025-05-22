package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.reading;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.configs.ApiConfig;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.GetLocalPeerIDEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_event_handler.EventHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * The MessageServer class handles incoming socket connections in a
 * peer-to-peer chat application. It listens for connections and
 * creates a new ClientServer instance for each connection.
 */
public class MessageServer implements Runnable {

    /** Logger instance for logging server activity */
    private static final Logger logger = LoggerFactory.getLogger(MessageServer.class);

    /** Port number to listen on */
    private int port;

    /** Run flag for controlling the server loop */
    private boolean runFlag;

    /** Singleton instance of MessageServer */
    private static MessageServer instance;

    /** The server socket used to accept connections */
    private ServerSocket serverSocket;

    /** List of active client connections */
    private ArrayList<ClientServer> clientServers;

    /** ID of the local peer */
    private String peerId;

    /** Username of the local peer */
    private String userName;

    /** Thread running the server loop */
    private Thread thread;

    /** Event handler for system events */
    private EventHandler eventHandler;

    /**
     * Returns the singleton instance of MessageServer.
     *
     * @return the MessageServer instance
     */
    public static MessageServer getInstance() {
        if (instance == null) {
            instance = new MessageServer();
        }
        return instance;
    }

    /**
     * Private constructor to enforce singleton pattern.
     */
    private MessageServer() {
        // Empty constructor for singleton pattern
    }

    /**
     * Sets the port manually (for testing purposes).
     *
     * @param port the port number to set
     */
    public void testingSetPort(int port) {
        this.port = port;
    }

    /**
     * Starts the server loop and listens for client connections.
     * Each accepted connection is handed off to a ClientServer instance.
     */
    @Override
    public void run() {
        logger.info("MessageServer started, listening for connections on {}:{}",
                this.serverSocket.getInetAddress().getHostAddress(), this.serverSocket.getLocalPort());
        while (runFlag) {
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
            } catch (SocketTimeoutException e) {
                continue; // normal, loop again
            } catch (IOException e) {
                logger.error("Error accepting client connection", e);
                throw new RuntimeException(e);
            }

            ClientServer clientServer = new ClientServer(this, clientSocket, this.peerId, this.userName, this.eventHandler);
            clientServer.start();
            this.clientServers.add(clientServer);
        }
        logger.info("MessageServer stopping...");
        this.thread.interrupt();
    }

    /**
     * Initializes the MessageServer, sets up the server socket, and starts the thread.
     */
    public void start() {
        this.runFlag = true;
        this.eventHandler = EventHandler.getInstance();
        this.clientServers = new ArrayList<>();
        this.port = ApiConfig.getMessagingPort();
        GetLocalPeerIDEvent event = new GetLocalPeerIDEvent();
        eventHandler.handleEvent(event);
        this.peerId = event.getOutput();
        this.userName = ApiConfig.getUserName();
        this.thread = new Thread(this, "MessageServer");

        logger.info("Starting MessageServer thread...");
        try {
            this.serverSocket = new ServerSocket(this.port);
            serverSocket.setSoTimeout(500);
            logger.info("MessageServer initialized on port {}", this.port);
        } catch (IOException e) {
            logger.error("Failed to initialize MessageServer on port {}", this.port, e);
            return;
        }

        if (this.thread == null) {
            this.thread = new Thread(this, "MessageServer");
        }

        this.thread.start();
    }

    /**
     * Stops the server and all active client threads.
     * Waits for server thread to finish execution.
     */
    public void stop() {
        if (!runFlag) {
            logger.warn("Calling stop when already stopped");
            return;
        }
        logger.info("Stopping MessageServer...");
        this.runFlag = false;

        for (ClientServer clientServer : this.clientServers) {
            if (clientServer == null) continue;
            clientServer.stop();
        }

        try {
            this.thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        this.clientServers.clear();
        this.thread = null;
    }

    /**
     * Waits for the server and all client threads to finish.
     */
    public void join() {
        if (this.thread == null) {
            return;
        }

        for (ClientServer clientServer : this.clientServers) {
            clientServer.join();
        }

        try {
            this.thread.join();
            logger.info("MessageServer thread joined.");
        } catch (InterruptedException e) {
            logger.error("Interrupted while joining MessageServer thread", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns an array of all current client server instances.
     *
     * @return array of ClientServer objects
     */
    public ClientServer[] getClientServer() {
        return this.clientServers.toArray(new ClientServer[0]);
    }

    /**
     * Removes a given ClientServer from the active list.
     *
     * @param clientServer the client server to remove
     */
    public void removeClientServer(ClientServer clientServer) {
        this.clientServers.remove(clientServer);
    }
}
