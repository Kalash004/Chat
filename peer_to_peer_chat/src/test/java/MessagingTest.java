import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.configs.ApiConfig;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.generic_messages.ConReqMessage;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.PeerManager;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.reading.MessageServer;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.sending.MessageClient;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_event_handler.EventHandler;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.utils.json.JsonUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;

public class MessagingTest {
    private static final Logger logger = LoggerFactory.getLogger(MessagingTest.class);

    @Test
    public void sendConReqMessageTest() throws Exception {
        // Options
        int timeout = 10000;
        int targetPort = 22222;
        InetAddress targetAddress = InetAddress.getLoopbackAddress();

        // Assemble client, message, and peer
        String localPeerId = UUID.randomUUID().toString();
        String userName = "Test";
        int messagingPort = 1234;

        String targetPeerId = UUID.randomUUID().toString();
        String targetUserName = "T";

        Message response_holder = new Message();

        MessageClient client = new MessageClient(timeout);
        Message message = new ConReqMessage(localPeerId, messagingPort, userName).getMessage();
        Peer peer = new Peer(targetPeerId, targetUserName, targetAddress, targetPort);

        // Act
        Thread serverThread = new Thread(() -> startTestServer(targetPort, response_holder));
        serverThread.start();
        client.sendMessage(message, peer);
        serverThread.join();
        // Assert
        Assertions.assertEquals(response_holder, message);
        // (you can later assert what was received)
    }

    @Test
    public void testConnectionRequestMessage() {
        // Options
        int timeout = 10000;
        int targetPort = ApiConfig.getMessagingPort();
        InetAddress targetAddress = InetAddress.getLoopbackAddress();

        // Assemble client, message, and peer
        String clientPeerId = UUID.randomUUID().toString();
        String userName = "Test";
        int messagingPort = 12111;

        String serverPeerId = UUID.randomUUID().toString();
        String targetUserName = "T";

        Message response_holder = new Message();

        MessageClient client = new MessageClient(timeout);
        MessageServer server = MessageServer.getInstance();
        Message message = new ConReqMessage(clientPeerId, messagingPort, userName).getMessage();
        Peer peer = new Peer(serverPeerId, targetUserName, targetAddress, targetPort);

        PeerManager peerManager = PeerManager.getInstanceForTesting();
        EventHandler.getInstance().addListener(EventReceiver.PEER_MANAGER, peerManager);

        // Act
        server.start();
        client.sendMessage(message, peer);
        server.stop();
        server.join();

        // Asses
        Assertions.assertTrue(PeerManager.getInstanceForTesting().containsPeerId(clientPeerId));
        Peer resultPeerId = PeerManager.getInstanceForTesting().getPeer(clientPeerId);
        try {
            Assertions.assertEquals(new Peer(clientPeerId, userName, InetAddress.getByName("127.0.0.1"), messagingPort), resultPeerId);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Passed " + this.getClass().getName());
    }

    private void startTestServer(int port, Message response_holder) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[TestServer] Listening on port " + port);
            Socket clientSocket = serverSocket.accept(); // Accept one connection
            System.out.println("[TestServer] Client connected: " + clientSocket.getInetAddress());

            // Receive the message
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            System.out.println("[TestServer] Reading input stream");
            String receivedJson;
            receivedJson = (String) in.readObject();
            System.out.println("[TestServer] Received message: " + receivedJson);

            // Parse received JSON into Message
            Message receivedMessage = JsonUtils.fromJson(receivedJson, Message.class);
            // Send a response back (assuming successful processing)
            String responseJson = JsonUtils.toJson(receivedMessage);
            out.writeObject(responseJson);
            out.flush();
            System.out.println("[TestServer] Sent response: " + responseJson);
            clientSocket.close();
            receivedMessage.copyTo(response_holder);
        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
}