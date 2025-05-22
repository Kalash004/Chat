package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventReceiver;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.event.EventType;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.commands.CommandRefreshMessageHistory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Event used to request UI to refresh its public message history. Used when synchronizing message history from other users.
 */
public class RefreshMessageHistoryEvent extends Event<Void, CommandRefreshMessageHistory> {
    public RefreshMessageHistoryEvent() {
        this.receivers = new ArrayList<>(List.of(
                EventReceiver.UI
        ));
        this.type = EventType.REFRESH_MESSAGE_HISTORY;
    }
}
