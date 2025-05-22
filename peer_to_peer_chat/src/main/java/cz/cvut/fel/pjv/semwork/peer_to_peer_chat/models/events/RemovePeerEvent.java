package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;

import java.util.ArrayList;
import java.util.List;

/**
 * Event used to remove a peer from the peer maneger, UI (peer list) and message service. The input is the peer's ID as a {@link String}.
 */
public class RemovePeerEvent extends Event<String, Void> {
    public RemovePeerEvent() {
        this.receivers = new ArrayList<>(List.of(
                EventReceiver.PEER_MANAGER,
                EventReceiver.UI,
                EventReceiver.MESSAGE_SERVICE
        ));
        this.type = EventType.REMOVE_PEER;
    }
}
