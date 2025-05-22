package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.commands;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.AddMessageToHistoryEvent;
import javafx.util.Pair;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.Event;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.ICommand;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.UiController;

public class CommandAddMessage  implements ICommand {
    private final UiController uiController;
    public CommandAddMessage(UiController uiController) {
        this.uiController = uiController;
    }

    @Override
    public void execute(Event<?, ?> input) {
        if (!(input instanceof AddMessageToHistoryEvent temp)) return;
        uiController.addMessage(temp.getInput().getValue());
    }
}
