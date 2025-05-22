package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.sort_utils;

import javafx.util.Pair;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;

import java.util.*;

public class PeerSorter {
    public static final ArrayList<String> SORT_OPTIONS = new ArrayList<>(List.of("Alphabetically", "Alphabetically (descending)", "Recently Chatted With"));

    public static Peer[] sortAlphabetically(Peer[] peers) {
        if(peers == null) {
            return null;
        }
        Arrays.sort(peers, Comparator.comparing(Peer::getUserName)); // sort alphabetically (ascending) by username
        return peers;
    }

    public static Peer[] sortAlphabeticallyDescending(Peer[] peers) {
        if(peers == null) {
            return null;
        }
        Arrays.sort(peers, Comparator.comparing(Peer::getUserName).reversed());
        return peers;
    }

    /**
     * Sorts peers based on the date of the most recent private message (most recent first).
     *
     * @param peerLatestMessageList list of (Peer, Date) pairs
     * @return sorted array of peers by recent activity or null if input is null
     */
    public static Peer[] sortRecentlyChattedWith(ArrayList<Pair<Peer, Date>> peerLatestMessageList) {
        if(peerLatestMessageList == null) {
            return null;
        }
        peerLatestMessageList.sort(Comparator.comparing(Pair::getValue, Comparator.nullsLast(Comparator.reverseOrder())));
        Peer[] sortedPeers = new Peer[peerLatestMessageList.size()];
        for(int i = 0; i < sortedPeers.length; i++) {
            sortedPeers[i] = peerLatestMessageList.get(i).getKey();
        }
        return sortedPeers;
    }

    /**
     * Moves a specific peer to the front of the peer list, preserving order of others.
     * Used in case Recently Chatted With sort option is chosen.
     *
     * @param peerList array of peers
     * @param thisPeer peer to be moved to the front
     * @return reordered array with thisPeer at index 0 or original array if not found
     */
    public static Peer[] movePeer(Peer[] peerList, Peer thisPeer) {
        int i = 0;
        if(peerList == null) {
            return null;
        }
        while(i < peerList.length && !thisPeer.getPeerId().equals(peerList[i].getPeerId())) {
            ++i;
        }
        while(i > 0) {
            Peer temp = peerList[i];
            peerList[i] = peerList[i - 1];
            peerList[i - 1] = temp;
            --i;
        }
        return peerList;
    }
}
