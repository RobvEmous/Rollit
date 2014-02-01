package game;

import java.awt.Point;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import client.ServerCommunicator;
import clientAndServer.Ball;
import clientAndServer.Board;
import clientAndServer.GlobalSettings;

import clientAndServer.Command;
import clientAndServer.Commands;
import exceptions.ClientTooSlowException;
import exceptions.ProtocolNotFollowedException;

/**
 * Represents a server Player in the Rollit game.
 * @author Rob van Emous
 */
public class OnlineHumanPlayer extends HumanPlayer {

    public Point choice = null;
    
    /**
     * Creates a new Player object.
     * 
     * @param name the name of the Player
     * @param ball the Ball of the Player
     */
    public OnlineHumanPlayer(String name, Ball ball) {
    	super (name, ball);
    }

    /**
     * Determines the field for the next move.
     * 
     * @param board the current board
     * @return the player's choice
     */
    public Point determineMove(Board board) {
		choice = null;
		boolean valid = false;
		while (choice == null || !valid) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			valid = board.isValidMove(getBall(), choice);
    		if (choice != null && !valid) {
    			OfflineGameUI.newPopup("Illegal move", "You can't turn that ball, " + 
    					"please choose another one.", false);
    			choice = null;
    		}
		}
		return choice;		
    }
}
