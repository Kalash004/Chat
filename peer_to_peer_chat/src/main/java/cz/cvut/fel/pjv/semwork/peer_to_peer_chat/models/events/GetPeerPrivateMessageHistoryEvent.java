package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events;

import javafx.util.Pair;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an event to retrieve the private message history for a specific peer.
 * The event's input is a {@link Peer}, and it returns an {@link ArrayList} of {@link Pair} objects,
 * where each pair contains a {@link Peer} and a corresponding {@link Message}.
 */

public class GetPeerPrivateMessageHistoryEvent extends Event<Peer, ArrayList<Pair<Peer, Message>>> {
    public GetPeerPrivateMessageHistoryEvent() {
        this.receivers = new ArrayList<>(List.of(
                EventReceiver.MESSAGE_SERVICE
        ));
        this.type = EventType.GET_PRIVATE_MESSAGE_HISTORY;
    }
}
