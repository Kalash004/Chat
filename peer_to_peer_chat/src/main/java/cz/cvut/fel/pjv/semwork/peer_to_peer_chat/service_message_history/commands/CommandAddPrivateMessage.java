package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_message_history.commands;

import javafx.util.Pair;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.Event;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.ICommand;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_message_history.MessageServiceController;

public class CommandAddPrivateMessage implements ICommand {
    private final MessageServiceController messageServiceController;

    public CommandAddPrivateMessage(MessageServiceController controller) {
        this.messageServiceController = controller;
    }

    @Override
    public void execute(Event<?, ?> input) {
        Event<Pair<Peer, Pair<Peer, Message>>, ?> temp = (Event<Pair<Peer, Pair<Peer, Message>>, ?>) input;
        messageServiceController.addPrivateMessage(temp.getInput().getKey(), temp.getInput().getValue());
    }
}
