package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.ICommand;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an event for sending a Tic-Tac-Toe invite confirmation/rejection. The event's input is a {@link Pair}
 * containing a {@link Peer} and a {@link Boolean} indicating whether the invite is accepted or not.
 */
public class ConfirmSendTTTInviteEvent extends Event<Pair<Peer, Boolean>, Void> {
    public ConfirmSendTTTInviteEvent() {
        this.receivers = new ArrayList<>(List.of(
                EventReceiver.API
        ));
        this.type = EventType.CONFIRM_SEND_TTT_INVITE;
    }

}
