package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventSender;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;

import java.util.ArrayList;
import java.util.List;

public class SendPublicMessageEvent extends Event<String, Void> {

    public SendPublicMessageEvent() {
        this.receivers = new ArrayList<>(List.of(
                EventReceiver.API
        ));
        this.sender = EventSender.UI;
        this.type = EventType.SEND_PUBLIC_MESSAGE;
    }

}
