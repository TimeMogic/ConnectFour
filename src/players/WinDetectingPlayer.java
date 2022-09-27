package players;

import game.Model;
import interfaces.IModel;
import interfaces.IPlayer;
import java.util.ArrayList;

/**
 * Implementing this player is an advanced task.
 * See assignment instructions for what to do.
 * If not attempting it, just upload the file as it is.
 *
 * @author Ray_Jiang <s2196231>
 */
public class WinDetectingPlayer implements IPlayer
{
	// A reference to the model, which you can use to get information about
	// the state of the game. Do not use this model to make any moves!
	private IModel model;
    private Model imModel;
	// The constructor is called when the player is selected from the game menu.
	public WinDetectingPlayer()
	{
		// You may (or may not) need to perform some initialisation here.
	}
	
	// This method is called when a new game is started or loaded.
	// You can use it to perform any setup that may be required before
	// the player is asked to make a move. The second argument tells
	// you if you are playing as player 1 or player 2.
	public void prepareForGameStart(IModel model, byte playerId)
	{
		this.model = model;
	}
	
	// This method is called to ask the player to take their turn.
	// The move they choose should be returned from this method.
	public int chooseMove(){
        int nrCols = model.getGameSettings().nrCols;
        byte iWin = model.getActivePlayer();
        byte opWin = (byte) (3 - iWin);
        ArrayList<Integer> opWins = new ArrayList<>();

        NextMove:
        for (int move = 0; move < nrCols; move++) {

            imModel = new Model(model);
            imModel.makeMove(move);
            if (imModel.getGameStatus() == iWin){
                return move;
            }else {
                for (int opMove = 0; opMove < nrCols; opMove++) {
                    imModel = new Model(model);
                    imModel.makeMove(move);
                    if (!imModel.isMoveValid(opMove)){
                        continue;
                    }
                    imModel.makeMove(opMove);
                    if (imModel.getGameStatus() == opWin) {
                        continue NextMove;
                    }
                }
                if (model.isMoveValid(move)){
                    return move;
                }
            }
        }
        return IModel.CONCEDE_MOVE;
    }
}
