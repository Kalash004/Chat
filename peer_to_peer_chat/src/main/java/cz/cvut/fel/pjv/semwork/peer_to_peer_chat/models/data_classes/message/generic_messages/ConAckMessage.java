package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.generic_messages;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.MessageWrapper;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.message_content_classes.MessageHistoryAndPeersContent;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.connection.MessageType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.message_status.MessageStatus;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ConAckMessage extends MessageWrapper {
    public ConAckMessage(String peerId, HashMap<String, ArrayList<Message>> messageHistory, Peer[] connectedPeers) {
        message = new Message();
        message.setPeerId(peerId);
        message.setType(MessageType.CONNECTION_ACKNOWLEDGEMENT);
        message.setTimestamp(new Date());
        message.setStatus(MessageStatus.OK);
        message.setContent(new MessageHistoryAndPeersContent(messageHistory, connectedPeers).toJson());
    }
}
