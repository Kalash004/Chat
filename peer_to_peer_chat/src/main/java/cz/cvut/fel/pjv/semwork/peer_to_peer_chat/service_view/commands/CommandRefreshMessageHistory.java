package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.commands;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.Event;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.ICommand;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.UiController;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

public class CommandRefreshMessageHistory implements ICommand {
    private final UiController uiController;

    public CommandRefreshMessageHistory(UiController uiController) {
        this.uiController = uiController;
    }

    @Override
    public void execute(Event<?, ?> input) {

        uiController.refreshPublicMessageHistory();
    }
}
