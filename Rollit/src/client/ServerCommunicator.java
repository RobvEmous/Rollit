package client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Observable;

import clientAndServer.Command;
import clientAndServer.Tools;
import exceptions.ProtecolNotFollowedException;

public class ServerCommunicator extends Observable {
	
	private static final int TIME_OUT = 2000;
	private static final int SLEEP_TIME = 20;
	private static final int SLEEP_COUNT = TIME_OUT / SLEEP_TIME;
	
	private Client client;
	
	private String commandLogin = "login";
	private String loginSuccesfull = "welcome";
	private String loginUnSuccesfull = "incorrect";
	
	private String commandJoin = "join";
	
	private String commandChallenge = "challenge";
	
	private String commandDisjoin = "disjoin";
	
	private String commandChat = "chat";
	
	private String chatSuccesfull = "received";
	private String chatUnSuccesfull = "error";
	
	private String commandMove = "move";
	private String moveSuccesfull = "ok";
	private String moveUnSuccesfull = "kick";
	
	private String commandQuitGame = "quitGame";
	
	private String commandGetHighScores = "getHighScores";
	private String highScoreError = "";
	
	private String commandLogout = "logOut";

	public String commandNewGame = "newGame";
	public String commandMessage = "message";
	public String commandUpdate = "update";
	public String commandYourTurn = "yourTurn";
	public String commandGameOver = "gameOver";
	
	private boolean stop = false;
	

	public ServerCommunicator(String name, InetAddress host, int port) throws IOException {
		client = new Client(name, host, port);
		client.start();
		readCommands();
	}
	
	public ServerCommunicator(String name, int port) throws IOException {
		this(name, InetAddress.getLocalHost(), port);
	}
	
	public ServerCommunicator(String name, InetAddress host) throws IOException {
		this(name, host, Client.PORT);
	}
	
	public ServerCommunicator(String name) throws IOException {
		this(name, Client.PORT);
	}
	
	private void readCommands() {
		Thread reader = new Thread(new Runnable() {		
			@Override
			public void run() {
				while (!stop) {
					for (Command c : client.getCommands()) {
						notifyObservers(c);
						client.removeCommand(c);
					}
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}		
			}
		});
		reader.start();		
	}

	/**
	 * Tries to login to the server.
	 * @param name the name of the account
	 * @param password the password of the account
	 * @return true if success, false if wrong password
	 */
	public boolean login(String name, String password) throws ProtecolNotFollowedException, IOException {
		int counter = 0;
		String[] args = {Tools.replaceSpace(name), password};
		client.sendCommand(commandLogin, args);
		while (true) {
			for (Command c : client.getAnswers()) {
				if (c.getId().equals(commandLogin + Client.ACKNOWLEDGED)) {
					client.removeCommand(c);
					if (c.getArgs()[0].equals(loginSuccesfull)) {
						return true;
					} else {
						return false;
					}
				} 
			}
			try {
				Thread.sleep(SLEEP_TIME);
				counter++;
				if (counter >= SLEEP_COUNT) {
					throw new ProtecolNotFollowedException();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
		
	/**
	 * 
	 * @param nrOfPlayers
	 * @return
	 */
	public boolean join(int nrOfPlayers) throws ProtecolNotFollowedException, IOException {
		int counter = 0;
		String[] args = {nrOfPlayers + ""};
		client.sendCommand(commandJoin, args);
		while (true) {
			for (Command c : client.getAnswers()) {
				if (c.getId().equals(commandJoin + Client.ACKNOWLEDGED)) {
					client.removeCommand(c);
					return true;
				} 
			}
			try {
				Thread.sleep(SLEEP_TIME);
				counter++;
				if (counter >= SLEEP_COUNT) {
					throw new ProtecolNotFollowedException();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * 
	 * @param playerNames
	 * @return
	 */
	public boolean challenge(String[] playerNames) throws ProtecolNotFollowedException, IOException {
		int counter = 0;
		String[] args = playerNames;
		client.sendCommand(commandChallenge, args);
		while (true) {
			for (Command c : client.getAnswers()) {
				if (c.getId().equals(commandChallenge + Client.ACKNOWLEDGED)) {
					client.removeCommand(c);
					return true;
				} 
			}
			try {
				Thread.sleep(SLEEP_TIME);
				counter++;
				if (counter >= SLEEP_COUNT) {
					throw new ProtecolNotFollowedException();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public void disjoin() throws ProtecolNotFollowedException, IOException {
		int counter = 0;
		String[] args = {};
		client.sendCommand(commandDisjoin, args);
		while (true) {
			for (Command c : client.getAnswers()) {
				if (c.getId().equals(commandDisjoin + Client.ACKNOWLEDGED)) {
					client.removeCommand(c);
					return;
				} 
			}
			try {
				Thread.sleep(SLEEP_TIME);
				counter++;
				if (counter >= SLEEP_COUNT) {
					throw new ProtecolNotFollowedException();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	
	}
	
	/**
	 * 
	 * @param message
	 * @return
	 */
	public boolean chat(String message) throws ProtecolNotFollowedException, IOException {
		int counter = 0;
		String[] args = {message};
		client.sendCommand(commandChat, args);
		while (true) {
			for (Command c : client.getAnswers()) {
				if (c.getId().equals(commandChat + Client.ACKNOWLEDGED)) {
					client.removeCommand(c);
					if (c.getArgs()[0].equals(chatSuccesfull)) {
						return true;
					} else {
						return false;
					}
				} 
			}
			try {
				Thread.sleep(SLEEP_TIME);
				counter++;
				if (counter >= SLEEP_COUNT) {
					throw new ProtecolNotFollowedException();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean move(int x, int y) throws ProtecolNotFollowedException, IOException {
		int counter = 0;
		String[] args = {x + "", y + ""};
		client.sendCommand(commandMove, args);
		while (true) {
			for (Command c : client.getAnswers()) {
				if (c.getId().equals(commandMove + Client.ACKNOWLEDGED)) {
					client.removeCommand(c);
					if (c.getArgs()[0].equals(moveSuccesfull)) {
						return true;
					} else {
						return false;
					}
				} 
			}
			try {
				Thread.sleep(SLEEP_TIME);
				counter++;
				if (counter >= SLEEP_COUNT) {
					throw new ProtecolNotFollowedException();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * 
	 * @return
	 */
	public void logout() throws ProtecolNotFollowedException, IOException {
		int counter = 0;
		String[] args = {};
		client.sendCommand(commandLogout, args);
		while (true) {
			for (Command c : client.getAnswers()) {
				if (c.getId().equals(commandLogout + Client.ACKNOWLEDGED)) {
					client.removeCommand(c);
					return;
				} 
			}
			try {
				Thread.sleep(SLEEP_TIME);
				counter++;
				if (counter >= SLEEP_COUNT) {
					throw new ProtecolNotFollowedException();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
	}
	
	/**
	 * 
	 * @return
	 */
	public void quitGame() throws ProtecolNotFollowedException, IOException {
		int counter = 0;
		String[] args = {};
		client.sendCommand(commandQuitGame, args);
		while (true) {
			for (Command c : client.getAnswers()) {
				if (c.getId().equals(commandQuitGame + Client.ACKNOWLEDGED)) {
					client.removeCommand(c);
					return;
				} 
			}
			try {
				Thread.sleep(SLEEP_TIME);
				counter++;
				if (counter >= SLEEP_COUNT) {
					throw new ProtecolNotFollowedException();
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

	public void shutdown() {
		stop = true;
		client.shutdown();	
	}
}
