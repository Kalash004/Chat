package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventSender;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Event representing the start of the program.
 */
public class StartEvent extends Event<Void,Void> {
    public StartEvent() {
        this.type = EventType.PROGRAM_START;
        this.receivers = new ArrayList<EventReceiver>(Arrays.asList(
                EventReceiver.API,
                EventReceiver.COROUTINE
        ));
        this.sender = EventSender.PROGRAM;
    }
}
