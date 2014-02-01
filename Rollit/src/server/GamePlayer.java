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
public class GamePlayer implements Observer {

    private Ball ball;
    private ClientCommunicator client;
    private Command moveCommand = null;
    
	/**
	 * Creates a new server-side game player object.
	 * 
	 * @param client the client this player is connected to
	 * @param ball the Ball of the Player
	 */
    public GamePlayer(ClientCommunicator client, Ball ball) {
    	client.addObserver(this);
        this.ball = ball;
    }

    /**
     * Returns the ball of the player.
     */
    public Ball getBall() {
        return ball;
    }
    
    public void sendchatMessage(String message) {
    	client.
    }

    /**
     * Determines the field for the next move.
     * 
     * @param board the current board
     * @return the player's choice
     */
    public Point determineMove(Board board) throws ClientTooSlowException, ProtocolNotFollowedException, IOException {
		int counter = 0;
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
		return move;		
    }

    /**
     * Makes a move on the board.
     * 
     * @param board the current board
     */
    public void makeMove(Board board) throws ClientTooSlowException, ProtocolNotFollowedException, IOException {
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
