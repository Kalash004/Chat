package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;

import java.util.ArrayList;
import java.util.List;

public class ShowFatalErrorEvent extends Event<Exception, Void> {
    public ShowFatalErrorEvent() {
        this.receivers = new ArrayList<>(List.of(EventReceiver.UI));
        this.type = EventType.SHOW_FATAL_ERROR;
    }
}
