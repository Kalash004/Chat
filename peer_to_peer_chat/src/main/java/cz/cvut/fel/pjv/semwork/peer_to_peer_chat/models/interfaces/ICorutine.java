package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.interfaces;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.coroutine.CoroutineType;

public interface ICorutine {
    public CoroutineType getCoroutineType();
    /**
     *
     * @return True if still runs
     */
    public boolean resume();
}
