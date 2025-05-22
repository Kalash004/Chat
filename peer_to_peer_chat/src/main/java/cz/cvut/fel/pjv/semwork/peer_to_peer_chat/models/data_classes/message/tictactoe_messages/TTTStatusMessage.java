package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.tictactoe_messages;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.configs.ApiConfig;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.MessageWrapper;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.connection.MessageType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.tictactoe.TictactoeSpaceStatus;

import java.util.Date;

public class TTTStatusMessage extends MessageWrapper {
    public TTTStatusMessage(TictactoeSpaceStatus[][] board) {
            this.message = new Message();
            message.setPeerId(ApiConfig.getPeerId());
            message.setType(MessageType.MESSAGE_PRIVATE);
            message.setTimestamp(new Date());
            //message.setContent(content);
    }
}
