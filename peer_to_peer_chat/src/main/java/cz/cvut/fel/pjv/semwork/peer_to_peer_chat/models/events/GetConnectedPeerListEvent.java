package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an event to retrieve a list of currently connected peers.
 * This event has no input and returns an array of {@link Peer} objects as the output.
 */
public class GetConnectedPeerListEvent extends  Event<Void, Peer[]> {
    public GetConnectedPeerListEvent() {
        this.receivers = new ArrayList<>(List.of(EventReceiver.PEER_MANAGER));
        //this.sender = EventSender.UI;
        this.type = EventType.GET_PEER_LIST;
    }
}
