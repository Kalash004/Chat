package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.message_content_classes;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.tictactoe.TictactoeSpaceStatus;

public class TicTacToeGameStateHolder {
    public TictactoeSpaceStatus[][] spaceStatus;

    public TicTacToeGameStateHolder(TictactoeSpaceStatus[][] spaceStatus) {
        this.spaceStatus = spaceStatus;
    }

    public TicTacToeGameStateHolder() {
    }

}
