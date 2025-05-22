package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.message_content_classes;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.utils.json.JsonUtils;

public class PeerAndMessageTextHolder {
    @JsonUnwrapped
    public PeerIdHolder peerIdHolder;

    @JsonUnwrapped
    public MessageTextHolder messageTextHolder;

    public PeerAndMessageTextHolder(String peerId, String messageText) {
        this.peerIdHolder = new PeerIdHolder(peerId);
        this.messageTextHolder = new MessageTextHolder(messageText);
    }

    public PeerAndMessageTextHolder() {
    }

    public String toJson() {
        return JsonUtils.toJson(this);
    }
}
