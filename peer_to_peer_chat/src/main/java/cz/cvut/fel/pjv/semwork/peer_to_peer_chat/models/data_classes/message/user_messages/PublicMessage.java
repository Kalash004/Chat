package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.user_messages;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.MessageWrapper;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.connection.MessageType;

import java.util.Date;
import java.util.UUID;

public class PublicMessage extends MessageWrapper {
    public PublicMessage(String peerId, String content, String senderName) {
        this.message = new Message();
        message.setPeerId(peerId);
        message.setType(MessageType.MESSAGE_PUBLIC);
        message.setTimestamp(new Date());
        message.setContent(content);
        message.setMessageId(UUID.randomUUID().toString());
        message.setSenderName(senderName);
    }

}
