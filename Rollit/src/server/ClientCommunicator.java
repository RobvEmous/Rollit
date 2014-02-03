package server;

import java.io.IOException;
import java.net.Socket;
import java.util.Observable;

import clientAndServer.Command;
import clientAndServer.Commands;
import clientAndServer.GlobalSettings;
import exceptions.ProtocolNotFollowedException;

/**
 * This class is the protocol-layer above the standard ClientHandler<br> 
 * It is able to send all supported commands and waits for an 
 * 'Ack'-command from the client. To be able to read commands from the 
 * client any class can observe this class and will be updated if a 
 * command is received.
 * @author Rob van Emous
 * @version 1.0
 */
public class ClientCommunicator extends Observable {
		
	private ClientHandler clientHandler;
	
	private boolean stop = false;
	
	/*@
	  requires sockArg != null;
	  ensures this != null;
	 */
	public ClientCommunicator(Socket sockArg) throws IOException {
		clientHandler = new ClientHandler(sockArg);
		clientHandler.start();
		readCommands();
	}
	
	private void readCommands() {
		Thread reader = new Thread(new Runnable() {		
			@Override
			public void run() {
				while (!stop && clientHandler.isAlive()) {
					for (Command c : clientHandler.getCommands()) {
						while (countObservers() == 0) {
							try {
								Thread.sleep(GlobalSettings.SLEEP_TIME);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						notifyObservers(c);
					}
					clientHandler.removeAllCommands();
					try {
						Thread.sleep(GlobalSettings.SLEEP_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}	
			}
		});
		reader.start();		
	}
	
	/**
	 * Sends an 'Ack'-command to the client belonging to the command
	 * <code>id</code>, with argument <code>arg</code>.
	 * @param id the id of the command
	 * @param arg possible argument about whether the command has been 
	 * accepted or not
	 * @throws IOException
	 */
	/*@
	  requires id != null && arg != null;
	 */	
	public synchronized void sendAck(String id, String arg) throws IOException {
		String[] args = {arg};
		clientHandler.sendCommand(id + Commands.COM_ACK, args);
	}

	/**
	 * Signals the client that a new game has been started and also sends
	 * the names of the players of this game.<br>
	 * This should only be used if the client is not already playing a 
	 * game and the name of this client <b>must</b> be amongst 
	 * <code>players</code>.
	 * 
	 * @param players the players of the new game
	 * @throws ProtocolNotFollowedException
	 * @throws IOException
	 */
	/*@
	  requires players != null;
	*/
	public void newGame(String[] players) throws ProtocolNotFollowedException, IOException {
		int counter = 0;
		String[] args = players;
		clientHandler.sendCommand(Commands.COM_NEWGAME, args);
		while (true) {
			for (Command c : clientHandler.getAnswers()) {
				if (c.getId().equals(Commands.COM_NEWGAME)) {
					clientHandler.removeAnswers(c);
					return;
				} 
			}
			try {
				Thread.sleep(GlobalSettings.SLEEP_TIME);
				counter++;
				if (counter >= GlobalSettings.SLEEP_COUNT) {
					throw new ProtocolNotFollowedException();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Signals the client that another player of the game has send a chat
	 * message and supplies this message to the client.<br>
	 * This should only be used while the client is playing a game.
	 * 
	 * @param message the chat message
	 * @throws ProtocolNotFollowedException
	 * @throws IOException
	 */
	/*@
	  requires message != null;
	 */
	public void message(String message) throws ProtocolNotFollowedException, IOException {
		int counter = 0;
		String[] args = {message};
		clientHandler.sendCommand(Commands.COM_MESSAGE, args);
		while (true) {
			for (Command c : clientHandler.getAnswers()) {
				if (c.getId().equals(Commands.COM_MESSAGE)) {
					clientHandler.removeAnswers(c);
					return;
				} 
			}
			try {
				Thread.sleep(GlobalSettings.SLEEP_TIME);
				counter++;
				if (counter >= GlobalSettings.SLEEP_COUNT) {
					throw new ProtocolNotFollowedException();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
		
	/**
	 * Signals the client that another player of the game has made a move
	 * and supplies the name of this player and the coordinates of the 
	 * move.<br>
	 * This should only be used while the client is playing a game and it
	 * is not his turn.
	 * 
	 * @param playerName the name of player who made the move
	 * @param x the x-coordinate of the move
	 * @param y the y-coordinate of the move
	 * @throws ProtocolNotFollowedException
	 * @throws IOException
	 */
	/*@
	  requires playerName != null && 0 <= x && 0 <= y;
	 */
	public void update(String playerName, int x, int y) throws ProtocolNotFollowedException, IOException {
		int counter = 0;
		String[] args = {playerName, x + "", y + ""};
		clientHandler.sendCommand(Commands.COM_UPDATE, args);
		while (true) {
			for (Command c : clientHandler.getAnswers()) {
				if (c.getId().equals(Commands.COM_UPDATE)) {
					clientHandler.removeAnswers(c);
					return;
				} 
			}
			try {
				Thread.sleep(GlobalSettings.SLEEP_TIME);
				counter++;
				if (counter >= GlobalSettings.SLEEP_COUNT) {
					throw new ProtocolNotFollowedException();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Signals the client that it is his turn and that he should perform a
	 * move within the available time. <br>
	 * This should only be used while the client is playing a game.
	 * 
	 * @see clientAndServer.GlobalSettings.THINK_TIME
	 * @throws ProtocolNotFollowedException
	 * @throws IOException
	 */
	public void yourTurn() throws ProtocolNotFollowedException, IOException {
		int counter = 0;
		String[] args = {};
		clientHandler.sendCommand(Commands.COM_YOURTURN, args);
		while (true) {
			for (Command c : clientHandler.getAnswers()) {
				if (c.getId().equals(Commands.COM_YOURTURN)) {
					clientHandler.removeAnswers(c);
					return;
				} 
			}
			try {
				Thread.sleep(GlobalSettings.SLEEP_TIME);
				counter++;
				if (counter >= GlobalSettings.SLEEP_COUNT) {
					throw new ProtocolNotFollowedException();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Signals the client he has taken too long to perform a move and 
	 * supplies the client with a forced (randomy chosen) move.<br>
	 * This should only be used while the client is playing a game and
	 * after he has got the yourTurn command, but before the server
	 * receives a move command from this client.
	 * 
	 * @param x the x-coordinate of the move
	 * @param y the y-coordinate of the move
	 * @throws ProtocolNotFollowedException
	 * @throws IOException
	 */
	/*@
	  requires 0 <= x && 0 <= y;
	 */
	public void moveTooSlow(int x, int y) throws ProtocolNotFollowedException, IOException {
		int counter = 0;
		String[] args = {x + "", y + ""};
		clientHandler.sendCommand(Commands.COM_MOVETOOSLOW, args);
		while (true) {
			for (Command c : clientHandler.getAnswers()) {
				if (c.getId().equals(Commands.COM_MOVETOOSLOW)) {
					clientHandler.removeAnswers(c);
					return;
				} 
			}
			try {
				Thread.sleep(GlobalSettings.SLEEP_TIME);
				counter++;
				if (counter >= GlobalSettings.SLEEP_COUNT) {
					throw new ProtocolNotFollowedException();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Signals the client that another player of the game has rage-quit
	 * and supplies the name of this player.<br>
	 * The game will continue if at least two players are left, otherwise
	 * a gameOver command should be sent after this command.<br>
	 * This should only be used while the client is playing a game.
	 * 
	 * @param playerName the name of this player
	 * @throws ProtocolNotFollowedException
	 * @throws IOException
	 */
	/*@
	  requires playerName != null;
	 */
	public void PlayerQuited(String playerName) throws ProtocolNotFollowedException, IOException {
		int counter = 0;
		String[] args = {playerName};
		clientHandler.sendCommand(Commands.COM_PLAYERQUIT, args);
		while (true) {
			for (Command c : clientHandler.getAnswers()) {
				if (c.getId().equals(Commands.COM_PLAYERQUIT)) {
					clientHandler.removeAnswers(c);
					return;
				} 
			}
			try {
				Thread.sleep(GlobalSettings.SLEEP_TIME);
				counter++;
				if (counter >= GlobalSettings.SLEEP_COUNT) {
					throw new ProtocolNotFollowedException();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Signals the client that the game is over. This is either because
	 * the board is full or because all other players have rage-quit.<br>
	 * This should only be used while the client is playing a game.
	 * 
	 * @throws ProtocolNotFollowedException
	 * @throws IOException
	 */
	public void gameOver() throws ProtocolNotFollowedException, IOException {
		int counter = 0;
		String[] args = {};
		clientHandler.sendCommand(Commands.COM_GAMEOVER, args);
		while (true) {
			for (Command c : clientHandler.getAnswers()) {
				if (c.getId().equals(Commands.COM_GAMEOVER)) {
					clientHandler.removeAnswers(c);
					return;
				} 
			}
			try {
				Thread.sleep(GlobalSettings.SLEEP_TIME);
				counter++;
				if (counter >= GlobalSettings.SLEEP_COUNT) {
					throw new ProtocolNotFollowedException();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void notifyObservers(Object argument) {
		setChanged();
		super.notifyObservers(argument);
	}
	
	/*@
	  ensures \result != null;
	 */
	@Override
	public String toString() {
		return clientHandler.toString();
	}
	
	/**
	 * Shuts down the entire communication between the client and the 
	 * server.<br>
	 * This should only be done when the connection with the client has
	 * been lost or when the client is kicked
	 * This cannot be undone otherwise than re-constructing this class.
	 * @param selfDestruct whether this class has initiated the shutdown or not
	 */
	public void shutdown(boolean selfDestruct) {
		stop = true;
		clientHandler.shutdown();	
		if (selfDestruct) {
			notifyObservers(new Command("Player died", new String[]{}));
		}
	}
	
}
