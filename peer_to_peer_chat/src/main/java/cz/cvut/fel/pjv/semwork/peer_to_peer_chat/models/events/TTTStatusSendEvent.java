package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events;

import javafx.util.Pair;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.tictactoe.TictactoeSpaceStatus;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Event used to send the current Tic-Tac-Toe board status from one peer to another.
 * Carries a pair of peer and 2D board state as input, with no output.
 */

public class TTTStatusSendEvent extends Event<Pair<Peer, TictactoeSpaceStatus[][]>, Void> {
    public TTTStatusSendEvent() {
        this.receivers = new ArrayList<>(Arrays.asList(
                EventReceiver.MESSAGE_SERVICE,
                EventReceiver.API
        ));
        this.type = EventType.TTT_STATUS_SEND;
    }
}
