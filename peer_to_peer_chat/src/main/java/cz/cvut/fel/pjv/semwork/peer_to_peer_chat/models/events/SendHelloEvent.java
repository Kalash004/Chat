package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventSender;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;

import java.util.ArrayList;
import java.util.List;

/**
 * Event used to send a "hello" message during peer discovery.
 * The input is the username as a {@link String}.
 */
public class SendHelloEvent extends Event<String, Void> {
    public SendHelloEvent() {
        this.type = EventType.SEND_HELLO;
        this.receivers = new ArrayList<EventReceiver>(List.of(
                EventReceiver.API
        ));
        this.sender = EventSender.UI;
    }
}
