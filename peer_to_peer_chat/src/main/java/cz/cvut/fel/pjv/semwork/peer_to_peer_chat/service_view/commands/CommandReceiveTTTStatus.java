package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.commands;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.tictactoe.TictactoeSpaceStatus;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.Event;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.ICommand;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.tictactoe_service.TictactoeGameHandler;
import javafx.util.Pair;

public class CommandReceiveTTTStatus implements ICommand {
    private final TictactoeGameHandler gameHandler;
    public CommandReceiveTTTStatus(TictactoeGameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    @Override
    public void execute(Event<?, ?> input) {
        gameHandler.onReceivedTTTStatus((Pair<Peer, TictactoeSpaceStatus[][]>) input.getInput());
    }
}
