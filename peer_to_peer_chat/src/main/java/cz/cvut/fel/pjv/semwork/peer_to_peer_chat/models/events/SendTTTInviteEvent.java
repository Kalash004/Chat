package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;

import java.util.ArrayList;
import java.util.List;

/**
 * Event for sending a Tic-Tac-Toe invite to a peer.
 * Takes a {@link Peer} as input and returns no output.
 */
public class SendTTTInviteEvent extends Event<Peer, Void> {
    public SendTTTInviteEvent() {
        this.receivers = new ArrayList<>(List.of(
            EventReceiver.API
        ));
        this.type = EventType.SEND_TTT_INVITE;
    }
}
