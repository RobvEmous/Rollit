package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Observable;

import clientAndServer.Command;
import exceptions.ProtecolNotFollowedException;

public class ClientCommunicator extends Observable {
	
	private static final int TIME_OUT = 2000;
	private static final int SLEEP_TIME = 20;
	private static final int SLEEP_COUNT = TIME_OUT / SLEEP_TIME;
	
	private ClientHandler clientHandler;
	
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

	public ClientCommunicator(Server serverArg, Socket sockArg) throws IOException {
		clientHandler = new ClientHandler(serverArg, sockArg);
		clientHandler.start();
		readCommands();
	}
	
	private void readCommands() {
		Thread reader = new Thread(new Runnable() {		
			@Override
			public void run() {
				while (true) {
					for (Command c : clientHandler.getCommands()) {
						notifyObservers(c);
						clientHandler.removeCommand(c);
					}
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}		
			}
		});
		reader.setDaemon(true);
		reader.start();		
	}

	/**
	 * 
	 * @param name
	 * @param password
	 * @return 0 if exception, 1 if wrong password, 2 if success
	 */
	public void newGame(String[] players) throws ProtecolNotFollowedException, IOException {
		int counter = 0;
		String[] args = players;
		clientHandler.sendCommand(commandNewGame, args);
		while (true) {
			for (Command c : clientHandler.getAnswers()) {
				if (c.getId().equals(commandNewGame + ClientHandler.ACKNOWLEDGED)) {
					clientHandler.removeCommand(c);
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
	 * @param x
	 * @param y
	 * @return
	 */
	public void update(String playerName, int x, int y) throws ProtecolNotFollowedException, IOException {
		int counter = 0;
		String[] args = {playerName, x + "", y + ""};
		clientHandler.sendCommand(commandUpdate, args);
		while (true) {
			for (Command c : clientHandler.getAnswers()) {
				if (c.getId().equals(commandUpdate + ClientHandler.ACKNOWLEDGED)) {
					clientHandler.removeCommand(c);
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
	 */
	public void yourTurn() throws ProtecolNotFollowedException, IOException {
		int counter = 0;
		String[] args = {};
		clientHandler.sendCommand(commandYourTurn, args);
		while (true) {
			for (Command c : clientHandler.getAnswers()) {
				if (c.getId().equals(commandYourTurn + ClientHandler.ACKNOWLEDGED)) {
					clientHandler.removeCommand(c);
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
	 */
	public void gameOver() throws ProtecolNotFollowedException, IOException {
		int counter = 0;
		String[] args = {};
		clientHandler.sendCommand(commandGameOver, args);
		while (true) {
			for (Command c : clientHandler.getAnswers()) {
				if (c.getId().equals(commandGameOver + ClientHandler.ACKNOWLEDGED)) {
					clientHandler.removeCommand(c);
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
}
