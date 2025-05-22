package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.message_content_classes;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.utils.json.JsonUtils;

public class PeerIdHolder {
    public String peerId;

    public PeerIdHolder(String peerId) {
        this.peerId = peerId;
    }

    public PeerIdHolder() {
    }

    public String toJson() {
        return JsonUtils.toJson(this);
    }
}
