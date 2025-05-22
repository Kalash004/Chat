package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an event to retrieve the local peer's ID.
 * This event has no input and returns a {@link String} representing the peer's ID as the output.
 */
public class GetLocalPeerIDEvent extends Event<Void, String> {
    public GetLocalPeerIDEvent() {
        this.receivers = new ArrayList<>(List.of(EventReceiver.PEER_MANAGER));
        //this.sender = EventSender.UI;
        this.type = EventType.GET_LOCAL_PEER_ID;
    }

}
