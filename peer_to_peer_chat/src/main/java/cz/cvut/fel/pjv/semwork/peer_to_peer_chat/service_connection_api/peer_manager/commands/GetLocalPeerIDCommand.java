package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.commands;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.Event;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces.ICommand;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.PeerManager;

public class GetLocalPeerIDCommand implements ICommand {
    private final PeerManager peerManager;

    public GetLocalPeerIDCommand(PeerManager peerManager) {
        this.peerManager = peerManager;
    }

    @Override
    public void execute(Event<?, ?> input) {
        Event<Peer, String> event = (Event<Peer, String>) input;
        event.setOutput(peerManager.getLocalPeerId());
    }
}
