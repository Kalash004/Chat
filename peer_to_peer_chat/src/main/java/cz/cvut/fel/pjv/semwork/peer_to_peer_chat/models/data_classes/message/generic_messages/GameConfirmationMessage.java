package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.generic_messages;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.MessageWrapper;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.message_content_classes.InviteConfirmationHolder;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.connection.MessageType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.message_status.MessageStatus;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.utils.json.JsonUtils;

import java.util.Date;

public class GameConfirmationMessage extends MessageWrapper {
    public GameConfirmationMessage(Boolean state, String peerId, String userName) {
        this.message = new Message();
        message.setPeerId(peerId);
        message.setType(MessageType.GAME_CONFIRMATION);
        message.setTimestamp(new Date());
        message.setContent(JsonUtils.toJson(new InviteConfirmationHolder(state)));
        message.setStatus(MessageStatus.OK);
        message.setSenderName(userName);
    }

}
