package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;

import java.util.ArrayList;
import java.util.List;

/**
 * Event representing a request to send a connection request to a peer.
 * The input is the {@link Peer} to connect with.
 */
public class SendConReqEvent extends Event<Peer,Void> {
    public SendConReqEvent() {
        this.receivers = new ArrayList<>(List.of(EventReceiver.API));
        this.type = EventType.SEND_CONREQ;
    }
}
