package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.tictactoe_service;

import javafx.util.Pair;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.tictactoe.TictactoeGameStatus;
import cz.cvut.fel.pjv.semwork.peer_to_peer_chat.models.enums.tictactoe.TictactoeSpaceStatus;

import java.util.Arrays;

public class TictactoeGame {
    private final TictactoeSpaceStatus[][] board;
    private final TictactoeSpaceStatus mySign;
    private final TictactoeSpaceStatus oppSign;
    private TictactoeGameStatus gameStatus;
    public TictactoeGame(boolean hasFirstTurn) {
        this.board = new TictactoeSpaceStatus[3][3];
        for(int i = 0; i < 3; i++) {
            Arrays.fill(this.board[i], TictactoeSpaceStatus.EMPTY);
        }

        this.mySign = hasFirstTurn ? TictactoeSpaceStatus.X : TictactoeSpaceStatus.O;
        this.oppSign = hasFirstTurn ? TictactoeSpaceStatus.O : TictactoeSpaceStatus.X;
        this.gameStatus = hasFirstTurn ? TictactoeGameStatus.PLAYING : TictactoeGameStatus.OPP_PLAYING;
    }

    public TictactoeGameStatus getGameStatus() {
        return gameStatus;
    }

    public TictactoeSpaceStatus[][] getBoard() {
        return board;
    }

    private boolean diagonalThree(int x, int y, TictactoeSpaceStatus sign) {
        // check if [x, y] are on the main diagonal
        if(x == y && board[(x + 1) % 3][(y + 1) % 3] == sign && board[(x + 2) % 3][(y + 2) % 3] == sign) return true;
        //check if [x, y] are on the antidiagonal
        if (x + y == 2 && board[(x - 1 + 3) % 3][(y + 1) % 3] == sign && board[(x - 2 + 3) % 3][(y + 2) % 3] == sign) return true;

        return false;
    }

    private boolean horizontalThree(int x, int y, TictactoeSpaceStatus sign) {
        return board[x][(y + 1) % 3] == sign && board[x][(y + 2) % 3] == sign;
    }

    private boolean verticalThree(int x, int y, TictactoeSpaceStatus sign) {
        return board[(x + 1) % 3][y] == sign && board[(x + 2) % 3][y] == sign;
    }

    private void checkWin(int x, int y) {
        if(diagonalThree(x, y, mySign) || horizontalThree(x, y, mySign) || verticalThree(x, y, mySign)) {
            this.gameStatus = TictactoeGameStatus.WIN;
        }
    }

    private void checkLoss(int x, int y) {
        if(diagonalThree(x, y, oppSign) || horizontalThree(x, y, oppSign) || verticalThree(x, y, oppSign)) {
            this.gameStatus = TictactoeGameStatus.LOSS;
        }
    }

    private void checkDraw(int x, int y) {
        boolean hasEmptySpace = false;
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(board[i][j] == TictactoeSpaceStatus.EMPTY) {
                    hasEmptySpace = true;
                    break;
                }
            }
        }
        if(!hasEmptySpace) {
            this.gameStatus = TictactoeGameStatus.DRAW;
        }
    }


    /**
     * Processes a local move and updates the game state.
     *
     * @param x the row index of the move
     * @param y the column index of the move
     * @return a pair of the current board and updated game status
     */
    public Pair<TictactoeSpaceStatus[][], TictactoeGameStatus> processSendBoard(int x, int y) {
        //accept player's changes from the UI
        board[x][y] = mySign;
        //check if win or draw status can be set
        checkWin(x, y);
        if(gameStatus == TictactoeGameStatus.PLAYING) {
            checkDraw(x, y);
        }
        if(gameStatus == TictactoeGameStatus.PLAYING) {
            //now, player has to wait until the other player makes his move
            gameStatus = TictactoeGameStatus.OPP_PLAYING;
        }
        return new Pair<>(board, gameStatus);
    }

    /**
     * Updates the game state after receiving the opponent's move.
     *
     * @param x the row index of the move
     * @param y the column index of the move
     */
    public void receiveBoard(int x, int y) {
        //set status to playing
        gameStatus = TictactoeGameStatus.PLAYING;
        board[x][y] = oppSign;
        // check if loss or draw status can be set
        checkLoss(x, y);
        if(gameStatus == TictactoeGameStatus.PLAYING) {
            checkDraw(x, y);
        }
    }
}
