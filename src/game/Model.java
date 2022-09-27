package game;

import interfaces.IModel;
import util.GameSettings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * This class represents the state of the game.
 * It has been partially implemented, but needs to be completed by you.
 *
 * @author Ray_Jiang <s2196231>
 */
public class Model implements IModel {
    // A reference to the game settings from which you can retrieve the number
    // of rows and columns the board has and how long the win streak is.
    private GameSettings settings;
    private byte[][] board;
    private byte activePlayer;
    private byte gameStatus;

        // The default constructor.
    public Model() {
        // You probably won't need this.
    }

    // A constructor that takes another instance of the same type as its parameter.
    // This is called a copy constructor.
    public Model(IModel model) {
        // You may (or may not) find this useful for advanced tasks.
        this.settings = model.getGameSettings();
        this.activePlayer = model.getActivePlayer();
        this.gameStatus = model.getGameStatus();
        this.board = new byte[settings.nrRows][settings.nrCols];
        for (int row = 0; row < settings.nrRows; row++) {
            for (int col = 0; col < settings.nrCols; col++) {
                this.board[row][col] = model.getPieceIn(row,col);
            }
        }
    }

    // Called when a new game is started on an empty board.
    public void initNewGame(GameSettings settings) {
        this.settings = settings;
        this.board = new byte[settings.nrRows][settings.nrCols];
        this.activePlayer = 1;
        this.gameStatus = IModel.GAME_STATUS_ONGOING;
    }

    // Called when a game state should be loaded from the given file.
    public void initSavedGame(String fileName) {
        try {
            File svFile = new File("saves", fileName);
            List<String> svContent = Files.readAllLines(svFile.toPath());

            int svRows = Integer.parseInt(svContent.get(0));
            int svCols = Integer.parseInt(svContent.get(1));
            int svStreak = Integer.parseInt(svContent.get(2));
            int svActivePlayer = Integer.parseInt(svContent.get(3));
            GameSettings svSettings = new GameSettings(svRows, svCols, svStreak);

            byte[][] svBoard = new byte[svRows][svCols];
            for (int row = 0; row < svRows; row++) {
                String[] svLine = svContent.get(row + 4).split("");
                for (int col = 0; col < svCols; col++) {
                    svBoard[row][col] = Byte.parseByte(svLine[col]);
                }
            }
            this.settings = svSettings;
            this.board = svBoard;
            this.activePlayer = (byte) svActivePlayer;
            this.gameStatus = IModel.GAME_STATUS_ONGOING;

        } catch (IOException e) {
            System.err.println("No such file called \"" + fileName + "\" in the /saves folder.");
        }
    }

    // Returns whether or not the passed in move is valid at this time.
    public boolean isMoveValid(int move) {
        if (move == IModel.CONCEDE_MOVE) {
            return true;
        } else return move < settings.nrCols && move >= 0 && board[0][move] == 0;
    }

    // Actions the given move if it is valid. Otherwise, does nothing.
    public void makeMove(int move) {
        if (move != IModel.CONCEDE_MOVE) {
            for (int i = settings.nrRows - 1; i >= 0; i--) {
                if (board[i][move] == 0) {
                    board[i][move] = getActivePlayer();
                    break;
                }
            }
            activePlayer = (byte)(3 - activePlayer);
        } else {
            switch (getActivePlayer()) {
                case 1:
                    gameStatus = IModel.GAME_STATUS_WIN_2;
                    break;
                case 2:
                    gameStatus = IModel.GAME_STATUS_WIN_1;
                    break;
            }
        }
    }

    // Returns one of the following codes to indicate the game's current status.
    // IModel.java in the "interfaces" package defines constants you can use for this.
    // 0 = Game in progress
    // 1 = Player 1 has won
    // 2 = Player 2 has won
    // 3 = Tie (board is full and there is no winner)
    public byte getGameStatus() {
        // Assuming the game is never ending.
        if (gameStatus == IModel.GAME_STATUS_WIN_1) {
            return IModel.GAME_STATUS_WIN_1;
        } else if (gameStatus == IModel.GAME_STATUS_WIN_2) {
            return IModel.GAME_STATUS_WIN_2;
        }

        if (detectStreak(getActivePlayer())) {
            switch (getActivePlayer()) {
                case 1:
                    return IModel.GAME_STATUS_WIN_2;
                case 2:
                    return IModel.GAME_STATUS_WIN_1;
            }
        }

        for (int i = 0; i < settings.nrCols; i++) {
            if (board[0][i] == 0) {
                return IModel.GAME_STATUS_ONGOING;
            }
        }
        return IModel.GAME_STATUS_TIE;
    }

    // Returns the number of the player whose turn it is.
    public byte getActivePlayer() {
        return activePlayer;
    }

    // Returns the owner of the piece in the given row and column on the board.
    // Return 1 or 2 for players 1 and 2 respectively or 0 for empty cells.
    public byte getPieceIn(int row, int column) {
        return this.board[row][column];
    }

    // Returns a reference to the game settings, from which you can retrieve the
    // number of rows and columns the board has and how long the win streak is.
    public GameSettings getGameSettings() {
        return settings;
    }

    // =========================================================================
    // ================================ HELPERS ================================
    // =========================================================================
    private boolean detectStreak(byte player) {
        return (checkRows(getMatch(player))
                || checkCols(getMatch(player))
                || checkPos45(getMatch(player))
                || checkNeg45(getMatch(player)));
    }

    private String getMatch(byte player) {
        String matchPiece;
        switch (player) {
            case 1:
                matchPiece = "2";
                break;
            case 2:
                matchPiece = "1";
                break;
            default:
                matchPiece = null;
        }
        ;
        return matchPiece.repeat(settings.minStreakLength);
    }

    private boolean checkRows(String match) {
        String temp;
        for (int i = 0; i < settings.nrRows; i++) {
            temp = "";
            for (int j = 0; j < settings.nrCols; j++) {
                temp += board[i][j];
            }
            if (temp.contains(match)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkCols(String match) {
        String temp;
        for (int i = 0; i < settings.nrCols; i++) {
            temp = "";
            for (int j = 0; j < settings.nrRows; j++) {
                temp += board[j][i];
            }
            if (temp.contains(match)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkPos45(String match) {
        String temp;
        int no45 = settings.nrRows + settings.nrCols - 1;
        for (int i = 0; i < no45; i++) {
            temp = "";
            for (int a = 0, b = i; a <= i && b >= 0; a++, b--) {
                if (a < settings.nrRows && b < settings.nrCols) {
                    temp += board[a][b];
                }
            }
            if (temp.contains(match)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkNeg45(String match) {
        byte[][] board1 = new byte[settings.nrCols][settings.nrRows];
        for (int i = 0; i < settings.nrCols; i++) {
            for (int j = 0; j < settings.nrRows; j++) {
                board1[i][j] = board[j][settings.nrCols - 1 - i];
            }
        }
        String temp;
        int no45 = settings.nrCols + settings.nrRows - 1;
        for (int i = 0; i < no45; i++) {
            temp = "";
            for (int a = 0, b = i; a <= i && b >= 0; a++, b--) {
                if (a < settings.nrCols && b < settings.nrRows) {
                    temp += board1[a][b];
                }
            }
            if (temp.contains(match)) {
                return true;
            }
        }
        return false;
    }
    // You may find it useful to define some helper methods here.
}
