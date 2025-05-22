package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.tictactoe_service;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.tictactoe.TictactoeInvitationStatus;

import java.util.Map;

public class TictactoeInvitationMessages {
    public static final Map<TictactoeInvitationStatus, String> INVITATION_MESSAGES = Map.of(
            TictactoeInvitationStatus.SENT, "AUTOMATICALLY GENERATED:\nThis player has invited you to play tictactoe.\n Click on PLAY TICTACTOE button to start the game!",
            TictactoeInvitationStatus.REJECTED, "AUTOMATICALLY GENERATED:\nOne of the players is currently unavailable. Invitation is reset.\n Invite other players!",
            TictactoeInvitationStatus.RESET, "AUTOMATICALLY GENERATED:\nThe invited player reset their last invitation to a tictactoe game.\n"
    );
}
