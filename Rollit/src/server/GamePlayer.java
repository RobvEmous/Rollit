package server;

import java.awt.Point;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

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
public class GamePlayer {

	private String name;
    private Ball ball;
    private ClientCommunicator client;
    
	/**
	 * Creates a new server-side game player object.
	 * 
	 * @param client the client this player is connected to
	 * @param ball the Ball of the Player
	 */
    public GamePlayer(String name, ClientCommunicator client, Ball ball) {
    	this.name = name;
    	this.client = client;
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
     * Sets the ball of the player.
     * @param ball the ball of the player
     */
    public void setBall(Ball ball) {
        this.ball = ball;
    }
    
    /**
     * Returns the {@link ClientCommunicator} object associated with this 
     * player.
     */
    public ClientCommunicator getClient() {
		return client;
	}
    
    @Override
    public String toString() {
    	return name + " (" + ball + ") - ";
    }

	
	/*
	 * 		int counter = 0;
		moveCommand = null;
		client.yourTurn();
		while (moveCommand == null) {
			try {
				Thread.sleep(GlobalSettings.SLEEP_TIME);
				counter++;
				if (counter >= GlobalSettings.THINK_TIME / GlobalSettings.SLEEP_TIME) {
					throw new ClientTooSlowException();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Point move = null;
		try {
			int x = Integer.parseInt(moveCommand.getArgs()[0]);
			int y = Integer.parseInt(moveCommand.getArgs()[1]);
			move = new Point(x,y);
		} catch (NumberFormatException e) {
			client.sendAck(Commands.COM_MOVE, Commands.ANS_GEN_BAD);
			throw new ProtocolNotFollowedException();
		}
		client.sendAck(Commands.COM_MOVE, Commands.ANS_GEN_GOOD);
		return move;*/
	 
	
}
