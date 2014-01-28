package server;

import java.awt.Point;

/**
 * Represents a client Player in the Rollit game.
 * @author Rob van Emous
 */
public class GamePlayer {

    private String name;
    private Ball ball;
    private ClientCommunicator c;

    /**
     * Creates a new Player object.
     * 
     * @param name the name of the Player
     * @param ball the Ball of the Player
     */
    public GamePlayer(String name, Ball ball, ClientCommunicator c) {
        this.name = name;
        this.ball = ball;
    }

    /**
     * Returns the name of the player.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the ball of the player.
     */
    public Ball getBall() {
        return ball;
    }

    /**
     * Determines the field for the next move.
     * 
     * @param board the current board
     * @return the player's choice
     */
    public Point determineMove(Board board) {
		return null;
    	
    }


    /**
     * Makes a move on the board.
     * 
     * @param board the current board
     */
    public void makeMove(Board board) {
        Point choice = determineMove(board);
        board.setField(choice, getBall());
    }

}
