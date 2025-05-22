package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.discovery;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api._factory.ConnectionApiFactory;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.sending.MessageClient;

/**
 * Controller/Api for discovering clients on the net.
 * Uses:
 * @class DiscoveryClient - sends discovery requests {@message_type <strong>hello</strong>}
 * @class DiscoveryServer - accepts discovery requests and requests {@link MessageClient} to send
 * {@message_type <strong>conack</strong}
 * @protocol UDP
 */
public class DiscoveryController {
    private final DiscoveryClient discoveryClient;
    private final DiscoveryServer discoveryServer;
    private static DiscoveryController instance = null;

    public static DiscoveryController getInstance() {
        if (instance == null) {
            instance = new DiscoveryController();
        }
        return instance;
    }

    private DiscoveryController() {
        this.discoveryClient = ConnectionApiFactory.getDiscoveryClient();
        this.discoveryServer = ConnectionApiFactory.getDiscoveryServer();
    }

    public void startDiscovery() {
        this.discoveryServer.start();
        this.discoveryClient.start();
    }

    public void stop() {
        this.discoveryClient.stop();
        this.discoveryServer.stop();
    }
}
