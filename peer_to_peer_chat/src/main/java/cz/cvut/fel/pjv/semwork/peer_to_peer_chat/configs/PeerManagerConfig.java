package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.configs;

public class PeerManagerConfig {
    private static final int peerExpirationTimeMs = 10000; // 10s

    public static int getPeerExpirationTimeMs() {
        return peerExpirationTimeMs;
    }
}
