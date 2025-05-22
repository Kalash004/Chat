package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.tictactoe.TictactoeSpaceStatus;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;

public class ReceiveTTTStatusEvent extends Event<Pair<Peer, TictactoeSpaceStatus[][]>, Void> {
    public ReceiveTTTStatusEvent() {
        this.receivers = new ArrayList<>(Arrays.asList(
                EventReceiver.UI
        ));
        this.type = EventType.RECEIVE_TTT_STATUS;
    }
}
