package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.generic_messages;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.MessageWrapper;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.message_content_classes.PortHolderContent;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.connection.MessageType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.message_status.MessageStatus;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.utils.json.JsonUtils;

import java.util.Date;

public class DiscoveryHelloMessage extends MessageWrapper {
    public DiscoveryHelloMessage(String peerId, int messagePort, String userName) {
        this.message = new Message();
        message.setPeerId(peerId);
        message.setType(MessageType.HELLO);
        message.setTimestamp(new Date());
        message.setContent(JsonUtils.toJson(new PortHolderContent(messagePort)));
        message.setStatus(MessageStatus.OK);
        message.setSenderName(userName);
    }
}
