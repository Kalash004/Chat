package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an event to retrieve the peer currently connected to the system.
 * It extends the generic {@link Event} class, with the input being a {@link String} (peer ID)
 * and the output being a {@link Peer} object representing the connected peer.
 */

public class GetConnectedPeerEvent extends Event<String, Peer> {
    public GetConnectedPeerEvent() {
        this.receivers = new ArrayList<>(List.of(
                EventReceiver.PEER_MANAGER
        ));
        this.type = EventType.GET_PEER;
    }
}
