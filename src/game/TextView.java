package game;

import interfaces.*;
import players.*;
import util.GameSettings;
import util.InputUtil;

/**
 * This class is used to interact with the user.
 * It has been partially implemented, but needs to be completed by you.
 *
 * @author Ray_Jiang <s2196231>
 */
public class TextView implements IView
{
	public void displayWelcomeMessage()
	{
		System.out.println("Welcome to Connect Four!");
	}
	
	public void displayChosenMove(int move)
	{
		System.out.println("Selected move: " + move);
	}
	
	public void displayMoveRejectedMessage(int move)
	{
		System.out.println("The move (" + move + ") was rejected, please try again.");
	}
	
	public void displayActivePlayer(byte playerID)
	{
		System.out.println("\nPlayer " + playerID + " is next!");
	}
	
	public void displayGameStatus(byte gameStatus)
	{
		System.out.print("\nGame status: ");
		
		switch(gameStatus)
		{
			case IModel.GAME_STATUS_ONGOING: System.out.println("The game is in progress."); break;
			case IModel.GAME_STATUS_WIN_1: System.out.println("Player 1 has won!"); break;
			case IModel.GAME_STATUS_WIN_2: System.out.println("Player 2 has won!"); break;
			case IModel.GAME_STATUS_TIE: System.out.println("The game has ended in a tie!"); break;
			default : System.out.println("Error: Invalid/unknown game status"); break;
		}
	}
	
	public void displayBoard(IModel model)
	{
		System.out.println("\n-------- BOARD --------");
		
		int nrRows = model.getGameSettings().nrRows;
		int nrCols = model.getGameSettings().nrCols;

		// Remove this and replace it with an actual representation of the board.
		//System.out.println("The board has " + nrRows + " rows and " + nrCols + " columns.");
        for (int i = 0; i < nrRows; i++) {
            for (int j = 0; j < nrCols; j++) {
                if (model.getPieceIn(i, j) == 1){
                    System.out.print('X');
                }else if(model.getPieceIn(i, j) == 2){
                    System.out.print('O');
                }else{
                    System.out.print('_');
                }
                if (j < nrCols - 1){
                    System.out.print(' ');
                }
            }
            System.out.println();
        }
		// Here is an example of how the output should look:
		//_ _ O O _ _ X
		//_ _ X O _ _ X
		//_ O X X _ _ O
		//_ X X O _ X O
		//X O O X O O O
		//X O X X X O X
	}

	public char requestMenuSelection()
	{
		// Display menu options.
		System.out.println("\n-------- MENU --------");
		System.out.println("(1) Start new game");
		System.out.println("(2) Resume saved game");
		System.out.println("(3) Change game settings");
		System.out.println("(4) Change players");
		
		// Request and return user input.
		System.out.print("Select an option and confirm with enter or use any other key to quit: ");
		return InputUtil.readCharFromUser();
	}
	
	public String requestSaveFileName()
	{
		System.out.println("\n-------- LOAD GAME --------");
		System.out.print("File name (e.g. Save.txt): ");
		return InputUtil.readStringFromUser();
	}
	
	public GameSettings requestGameSettings()
	{
		System.out.println("\n-------- GAME SETTINGS --------");
        //Checking and setting #rows.
		boolean rowsRejected;
		int tempRows;
		System.out.println("Setting the number of Rows on the board: ");
        do {
            tempRows = InputUtil.readIntFromUser();
            rowsRejected = !(tempRows >= IModel.MIN_ROWS && tempRows <= IModel.MAX_ROWS);
            if (rowsRejected){
                System.out.println("The number of rows (" + tempRows + ") was rejected, please try again.");
            }
        }while (rowsRejected);
		int nrRows = tempRows;
        //Checking and setting #columns.
        boolean colsRejected;
		int tempCols;
        System.out.println("Setting the number of Columns on the board: ");
        do {
            tempCols = InputUtil.readIntFromUser();
            colsRejected = !(tempCols >= IModel.MIN_COLS && tempCols <= IModel.MAX_COLS);
            if (colsRejected){
                System.out.println("The number of columns (" + tempCols + ") was rejected, please try again.");
            }
        }while (colsRejected);
		int nrCols = tempCols;
        //Checking and setting #streaks.
        boolean streakRejected;
        int tempStreak;
        int MAX_STREAK;
        if (tempRows < tempCols) {MAX_STREAK = tempRows;} else {MAX_STREAK = tempCols;}
        System.out.println("Setting the number of streaks to win: ");
        do {
            tempStreak = InputUtil.readIntFromUser();
            streakRejected = !(tempStreak > 0 && tempStreak <= MAX_STREAK);
            if (streakRejected){
                System.out.println("The number of streaks to win (" + tempStreak + ") was rejected, please try again.");
            }
        }while (streakRejected);
        int streak = tempStreak;

        // Wrap the selected settings in a GameSettings instance and return (leave this code here).
		return new GameSettings(nrRows, nrCols, streak);
	}
	
	public IPlayer requestPlayerSelection(byte playerId)
	{
		System.out.println("\n-------- CHOOSE PLAYER " + playerId + " --------");
		System.out.println("(1) HumanPlayer");
		System.out.println("(2) RoundRobinPlayer");
		System.out.println("(3) WinDetectingPlayer");
		System.out.println("(4) CompetitivePlayer");
		
		// Request user input.
		System.out.print("Select an option and confirm with enter (invalid input will select a HumanPlayer): ");
		char selectedPlayer = InputUtil.readCharFromUser();
		
		// Instantiate the selected player class.
		switch(selectedPlayer)
		{
			case '2': return new RoundRobinPlayer();
			case '3': return new WinDetectingPlayer();
			case '4': return new CompetitivePlayer();
			default: return new HumanPlayer();
		}
	}
}
