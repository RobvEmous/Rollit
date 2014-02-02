package client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Observable;

import clientAndServer.Command;
import clientAndServer.Commands;
import clientAndServer.GlobalSettings;
import clientAndServer.Tools;
import exceptions.NotSameStateException;
import exceptions.ProtocolNotFollowedException;

/**
 * This class is the protocol-layer above the standard client<br> 
 * It is able to send all supported commands and waits for an 
 * 'Ack'-command from the server. To be able to read commands from the 
 * server any class can observe this class and will be updated if a 
 * command is received.
 * @author Rob van Emous
 * @version 1.0
 */
public class ServerCommunicator extends Observable {
	
	private Client client;
	
	private boolean stop = false;

	public ServerCommunicator(String name, InetAddress host, int port) throws IOException {
		client = new Client(name, host, port);
		client.start();
		readCommands();
	}
	
	private void readCommands() {
		Thread reader = new Thread(new Runnable() {		
			@Override
			public void run() {
				while (!stop) {
					for (Command c : client.getCommands()) {
						while (countObservers() == 0) {
							try {
								Thread.sleep(GlobalSettings.SLEEP_TIME);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						notifyObservers(c);
					}
					client.removeAllCommands();
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
	 * Sends an 'Ack'-command to the server belonging to the command
	 * <code>id</code>, with argument <code>arg</code>.
	 * 
	 * @param id the id of the command
	 * @param arg possible argument about whether the command has been 
	 * accepted or not
	 * @throws IOException
	 */
	public synchronized void sendAck(String id, String arg) throws IOException {
		String[] args = {arg};
		client.sendCommand(id + Commands.COM_ACK, args);
	}

	/**
	 * Tries to login to the server with this name and password.<br>
	 * This should only be done if the user is not logged in yet.
	 * 
	 * @param name the name of the account
	 * @param password the password of the account
	 * @return true if success, false if wrong password
	 * @throws ProtocolNotFollowedException
	 * @throws IOException
	 * @throws NotSameStateException
	 */
	public synchronized boolean login(String name, String password) throws ProtocolNotFollowedException, IOException, NotSameStateException {
		int counter = 0;
		String[] args = {Tools.replaceSpace(name), password};
		client.sendCommand(Commands.COM_LOGIN, args);
		while (true) {
			for (Command c : client.getAnswers()) {
				if (c.getId().equals(Commands.COM_LOGIN)) {
					client.removeAnswers(c);
					if (c.getArgs()[0].equals(Commands.ANS_LOGIN_GOOD)) {
						return true;
					} else if (c.getArgs()[0].equals(Commands.ANS_LOGIN_BAD)) {
						return false;
					} else {
						System.out.println(c.toString());
						throw new NotSameStateException();
					}
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
	 * Tries to join a game with the specified amount of players.<br>
	 * This should only be done if no join or challenge commands have been
	 * send yet.
	 * 
	 * @param nrOfPlayers the number of players
	 * @throws ProtocolNotFollowedException
	 * @throws IOException
	 * @throws NotSameStateException
	 */
	public synchronized void join(int nrOfPlayers) throws ProtocolNotFollowedException, IOException, NotSameStateException {
		int counter = 0;
		String[] args = {nrOfPlayers + ""};
		client.sendCommand(Commands.COM_JOIN, args);
		while (true) {
			for (Command c : client.getAnswers()) {
				if (c.getId().equals(Commands.COM_JOIN)) {
					client.removeAnswers(c);
					if (c.getArgs()[0].equals(Commands.ANS_GEN_BAD)) {
						throw new NotSameStateException();
					}
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
	 * Tries to challenge the specified players to play a game.<br>
	 * This should only be done if no join or challenge commands have been
	 * send yet.
	 * 
	 * @param playerNames the names of the challenged players
	 * @throws ProtocolNotFollowedException
	 * @throws IOException
	 * @throws NotSameStateException
	 */
	public synchronized void challenge(String[] playerNames) throws ProtocolNotFollowedException, IOException, NotSameStateException {
		int counter = 0;
		String[] args = playerNames;
		client.sendCommand(Commands.COM_CHALLENGE, args);
		while (true) {
			for (Command c : client.getAnswers()) {
				if (c.getId().equals(Commands.COM_CHALLENGE)) {
					client.removeAnswers(c);
					if (c.getArgs()[0].equals(Commands.ANS_GEN_BAD)) {
						throw new NotSameStateException();
					}
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
	 * Tries to undo a join or challenge command.<br>
	 * This should only be done if a join or challenge command has been 
	 * sent previously and a newGame command has not been received yet.
	 * 
	 * @throws ProtocolNotFollowedException
	 * @throws IOException
	 * @throws NotSameStateException
	 */
	public synchronized void disjoin() throws ProtocolNotFollowedException, IOException, NotSameStateException {
		int counter = 0;
		String[] args = {};
		client.sendCommand(Commands.COM_DISJOIN, args);
		while (true) {
			for (Command c : client.getAnswers()) {
				if (c.getId().equals(Commands.COM_DISJOIN)) {
					client.removeAnswers(c);
					if (c.getArgs()[0].equals(Commands.ANS_GEN_BAD)) {
						throw new NotSameStateException();
					}
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
	 * Tries to send a chat message to the other players of the game.<br>
	 * This should only be done while playing an online game.
	 * 
	 * @param message the message to send
	 * @throws ProtocolNotFollowedException
	 * @throws IOException
	 * @throws NotSameStateException
	 */
	public synchronized void chat(String message) throws ProtocolNotFollowedException, IOException, NotSameStateException {
		int counter = 0;
		String[] args = {message};
		client.sendCommand(Commands.COM_CHAT, args);
		while (true) {
			for (Command c : client.getAnswers()) {
				if (c.getId().equals(Commands.COM_CHAT)) {
					client.removeAnswers(c);
					if (c.getArgs()[0].equals(Commands.ANS_GEN_BAD)) {
						throw new NotSameStateException();
					}
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
	 * Tries to perform a move while playing an online game.<br>
	 * This should only be done while playing an online game, after
	 * receiving a yourTurn command and before receiving a moveToSlow 
	 * command. The latter signals that the player has taken to much time
	 * to perform a move and a random move will be done for the client.
	 * 
	 * @param x the x-coördinate
	 * @param y the x-coördinate
	 * @throws ProtocolNotFollowedException
	 * @throws IOException
	 * @throws NotSameStateException
	 */
	public synchronized void move(int x, int y) throws ProtocolNotFollowedException, IOException, NotSameStateException {
		int counter = 0;
		String[] args = {x + "", y + ""};
		client.sendCommand(Commands.COM_MOVE, args);
		while (true) {
			for (Command c : client.getAnswers()) {
				if (c.getId().equals(Commands.COM_MOVE)) {
					client.removeAnswers(c);
					if (c.getArgs()[0].equals(Commands.ANS_GEN_BAD)) {
						throw new NotSameStateException();
					}
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
	 * Tries to logout from the server.<br>
	 * This should only be done when logged in and not while playing a 
	 * game. If the user is playing a game and rage-quits the whole 
	 * application first a quitGame command should be send and after this
	 * the client can safely logout.
	 * 
	 * @throws ProtocolNotFollowedException
	 * @throws IOException
	 * @throws NotSameStateException
	 */
	public synchronized void logout() throws ProtocolNotFollowedException, IOException, NotSameStateException {
		int counter = 0;
		String[] args = {};
		client.sendCommand(Commands.COM_LOGOUT, args);
		while (true) {
			for (Command c : client.getAnswers()) {
				if (c.getId().equals(Commands.COM_LOGOUT)) {
					client.removeAnswers(c);
					if (c.getArgs()[0].equals(Commands.ANS_GEN_BAD)) {
						throw new NotSameStateException();
					}
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
	 * Tries to quit a online game while in the middle of playing it.<br>
	 * This should only be done while playing a game.
	 * 
	 * @throws ProtocolNotFollowedException
	 * @throws IOException
	 * @throws NotSameStateException
	 */
	public void quitGame() throws ProtocolNotFollowedException, IOException, NotSameStateException {
		int counter = 0;
		String[] args = {};
		client.sendCommand(Commands.COM_QUIT, args);
		while (true) {
			for (Command c : client.getAnswers()) {
				if (c.getId().equals(Commands.COM_QUIT)) {
					client.removeAnswers(c);
					if (c.getArgs()[0].equals(Commands.ANS_GEN_BAD)) {
						throw new NotSameStateException();
					}
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

	/**
	 * Shuts down the entire communication between the client and the 
	 * server.<br>
	 * This should only be done after sending a quitGame command if 
	 * in the middle of playing an online game and after sending a logout
	 * command if logged in.<br>
	 * This cannot be undone otherwise than re-constructing this class.
	 */
	public void shutdown() {
		stop = true;
		client.shutdown();	
	}
}
