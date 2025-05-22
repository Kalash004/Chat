package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.user_messages;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.MessageWrapper;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.connection.MessageType;

import java.util.Date;

public class PrivateMessage extends MessageWrapper {

    public PrivateMessage(String peerId,String content, String userName) {
        this.message = new Message();
        message.setPeerId(peerId);
        message.setType(MessageType.MESSAGE_PRIVATE);
        message.setTimestamp(new Date());
        message.setContent(content);
        message.setSenderName(userName);
    }

}
