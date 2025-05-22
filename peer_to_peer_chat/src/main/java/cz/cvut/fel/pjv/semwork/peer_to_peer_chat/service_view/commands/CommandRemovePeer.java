package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.commands;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.Event;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.ICommand;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.UiController;

public class CommandRemovePeer implements ICommand {
    private final UiController uiController;
    public CommandRemovePeer(UiController uiController) {
        this.uiController = uiController;
    }

    @Override
    public void execute(Event<?, ?> input) {
        uiController.updatePeer();
    }
}
