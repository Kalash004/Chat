package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.service_event_handling;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.exceptions.MessageDoesntExistRuntimeException;

public class Data {
    private final Message message = null;

    public Message getMessage() throws MessageDoesntExistRuntimeException {
        if (message == null) {
            throw new MessageDoesntExistRuntimeException();
        }
        return message;
    }
}
