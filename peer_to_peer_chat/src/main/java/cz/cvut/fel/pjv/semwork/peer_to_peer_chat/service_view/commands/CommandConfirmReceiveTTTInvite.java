package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.commands;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.Event;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.ICommand;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.tictactoe_service.TictactoeGameHandler;
import javafx.util.Pair;

import java.util.logging.Logger;

public class CommandConfirmReceiveTTTInvite implements ICommand {
    private static final Logger logger = Logger.getLogger(CommandConfirmReceiveTTTInvite.class.getName());
    private final TictactoeGameHandler gameHandler;

    public CommandConfirmReceiveTTTInvite(TictactoeGameHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    @Override
    public void execute(Event<?, ?> input) {
        gameHandler.onConfirmedTTTInvite((Pair<Peer, Boolean>) input.getInput());
    }
}
