package server;

import java.awt.Point;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import clientAndServer.Ball;
import clientAndServer.Board;
import clientAndServer.Command;
import clientAndServer.Commands;
import exceptions.ClientTooSlowException;
import exceptions.ProtecolNotFollowedException;

/**
 * Represents a server Player in the Rollit game.
 * @author Rob van Emous
 */
public class GamePlayer implements Observer {

    private Ball ball;
    private int thinkTime;
    private ClientCommunicator client;
    private Command moveCommand = null;
    
    /**
     * Creates a new Player object.
     * 
     * @param name the name of the Player
     * @param ball the Ball of the Player
     */
    public GamePlayer(Ball ball, int thinkTime, ClientCommunicator client) {
    	client.addObserver(this);
        this.ball = ball;
        this.thinkTime = thinkTime;
    }

    /**
     * Returns the name of the player.
     */
   /* public String getName() {
        return client;
    }*/

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
    public Point determineMove(Board board) throws ClientTooSlowException, ProtecolNotFollowedException, IOException {
		int counter = 0;
		moveCommand = null;
		client.yourTurn();
		while (moveCommand == null) {
			try {
				Thread.sleep(ClientCommunicator.SLEEP_TIME);
				counter++;
				if (counter >= thinkTime / ClientCommunicator.SLEEP_TIME) {
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
			client.sendAck(Commands.COM_MOVE, Commands.COM_MOVE_B);
			throw new ProtecolNotFollowedException();
		}
		client.sendAck(Commands.COM_MOVE, Commands.COM_MOVE_G);
		return move;		
    }


    /**
     * Makes a move on the board.
     * 
     * @param board the current board
     */
    public void makeMove(Board board) throws ClientTooSlowException, ProtecolNotFollowedException, IOException {
        Point choice = determineMove(board);
        board.setField(choice, getBall());
    }

	@Override
	public void update(Observable o, Object arg)  {
		if (o.equals(client)) {
			Command comm = (Command) arg; 
			if (comm.getId().equals(Commands.COM_MOVE)) {	
				moveCommand = comm;
			}
		}
		
	}

}
