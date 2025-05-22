package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.tictactoe_service;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.tictactoe.TictactoeGameStatus;

import java.util.Map;

public class TictactoeStatusReports {
    public static final Map<TictactoeGameStatus, String> STATUS_REPORTS = Map.of(
            TictactoeGameStatus.WIN, "Status: You have won the game!",
            TictactoeGameStatus.LOSS, "Status: You have lost the game.",
            TictactoeGameStatus.DRAW, "Status: Game has ended in a draw!",
            TictactoeGameStatus.OPP_PLAYING, "Status: Wait for the opponent's move.",
            TictactoeGameStatus.PLAYING, "Status: It's your turn!"
    );
}
