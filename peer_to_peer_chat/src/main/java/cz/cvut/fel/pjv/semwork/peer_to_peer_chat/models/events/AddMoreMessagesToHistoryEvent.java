package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventSender;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;

public class AddMoreMessagesToHistoryEvent extends Event<Pair<String, Message[]>, Void> {
    public AddMoreMessagesToHistoryEvent() {
        this.receivers = new ArrayList<>(Arrays.asList(
                EventReceiver.MESSAGE_SERVICE
        ));
        this.sender = EventSender.API;
        this.type = EventType.ADD_MORE_MESSAGES;
    }
}
