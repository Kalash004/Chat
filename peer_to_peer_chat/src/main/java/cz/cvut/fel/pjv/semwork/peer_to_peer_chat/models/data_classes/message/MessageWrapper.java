package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message;

public abstract class MessageWrapper {
    protected Message message;
    public Message getMessage() {
        return message;
    }
}

