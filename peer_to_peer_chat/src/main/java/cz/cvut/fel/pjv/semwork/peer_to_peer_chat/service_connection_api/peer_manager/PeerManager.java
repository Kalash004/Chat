package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.configs.ApiConfig;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.configs.PeerManagerConfig;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.peer.SynchronizationStatus;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.*;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.exceptions.PeerDoesntExist;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.ICommand;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.IListen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.commands.AddPeerCommand;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.commands.GetLocalPeerIDCommand;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_event_handler.EventHandler;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.utils.ip.IpUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Manages peer connections in the peer-to-peer chat system.
 * Handles peer registration, connection events, and peer synchronization.
 */
public class PeerManager implements IListen {
    private static final Logger logger = LoggerFactory.getLogger(PeerManager.class);

    private static PeerManager instance;
    private final int peerLiveMs;
    private final HashMap<String, Peer> connectedPeers;
    private String localPeerId = null;
    private final EventHandler eventHandler;
    private final HashMap<EventType, ICommand> commands;
    private String userName;

    /**
     * Returns the singleton instance of PeerManager.
     * Only allows access from the main Application class.
     *
     * @return singleton instance of PeerManager
     */
    public static synchronized PeerManager getInstance() {
        String allowedCaller = "cz.cvut.fel.pjv.semwork.peer_to_peer_chat._main.Application";

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 2) {
            String callerClassName = stackTrace[2].getClassName();
            if (!callerClassName.equals(allowedCaller)) {
                throw new SecurityException("Unauthorized caller: " + callerClassName + " PLEASE USE EVENTS OR START FROM " + allowedCaller);
            }
        }

        if (instance == null) {
            instance = new PeerManager();
        }
        return instance;
    }

    /**
     * Returns a testing-safe instance of PeerManager.
     * Bypasses main-caller check.
     *
     * @return instance of PeerManager
     */
    public static synchronized PeerManager getInstanceForTesting() {
        if (instance == null) {
            instance = new PeerManager();
        }
        return instance;
    }

    /**
     * Private constructor initializing internal peer structures,
     * event handler and peer command mappings.
     */
    private PeerManager() {
        this.connectedPeers = new HashMap<>();
        this.peerLiveMs = PeerManagerConfig.getPeerExpirationTimeMs();
        this.eventHandler = EventHandler.getInstance();
        this.commands = new HashMap<>(Map.of(
                EventType.CONNECTION_REQUESTED, new AddPeerCommand(this),
                EventType.ADD_PEER, new AddPeerCommand(this),
                EventType.GET_LOCAL_PEER_ID, new GetLocalPeerIDCommand(this),
                EventType.GET_PEER_LIST, new ICommand() {
                    @Override
                    public void execute(Event<?, ?> input) {
                        if (!(input instanceof GetConnectedPeerListEvent)) return;
                        GetConnectedPeerListEvent temp = (GetConnectedPeerListEvent) input;
                        temp.setOutput(getConnectedPeers());
                    }
                },
                EventType.GET_LOCAL_PEER, new ICommand() {
                    @Override
                    public void execute(Event<?, ?> input) {
                        if (!(input instanceof GetLocalPeerEvent)) return;
                        GetLocalPeerEvent temp = (GetLocalPeerEvent) input;
                        temp.setOutput(getLocalPeer());
                    }
                },
                EventType.IS_PEER_CONNECTED, new ICommand() {
                    @Override
                    public void execute(Event<?, ?> input) {
                        if (!(input instanceof IsPeerConnectedEvent)) return;
                        IsPeerConnectedEvent temp = (IsPeerConnectedEvent) input;
                        temp.setOutput(containsPeer(temp.getInput()));
                    }
                },
                EventType.GET_PEER, new ICommand() {
                    @Override
                    public void execute(Event<?, ?> input) {
                        if (!(input instanceof GetConnectedPeerEvent)) return;
                        GetConnectedPeerEvent temp = (GetConnectedPeerEvent) input;
                        temp.setOutput(getPeer(temp.getInput()));
                    }
                },
                EventType.REMOVE_PEER, new ICommand() {
                    @Override
                    public void execute(Event<?, ?> input) {
                        if (!(input instanceof RemovePeerEvent)) return;
                        RemovePeerEvent temp = (RemovePeerEvent) input;
                        Peer peer = getPeer(temp.getInput());
                        removePeer(peer);
                    }
                }
        ));
    }

    /**
     * Adds a peer to the list of connected peers.
     *
     * @param peer the peer to add
     * @return true if successfully added
     * @throws NullPointerException if peer or peer ID is null
     */
    public Boolean addPeer(Peer peer) throws NullPointerException {
        if (peer == null) throw new NullPointerException("Null peer");
        if (peer.getPeerId() == null) throw new NullPointerException("Null peerId for peer : " + peer);

        if (this.connectedPeers.containsKey(peer.getPeerId())) {
            logger.info("Peer " + peer.getPeerId() + " is already connected");
            return true;
        }
        this.connectedPeers.put(peer.getPeerId(), peer);
        logger.info("Added peer {} to peer", peer.getPeerId());
        return true;
    }

    /**
     * Removes a peer from the connected peer list.
     *
     * @param peer the peer to remove
     * @throws PeerDoesntExist if peer is not found
     * @throws NullPointerException if peer is null
     */
    public void removePeer(Peer peer) throws PeerDoesntExist, NullPointerException {
        if (peer == null) {
            logger.error("Removing null peer");
        }
        if (!this.connectedPeers.containsKey(peer.getPeerId())) {
            throw new PeerDoesntExist("Peer : " + peer);
        }
        this.connectedPeers.remove(peer.getPeerId());
    }

    /**
     * Removes peers whose last check-in time exceeds the allowed peer lifetime.
     */
    public void removeOldPeers() {
        for (Peer peer : this.connectedPeers.values()) {
            long duration = new Date().getTime() - peer.getLastCheckTime().getTime();
            long peerLifeTimeMs = TimeUnit.MILLISECONDS.convert(duration, TimeUnit.MILLISECONDS);
            if (peerLifeTimeMs > this.peerLiveMs) {
                this.connectedPeers.remove(peer.getPeerId());
            }
        }
    }

    /**
     * Generates a new unique peer ID using UUID.
     *
     * @return new UUID as string
     */
    public static String generatePeerId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * Gets the ID of the local peer. If none exists, generates one.
     *
     * @return local peer ID
     */
    public String getLocalPeerId() {
        if (localPeerId == null) {
            localPeerId = generatePeerId();
        }
        return this.localPeerId;
    }

    /**
     * Constructs and returns the local peer object.
     *
     * @return local Peer object
     */
    public Peer getLocalPeer() {
        try {
            return new Peer(this.localPeerId, ApiConfig.getUserName(), InetAddress.getByName(IpUtils.getLocalIp()), ApiConfig.getMessagingPort());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves a peer by its ID.
     *
     * @param peerId the peer's ID
     * @return Peer object or null if not found
     */
    public Peer getPeer(String peerId) {
        if (!containsPeerId(peerId)) return null;
        return this.connectedPeers.get(peerId);
    }

    /**
     * Checks if a peer is in the connected list.
     *
     * @param peer the peer object
     * @return true if peer is connected
     */
    public boolean containsPeer(Peer peer) {
        return containsPeerId(peer.getPeerId());
    }

    /**
     * Checks if a peer ID is present in the connected list.
     *
     * @param peerId the peer ID
     * @return true if peer ID is found
     */
    public boolean containsPeerId(String peerId) {
        return this.connectedPeers.containsKey(peerId);
    }

    /**
    /**
     * Returns a list of currently connected peers.
     *
     * @return array of Peer objects
     */
    public Peer[] getConnectedPeers() {
        return this.connectedPeers.values().toArray(new Peer[0]);
    }

    /**
     * Handles incoming events by executing mapped commands.
     *
     * @param event the event to process
     */
    @Override
    public void handleEvent(Event<?, ?> event) {
        if (!this.commands.containsKey(event.getType())) {
            // TODO: Log
            return;
        }
        this.commands.get(event.getType()).execute(event);
    }

    /**
     * Adds a peer and marks it as connection requested.
     * Useful when a peer initiates connection.
     *
     * @param peer the peer to add
     * @return true if added
     */
    public boolean AddPeer(Peer peer) {
        if (peer == null) {
            throw new RuntimeException("Null peer");
        }
        if (this.containsPeer(peer)) return true;
        peer.setLastCheckTime(new Date());
        peer.setStatus(SynchronizationStatus.PEER_REQUESTED_CONNECTION);
        this.connectedPeers.put(peer.getPeerId(), peer);
        return true;
    }
}

