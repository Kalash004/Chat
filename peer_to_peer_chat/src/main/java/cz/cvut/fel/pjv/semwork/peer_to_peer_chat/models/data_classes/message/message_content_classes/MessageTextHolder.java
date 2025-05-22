package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.message_content_classes;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.utils.json.JsonUtils;

public class MessageTextHolder {
    public String text;

    public MessageTextHolder(String text) {
        this.text = text;
    }

    public MessageTextHolder() {
    }

    public String toJson() {
        return JsonUtils.toJson(this);
    }
}
