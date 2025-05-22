package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.Event;

public interface ICommand {
    void execute(Event<?, ?> input);
}
