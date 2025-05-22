package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.generic_messages;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.MessageWrapper;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.connection.MessageType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.message_status.MessageStatus;

public class ResponseMessage extends MessageWrapper {

    public ResponseMessage(String peerId, String userName, MessageStatus messageStatus) {
        this.message = new Message();
        this.message.setPeerId(peerId);
        this.message.setType(MessageType.RESPONSE);
        this.message.setStatus(messageStatus);
        this.message.setSenderName(userName);
    }
}
