package client;

import java.io.IOException;
import java.net.InetAddress;

public class ClientCommunicator {
	
	private static final int TIME_OUT = 2000;
	private static final int SLEEP_TIME = 20;
	private static final int SLEEP_COUNT = TIME_OUT / SLEEP_TIME;
	
	private Client client;
	
	private String commandLogin = "login";
	private String commandJoin = "join";
	private String commandChallenge = "challenge";
	private String commandDisjoin = "disjoin";
	private String commandChat = "chat";
	private String commandMove = "move";
	private String commandQuitGame = "quitGame";
	private String commandGetHighScores = "getHighScores";
	private String commandLogout = "logOut";
	private String acknowledged = "Ack";
	
	private String loginSuccesfull = "welcome";
	private String loginUnSuccesfull = "incorrect";
	
	private String chatSuccesfull = "received";
	private String chatUnSuccesfull = "error";
	
	private String moveSuccesfull = "ok";
	private String moveUnSuccesfull = "kick";
	
	private String highScoreError = "";


	
	public ClientCommunicator(String name, InetAddress host, int port) throws IOException {
		client = new Client(name, host, port);
		client.start();
	}

	/**
	 * 
	 * @param name
	 * @param password
	 * @return 0 if exception, 1 if wrong password, 2 if success
	 */
	public int login(String name, String password) {
		int counter = 0;
		String[] args = {name, password};
		try {
			client.sendCommand(commandLogin, args);
			while (true) {
				for (Command c : client.getCommands()) {
					if (c.getCommand().equals(commandLogin + acknowledged)) {
						if (c.getArgs()[0].equals(loginSuccesfull)) {
							return 2;
						} else {
							return 1;
						}
					} 
				}
				try {
					Thread.sleep(SLEEP_TIME);
					counter++;
					if (counter >= SLEEP_COUNT) {
						throw new IOException();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
		
	public boolean join(int nrOfPlayers) {
		int counter = 0;
		String[] args = {nrOfPlayers + ""};
		try {
			client.sendCommand(commandJoin, args);
			while (true) {
				for (Command c : client.getCommands()) {
					if (c.getCommand().equals(commandJoin + acknowledged)) {
						return true;
					} 
				}
				try {
					Thread.sleep(SLEEP_TIME);
					counter++;
					if (counter >= SLEEP_COUNT) {
						throw new IOException();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean challenge(String[] playerNames) {
		int counter = 0;
		String[] args = playerNames;
		try {
			client.sendCommand(commandChallenge, args);
			while (true) {
				for (Command c : client.getCommands()) {
					if (c.getCommand().equals(commandJoin + acknowledged)) {
						return true;
					} 
				}
				try {
					Thread.sleep(SLEEP_TIME);
					counter++;
					if (counter >= SLEEP_COUNT) {
						throw new IOException();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public boolean disjoin() {
		return false;
		
	}
	
	public boolean chat(String message) {
		return false;
		
	}
	
	public boolean move(int x, int y) {
		return false;
		
	}
	
	public boolean logout(int x, int y) {
		return false;
		
	}
}
