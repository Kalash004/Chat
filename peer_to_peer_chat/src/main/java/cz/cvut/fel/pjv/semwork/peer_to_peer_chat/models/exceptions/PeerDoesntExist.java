package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.exceptions;

public class PeerDoesntExist extends RuntimeException {
    public PeerDoesntExist(String message) {
      super("Peer is not registered in PeerManager " + message);
    }
}
