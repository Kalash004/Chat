import org.junit.jupiter.api.Test;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.discovery.DiscoveryClient;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_coroutine.CoroutineService;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import static org.junit.jupiter.api.Assertions.*;

public class DiscoveryTest {

    @Test
    public void testDiscoveryClientSendsPacket() throws Exception {
        // Arrange
        String testPeerId = "peer123";
        int port_recieve = 2020;
        int port_send = 2021;
        int bufferSize = 1024;
        int timeout = 1000;
        int sleep = 100;
        int messagingPort = 2222;
        String ipAddress = "255.255.255.255";
        CoroutineService cors = CoroutineService.getInstance();
        cors.stop();
        cors.start();

        // Start the receiver in a separate thread
        Thread receiverThread = new Thread(() -> {
            try (DatagramSocket receiverSocket = new DatagramSocket(port_recieve)) {
                receiverSocket.setSoTimeout(timeout);
                byte[] buffer = new byte[bufferSize];
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

                receiverSocket.receive(receivePacket);  // Wait for packet

                // Verify packet data
//                assertEquals(InetAddress.getByName("177.0.0.1"), receivePacket.getAddress());
                assertEquals(port_send, receivePacket.getPort());
                System.out.println("Received packet: " + new String(receivePacket.getData()).trim());
                System.out.println("Recieved port: " + receivePacket.getPort() + " vs Sent port: " + port_send);
            } catch (Exception e) {
                e.printStackTrace();
                fail("Receiver socket failed");
            }
        });

        // Create the DiscoveryClient
        DiscoveryClient discoveryClient = new DiscoveryClient(port_recieve, bufferSize, testPeerId, timeout, sleep, ipAddress, port_send, messagingPort, "Test");

        receiverThread.start();
        // Act - Start sending the packet
        discoveryClient.start();

        // Wait for the receiver thread to process the received packet
        receiverThread.join();
        discoveryClient.stop();
        discoveryClient.getThread().join();
        cors.stop();
    }
}