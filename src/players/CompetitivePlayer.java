package players;

import game.Model;
import interfaces.IModel;
import interfaces.IPlayer;

/**
 * Implementing this player is an advanced task.
 * See assignment instructions for what to do.
 * If not attempting it, just upload the file as it is.
 *
 * @author Ray_Jiang <s2196231>
 */
public class CompetitivePlayer implements IPlayer
{  	// A reference to the model, which you can use to get information about
	// the state of the game. Do not use this model to make any moves!
	private IModel model;
    private byte playerId;
    private byte opponentId;
    private int nrRows;
    private int nrCols;
    private int minStreak;
	// The constructor is called when the player is selected from the game menu.
	public CompetitivePlayer()	{
		// You may (or may not) need to perform some initialisation here.
	}

	// This method is called when a new game is started or loaded.
	// You can use it to perform any setup that may be required before
	// the player is asked to make a move. The second argument tells
	// you if you are playing as player 1 or player 2.
	public void prepareForGameStart(IModel model, byte playerId)
	{
		this.model = model;
        this.playerId = playerId;
        this.opponentId = (byte) (3 - playerId);
        this.nrRows = model.getGameSettings().nrRows;
        this.nrCols = model.getGameSettings().nrCols;
        this.minStreak = model.getGameSettings().minStreakLength;
	}
	
	// This method is called to ask the player to take their turn.
	// The move they choose should be returned from this method.
	public int chooseMove()
	{
		return miniMax(model, 7, Integer.MIN_VALUE, Integer.MAX_VALUE, true)[0];
	}

    private int scoreWindow(byte[] window, byte playerId) {
        int score = 0;
        int numPlayer = 0;
        int numEmpty = 0;
        for (byte b : window) {
            if (b == playerId) {
                numPlayer++;
            } else if (b == 0) {
                numEmpty++;
            }
        }
        if (numPlayer == window.length) {
            score += 10000;
        }else if (numPlayer + numEmpty == window.length) {
            score += numPlayer^2;
        }else if (numPlayer == 0 && numEmpty == 1){
            score -= 9000;
        }
        return score;
    }

    private int scoreBoard(IModel model, byte playerId) {
        int score = 0;
//        score central
        for (int c = 0; c < nrCols; c++) {
            for (int r = 0; r < nrRows; r++) {
                if (model.getPieceIn(r,c) == playerId) {
                    score += c*(nrCols-1-c);
                }
            }
        }
//        score lower
        for (int r = 0; r < nrRows; r++) {
            for (int c = 0; c < nrCols; c++) {
                if (model.getPieceIn(r,c) == playerId) {
                    score += r;
                }
            }
        }
//        score rows
        for (int r = 0; r < nrRows; r++) {
            for (int c = 0; c < nrCols-(minStreak-1); c++) {
                byte[] window = new byte[minStreak];
                for (int i = 0; i < window.length; i++) {
                    window[i] = model.getPieceIn(r,c+i);
                }
                score += scoreWindow(window,playerId);
            }
        }
//        score cols
        for (int c = 0; c < nrCols; c++) {
            for (int r = 0; r < nrRows-(minStreak-1); r++) {
                byte[] window = new byte[minStreak];
                for (int i = 0; i < window.length; i++) {
                    window[i] = model.getPieceIn(r+i,c);
                }
                score += scoreWindow(window,playerId);
            }
        }
//       score pos45
        for (int r = 0; r < nrRows-(minStreak-1); r++) {
            for (int c = 0; c < nrCols - (minStreak-1); c++) {
                byte[] window = new byte[minStreak];
                for (int i = 0; i < window.length; i++) {
                    window[i] = model.getPieceIn(r+i,c+i);
                }
                score += scoreWindow(window,playerId);
            }
        }
//        score neg45
        for (int r = 0; r < nrRows-(minStreak-1); r++) {
            for (int c = 0; c < nrCols-(minStreak-1); c++) {
                byte[] window = new byte[minStreak];
                for (int i = 0; i < window.length; i++) {
                    window[i] = model.getPieceIn(r+(minStreak-1)-i,c+i);
                }
                score += scoreWindow(window,playerId);
            }
        }
        return score;
    }

    private int bestMove(IModel model, byte playerId){
        int bestScore = -100000;
        int bestCol = anyMove(model);
        for (int move = 0; move < nrCols; move++) {
            if (model.getPieceIn(0,move) == 0) {
                Model imModel = new Model(model);
                imModel.makeMove(move);
                int score = scoreBoard(imModel,playerId);
                if (score > bestScore){
                    bestScore = score;
                    bestCol = move;
                }
            }
        }
        return bestCol;
    }

    private int[] miniMax(IModel model, int depth, int alpha, int beta, boolean isMax) {
        int[] move_value = new int[2];
        byte gameStatus = model.getGameStatus();
        if (depth == 0 || gameStatus != IModel.GAME_STATUS_ONGOING) {
            if (depth == 0) {
                move_value[0] = bestMove(model, playerId);
                move_value[1] = scoreBoard(model, playerId);
            }else if (gameStatus == playerId) {
                move_value[1] = 100000000;
            }else if (gameStatus == opponentId) {
                move_value[1] = -10000000;
            }else {
                move_value[1] = 0;
            }
            return move_value;

        }else if (isMax) {
            move_value[1] = Integer.MIN_VALUE;
            move_value[0] = anyMove(model);
            for (int newMove = 0; newMove < nrCols; newMove++) {
                if (model.getPieceIn(0, newMove) == 0) {
                    Model imModel = new Model(model);
                    imModel.makeMove(newMove);
                    int newValue = miniMax(imModel, depth-1, alpha, beta, false)[1];
                    if (newValue > move_value[1]) {
                        move_value[1] = newValue;
                        move_value[0] = newMove;
                    }
                    alpha = Integer.max(alpha, move_value[1]);
                    if (alpha >= beta) {
                        break;
                    }
                }
            }
            return move_value;
        }
        else {
            move_value[1] = Integer.MAX_VALUE;
            move_value[0] = anyMove(model);
            for (int newMove = 0; newMove < nrCols; newMove++) {
                if (model.getPieceIn(0, newMove) == 0) {
                    Model imModel = new Model(model);
                    imModel.makeMove(newMove);
                    int newValue = miniMax(imModel, depth-1, alpha, beta, true)[1];
                    if (newValue < move_value[1]) {
                        move_value[1] = newValue;
                        move_value[0] = newMove;
                    }
                    beta = Integer.min(beta, move_value[1]);
                    if (alpha >= beta) {
                        break;
                    }
                }
            }
            return move_value;
        }
    }

    private int anyMove(IModel model) {
        for (int move = 0; move < nrCols; move++) {
            if (model.getPieceIn(0,move) == 0) {
                return move;
            }
        }
        return -1;
    }

}
