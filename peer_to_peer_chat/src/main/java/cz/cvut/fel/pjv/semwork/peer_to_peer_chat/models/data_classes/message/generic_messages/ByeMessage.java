package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.generic_messages;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.MessageWrapper;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.connection.MessageType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.message_status.MessageStatus;

import java.util.Date;


public class ByeMessage extends MessageWrapper {
    public ByeMessage(String peerId) {
        message = new Message();
        message.setPeerId(peerId);
        message.setType(MessageType.BYE);
        message.setTimestamp(new Date());
        message.setStatus(MessageStatus.OK);
    }
}
