package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api._factory;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.configs.ApiConfig;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.GetLocalPeerIDEvent;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.discovery.DiscoveryClient;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.discovery.DiscoveryServer;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.sending.MessageClient;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_event_handler.EventHandler;

/**
 * Factory class for creating and configuring instances related to the connection API.
 * <p>
 * This class fetches settings from {@link ApiConfig} and instantiates the appropriate
 * networking classes used in the peer-to-peer chat system.
 */
public class ConnectionApiFactory {

    /**
     * Creates a new instance of {@link DiscoveryServer} with configuration values
     * from {@link ApiConfig}.
     *
     * @return a configured DiscoveryServer instance
     */
    public static DiscoveryServer getDiscoveryServer() {
        return new DiscoveryServer(
                ApiConfig.getDiscoveryServerSourcePort(),
                ApiConfig.getDefualtPacketSize(),
                ApiConfig.getDiscoveryTimeoutMillis(),
                ApiConfig.getDiscoveryServerTargetPort()
        );
    }

    /**
     * Creates a new instance of {@link DiscoveryClient}.
     * <p>
     * Retrieves the local peer ID using {@link GetLocalPeerIDEvent}, then
     * constructs the client with values from {@link ApiConfig}.
     *
     * @return a configured DiscoveryClient instance
     */
    public static DiscoveryClient getDiscoveryClient() {
        GetLocalPeerIDEvent getLocalPeerIDEvent = new GetLocalPeerIDEvent();
        EventHandler.getInstance().handleEvent(getLocalPeerIDEvent);
        String localPeer = getLocalPeerIDEvent.getOutput();
        return new DiscoveryClient(
                ApiConfig.getDiscoveryServerTargetPort(),
                ApiConfig.getDefualtPacketSize(),
                localPeer,
                ApiConfig.getDiscoveryTimeoutMillis(),
                ApiConfig.getDiscoverySocketSleep(),
                ApiConfig.getDiscoveryBroadcastIpAddress(),
                ApiConfig.getDiscoveryClientSourcePort(),
                ApiConfig.getMessagingPort(),
                ApiConfig.getUserName()
        );
    }

    /**
     * Creates a new instance of {@link MessageClient} with a configured timeout.
     *
     * @return a configured MessageClient instance
     */
    public static MessageClient getMessagingClient() {
        return new MessageClient(ApiConfig.getMessagingTimeoutMillis());
    }
}
