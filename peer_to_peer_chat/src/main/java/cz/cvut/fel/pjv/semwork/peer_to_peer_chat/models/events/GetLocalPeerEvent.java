package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents an event to retrieve the local peer.
 * This event has no input and returns a {@link Peer} object as the output.
 */
public class GetLocalPeerEvent extends Event<Void, Peer> {
    public GetLocalPeerEvent() {
        this.receivers = new ArrayList<>(Arrays.asList(
                EventReceiver.PEER_MANAGER
        ));
        this.type = EventType.GET_LOCAL_PEER;
    }
}
