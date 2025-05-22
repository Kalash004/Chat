package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetMessageHistoryEvent extends Event<Void, HashMap<String, ArrayList<Message>>> {
    public GetMessageHistoryEvent() {
        this.receivers = new ArrayList<>(List.of(EventReceiver.MESSAGE_SERVICE));
        this.type = EventType.GET_MESSAGE_HISTORY;
    }
}
