package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events;

import javafx.util.Pair;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an event for receiving a Tic-Tac-Toe invitation in UI. The event's input is a {@link Peer}
 * (sender of the invitation).
 */

public class RecieveTTTInviteEvent extends Event<Peer, Void> {
    public RecieveTTTInviteEvent() {
        this.receivers = new ArrayList<>(List.of(
                EventReceiver.UI
        ));
        this.type = EventType.RECEIVE_TTT_INVITE;
    }
}
