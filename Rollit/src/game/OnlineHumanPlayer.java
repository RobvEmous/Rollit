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

    private Point choice = null;
    
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
     * Sets the choice of this player to <code>choice</code>.
     * @param choice the chosen choice 
     */
    public void setChoice(Point choice) {
    	this.choice = choice;
	}

    /**
     * Determines the field for the next move.
     * 
     * @param board the current board
     * @return the player's choice
     */
    public Point determineMove(Board board) {
		choice = null;
		while (choice == null) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	

    		if (choice != null) {
    			if (board.isValidMove(getBall(), choice)) {
    				return choice;	
    			} else {
        			OfflineGameUI.newPopup("Illegal move", "You can't turn that ball, " + 
        					"please choose another one.", false);
        			choice = null;
    			}
    		}
		}
		return null;

    }
}
