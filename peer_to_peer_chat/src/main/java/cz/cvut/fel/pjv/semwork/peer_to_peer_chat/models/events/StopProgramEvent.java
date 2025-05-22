package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;

import java.util.ArrayList;
import java.util.List;

/**
 * Event representing the end of the program.
 */
public class StopProgramEvent extends Event<Void, Void> {
    public StopProgramEvent() {
        this.type = EventType.PROGRAM_END;
        this.receivers = new ArrayList<>(List.of(
                EventReceiver.MESSAGE_SERVICE,
                EventReceiver.UI,
                EventReceiver.API,
                EventReceiver.PEER_MANAGER,
                EventReceiver.MIDDLE_WARE,
                EventReceiver.COROUTINE
        ));
    }
}
