package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.tictactoe_service;

import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.tictactoe.TictactoeInvitationStatus;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.ui_link_utils.Page;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.ConfirmSendTTTInviteEvent;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.GetLocalPeerEvent;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.PrivateMessageSendEvent;
import javafx.application.Platform;
import javafx.util.Pair;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.tictactoe.TictactoeGameStatus;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.tictactoe.TictactoeSpaceStatus;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.events.TTTStatusSendEvent;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_connection_api.peer_manager.Peer;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_event_handler.EventHandler;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.UiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class TictactoeGameHandler {
    private static final Logger logger = LoggerFactory.getLogger(UiController.class);
    private TictactoeGame game;
    private static TictactoeGameHandler instance = null;
    private Peer opponent;
    private Peer tttPeer;
    private boolean hasFirstTurn;
    private ArrayList<Peer> pendingInvites;

    public static TictactoeGameHandler getInstance() {
        if (instance == null) {
            instance = new TictactoeGameHandler();
        }
        return instance;
    }

    private TictactoeGameHandler() {
        this.hasFirstTurn = true;
        this.tttPeer = null;
        this.pendingInvites = new ArrayList<Peer>();
    }

    /**
     * Sends a board update event to the opponent.
     *
     * @param board the current board state
     */
    private void sendBoardEvent(TictactoeSpaceStatus[][] board) {
        TTTStatusSendEvent tttStatusSendEvent = new TTTStatusSendEvent();
        tttStatusSendEvent.setInput(new Pair<Peer, TictactoeSpaceStatus[][]>(opponent, board));
        EventHandler.getInstance().handleEvent(tttStatusSendEvent);
    }

    /**
     * Initializes a new game session and updates the UI status.
     */
    public void launchGame() {
        game = new TictactoeGame(hasFirstTurn);
        this.opponent = tttPeer;
        logger.info("Opponent is " + tttPeer.getUserName());
        TictactoeGameStatus gameStatus = game.getGameStatus();
        Platform.runLater(() -> {
            UiController.getInstance().setGameStatus(gameStatus);
            UiController.getInstance().setGamePeer(opponent.getUserName());
        });
    }

    /**
     * Sends a move to the opponent and updates the local UI/game state.
     *
     * @param x row index
     * @param y column index
     */
    public void sendBoard(int x, int y) {
        Pair<TictactoeSpaceStatus[][], TictactoeGameStatus> boardStatusPair = game.processSendBoard(x, y);
        Platform.runLater(() -> {
            UiController.getInstance().setGameStatus(boardStatusPair.getValue());
        });
        sendBoardEvent(boardStatusPair.getKey());
    }

    /**
     * Receives and processes the opponent's move.
     *
     * @param x row index
     * @param y column index
     */
    private void processReceivedBoard(int x, int y) {
        game.receiveBoard(x, y);
        String buttonName = "btn" + x + y;
        TictactoeGameStatus gameStatus = game.getGameStatus();
        Platform.runLater(() -> {
            UiController.getInstance().setGameStatus(gameStatus);
            UiController.getInstance().setReceiveBoard(buttonName, hasFirstTurn);
        });
    }

    /**
     * Checks whether it's players turn (and whether the game has not ended at all yet).
     *
     * @return true if the game is ongoing, false otherwise
     */
    public boolean isStillPlaying() {
        if(game.getGameStatus() == TictactoeGameStatus.PLAYING) {
            return true;
        } else {
            return false;
        }
    }

    public void onReceivedTTTStatus(Pair<Peer, TictactoeSpaceStatus[][]> input) {
        TictactoeSpaceStatus[][] previousBoard = game.getBoard();
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                if(input.getValue()[i][j] != previousBoard[i][j]) {
                    processReceivedBoard(i, j);
                    logger.info("Board change on " + i + " " + j);
                    break;
                }
            }
        }
    }

    /**
     * Handles receiving response to Tic Tac Toe game invitation.
     *
     * @param peerBooleanPair - pair of previously invited peer and a boolean (true if invitation was accepted, false if rejected)
     */
    public void onConfirmedTTTInvite(Pair<Peer, Boolean> peerBooleanPair) {
        logger.info("Invite confirmation from " + peerBooleanPair.getKey().getUserName() + " received.");
        if(tttPeer != null && tttPeer.getPeerId().equals(peerBooleanPair.getKey().getPeerId()) && peerBooleanPair.getValue() == true) {
            pendingInvites.clear();
            Platform.runLater(() -> {
                UiController.getInstance().openPage(Page.TICTACTOE, null);
                hasFirstTurn = true;
            });
        } else if(peerBooleanPair.getValue() == false) {
            Platform.runLater(() -> {
                Peer[] peerList = UiController.getInstance().getPeerList();
                for(Peer peer : peerList) {
                    if(peer.getPeerId().equals(peerBooleanPair.getKey().getPeerId())) {
                        PrivateMessageSendEvent privateMessageSendEvent = new PrivateMessageSendEvent();
                        privateMessageSendEvent.setInput(new Pair<>(peer, TictactoeInvitationMessages.INVITATION_MESSAGES.get(TictactoeInvitationStatus.REJECTED)));
                        EventHandler.getInstance().handleEvent(privateMessageSendEvent);
                    }
                }
                tttPeer = null;
            });

        }
    }

    private void sendTTTInviteRejection(Peer thisPeer) {
        if(thisPeer == null) return;
        ConfirmSendTTTInviteEvent confirmTTTInviteEvent = new ConfirmSendTTTInviteEvent();
        confirmTTTInviteEvent.setInput(new Pair<Peer, Boolean>(thisPeer, false));
        EventHandler.getInstance().handleEvent(confirmTTTInviteEvent);
    }

    /**
     * Handles receiving a Tic Tac Toe game invitation.
     *
     * @param thisPeer - the peer who sent the invitation
     */
    public void onReceivedTTTInvite(Peer thisPeer) {
        logger.info("Invite from " + thisPeer.getUserName() + " received.");
        if(tttPeer == null) {
            pendingInvites.add(thisPeer);
        } else if(!pendingInvites.contains(thisPeer)) {
            sendTTTInviteRejection(thisPeer);
        }
    }
    
    public String getTttPeerID() {
        return (tttPeer == null ? null : tttPeer.getPeerId());
    }

    public void setHasFirstTurn(boolean hasFirstTurn) {
        this.hasFirstTurn = hasFirstTurn;
    }

    public ArrayList<Peer> getPendingInvites() {
        return pendingInvites;
    }

    /**
     * Checks if the currently selected peer has a pending game invitation.
     *
     * @return true if there is a pending invite, false otherwise
     */
    public boolean checkPendingPeer(Peer currentPeer) {
        if(pendingInvites == null) return false;
        return (pendingInvites.contains(currentPeer));
    }

    public boolean getHasFirstTurn() {
        return hasFirstTurn;
    }

    public void setTTTPeer(Peer currentPeer) {
        tttPeer = currentPeer;
    }

    /**
     * Reset the active game invitation if there is any.
     */
    public void resetTTTPeer() {
        if(tttPeer == null) return;
        sendTTTInviteRejection(tttPeer);
        tttPeer = null;
    }

    public void removePeerFromPending(Peer peer) {
        if(peer == null) {
            peer = opponent;
        }
        pendingInvites.remove(peer);
    }
}
