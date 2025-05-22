package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_message_history.commands;

import javafx.util.Pair;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.Event;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.ICommand;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_message_history.MessageServiceController;

public class CommandAddMessage implements ICommand {
    private final MessageServiceController messageServiceController;
    public CommandAddMessage(MessageServiceController controller) {
        this.messageServiceController = controller;
    }

    @Override
    public void execute(Event<?, ?> input) {

        Event<Pair<String, Message>, ?> temp = (Event<Pair<String, Message>, ?>)input;
        messageServiceController.addMessage(temp.getInput().getKey(), temp.getInput().getValue());
    }
}
