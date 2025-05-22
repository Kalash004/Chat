package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.sort_utils;

import javafx.util.Pair;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.data_classes.message.Message;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class MessageHistorySorter {
    /**
     * Transformps message history map to a 1D array and sorts it by timestamp.
     *
     * @param messageHistory a map where each peer maps to a list of their messages
     * @return a list of (Peer, Message) pairs sorted by message timestamp
     */
    public static ArrayList<Pair<String, Message>> getSortedMessageHistory(HashMap<String, ArrayList<Message>> messageHistory) {
        ArrayList<Pair<String, Message>> sortedMessageHistory = new ArrayList<>();
        for (HashMap.Entry<String, ArrayList<Message>> peerMessageEntry : messageHistory.entrySet()) {
            for (Message message : peerMessageEntry.getValue()) {
                sortedMessageHistory.add(new Pair(peerMessageEntry.getKey(), message));
            }
        }
        sortedMessageHistory.sort(Comparator.comparing(pair -> pair.getValue().getTimestamp()));
        return sortedMessageHistory;
    }
}
