package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.message_content_classes;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.tictactoe.TictactoeInvitationStatus;

public class GameInviteHolder {
    String gameId;
    TictactoeInvitationStatus invitationStatus;
    public GameInviteHolder(String gameId) {
        this.gameId = gameId;
    }

    public GameInviteHolder() {
    }
}
