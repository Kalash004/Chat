package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;

import java.util.ArrayList;
import java.util.Arrays;

/**
* Represents an event indicating a connection request.
* This event is triggered when a peer requests to establish a connection with another peer or service.
 * It extends the generic {@link Event} class, with the input being a {@link Peer} and the output being a {@link Boolean} indicating
 * whether the connection request was successful or not.
*/
public class ConnectionRequestEvent extends Event<Peer, Boolean> {
    public ConnectionRequestEvent() {
        this.type = EventType.CONNECTION_REQUESTED;
        this.receivers = new ArrayList<>(Arrays.asList(
                EventReceiver.PEER_MANAGER,
                EventReceiver.API,
                EventReceiver.UI
        ));
    }

}
