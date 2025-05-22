package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.commands;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.ConnectionRequestEvent;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.Event;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.ICommand;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.ApiController;

public class CommandSendConAck implements ICommand {
    private final ApiController apiController;
    public CommandSendConAck(ApiController apiController) {
        this.apiController = apiController;
    }

    @Override
    public void execute(Event<?, ?> input) {
        if (!(input instanceof ConnectionRequestEvent connectionRequestEvent)) {
            return;
        }
        Boolean res = apiController.sendConAck(connectionRequestEvent.getInput());
        connectionRequestEvent.setOutput(res);
    }
}
