package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.commands;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.Event;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.ICommand;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.ApiController;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;

public class CommandSendConReq implements ICommand {
    private final ApiController apiController;
    public CommandSendConReq(ApiController apiController) {
        this.apiController = apiController;
    }

    @Override
    public void execute(Event<?, ?> input) {
        apiController.sendConReq((Peer) input.getInput());
    }
}
