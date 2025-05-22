package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.exceptions;

public class MessageDoesntExistRuntimeException extends RuntimeException {
    public MessageDoesntExistRuntimeException() {
        super("Message doesn't exist");
    }

}
