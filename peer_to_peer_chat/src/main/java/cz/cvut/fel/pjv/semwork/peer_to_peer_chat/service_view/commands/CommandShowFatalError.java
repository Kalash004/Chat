package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.commands;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.Event;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.ICommand;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.UiController;

public class CommandShowFatalError implements ICommand {
    private final UiController uiController;
    public CommandShowFatalError(UiController uiController) {
        this.uiController = uiController;
    }

    @Override
    public void execute(Event<?, ?> input) {
        Event<Exception, Void> temp = (Event<Exception, Void>) input;
        uiController.showFatalError(temp.getInput());
    }
}
