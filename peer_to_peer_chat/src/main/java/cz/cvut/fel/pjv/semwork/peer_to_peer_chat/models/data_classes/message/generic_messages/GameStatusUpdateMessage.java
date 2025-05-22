package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.generic_messages;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.MessageWrapper;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.connection.MessageType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.message_status.MessageStatus;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.utils.json.JsonUtils;

import java.util.Date;

public class GameStatusUpdateMessage<T> extends MessageWrapper {
    public GameStatusUpdateMessage(String peerId, String userName, MessageType messageType, T content) {
        this.message = new Message();
        message.setPeerId(peerId);
        message.setType(messageType);
        message.setTimestamp(new Date());
        message.setContent(JsonUtils.toJson(content));
        message.setStatus(MessageStatus.OK);
        message.setSenderName(userName);
    }
}
