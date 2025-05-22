package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.connection;

public enum MessageType {
    HELLO,
    CONNECTION_ACKNOWLEDGEMENT,
    CONNECTION_REQUEST,
    SYNCHRONIZATION,
    MESSAGE_PUBLIC,
    RESPONSE, BYE, ALIVE_CHECK, TIC_TAC_TOE_INVITE, TIC_TAC_TOE_STATUS, GAME_CONFIRMATION, MESSAGE_PRIVATE
}
