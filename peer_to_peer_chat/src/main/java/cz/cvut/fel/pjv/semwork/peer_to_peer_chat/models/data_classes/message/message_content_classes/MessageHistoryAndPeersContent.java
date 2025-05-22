package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.message_content_classes;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.utils.json.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageHistoryAndPeersContent {
    public HashMap<String, ArrayList<Message>> messages;
    public Peer[] peers;

    public MessageHistoryAndPeersContent(HashMap<String, ArrayList<Message>> messages, Peer[] peers) {
        this.messages = messages;
        this.peers = peers;
    }

    public MessageHistoryAndPeersContent() {
    }

    public String toJson() {
        return JsonUtils.toJson(this);
    }
}
