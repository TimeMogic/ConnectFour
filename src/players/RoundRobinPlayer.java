package players;

import interfaces.IModel;
import interfaces.IPlayer;

/**
 * Implementing this player is an intermediate task.
 * See assignment instructions for what to do.
 * If not attempting it, just upload the file as it is.
 *
 * @author Ray_Jiang <s2196231>
 */
public class RoundRobinPlayer implements IPlayer
{
	// A reference to the model, which you can use to get information about
	// the state of the game. Do not use this model to make any moves!
	private IModel model;
	private int moveLastPlayed;
	// The constructor is called when the player is selected from the game menu.
	public RoundRobinPlayer()
	{
		// You can leave this empty.
	}
	
	// This method is called when a new game is started or loaded.
	// You can use it to perform any setup that may be required before
	// the player is asked to make a move. The second argument tells
	// you if you are playing as player 1 or player 2.
	public void prepareForGameStart(IModel model, byte playerId)
	{
		this.model = model;
		moveLastPlayed = -1;
		// Extend this method if required.
	}
	
	// This method is called to ask the player to take their turn.
	// The move they choose should be returned from this method.
	public int chooseMove()
	{
		// Until you have implemented this player, it will always concede.
        for (int col = moveLastPlayed + 1; col < this.model.getGameSettings().nrCols; col++) {
            if (this.model.getPieceIn(0, col) == 0){
                moveLastPlayed = col;
                return col;
            }
        }
        if (this.model.getPieceIn(0,0) == 0){
            moveLastPlayed = 0;
            return 0;
        }else return IModel.CONCEDE_MOVE;
	}
}
