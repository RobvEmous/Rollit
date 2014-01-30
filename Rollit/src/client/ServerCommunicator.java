package client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Observable;

import clientAndServer.Command;
import clientAndServer.Commands;
import clientAndServer.GlobalSettings;
import clientAndServer.Tools;
import exceptions.ProtecolNotFollowedException;

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
						notifyObservers(c);
						client.removeCommand(c);
					}
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
	 * Tries to login to the server.
	 * @param name the name of the account
	 * @param password the password of the account
	 * @return true if success, false if wrong password
	 */
	public boolean login(String name, String password) throws ProtecolNotFollowedException, IOException {
		int counter = 0;
		String[] args = {Tools.replaceSpace(name), password};
		client.sendCommand(Commands.COM_LOGIN, args);
		while (true) {
			for (Command c : client.getAnswers()) {
				if (c.getId().equals(Commands.COM_LOGIN + Commands.COM_ACK)) {
					client.removeCommand(c);
					if (c.getArgs()[0].equals(Commands.COM_LOGIN_G)) {
						return true;
					} else {
						return false;
					}
				} 
			}
			try {
				Thread.sleep(GlobalSettings.SLEEP_TIME);
				counter++;
				if (counter >= GlobalSettings.SLEEP_COUNT) {
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
		client.sendCommand(Commands.COM_JOIN, args);
		while (true) {
			for (Command c : client.getAnswers()) {
				if (c.getId().equals(Commands.COM_JOIN + Commands.COM_ACK)) {
					client.removeCommand(c);
					return true;
				} 
			}
			try {
				Thread.sleep(GlobalSettings.SLEEP_TIME);
				counter++;
				if (counter >= GlobalSettings.SLEEP_COUNT) {
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
		client.sendCommand(Commands.COM_CHALLENGE, args);
		while (true) {
			for (Command c : client.getAnswers()) {
				if (c.getId().equals(Commands.COM_CHALLENGE + Commands.COM_ACK)) {
					client.removeCommand(c);
					return true;
				} 
			}
			try {
				Thread.sleep(GlobalSettings.SLEEP_TIME);
				counter++;
				if (counter >= GlobalSettings.SLEEP_COUNT) {
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
		client.sendCommand(Commands.COM_DISJOIN, args);
		while (true) {
			for (Command c : client.getAnswers()) {
				if (c.getId().equals(Commands.COM_DISJOIN + Commands.COM_ACK)) {
					client.removeCommand(c);
					return;
				} 
			}
			try {
				Thread.sleep(GlobalSettings.SLEEP_TIME);
				counter++;
				if (counter >= GlobalSettings.SLEEP_COUNT) {
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
		client.sendCommand(Commands.COM_CHAT, args);
		while (true) {
			for (Command c : client.getAnswers()) {
				if (c.getId().equals(Commands.COM_CHAT + Commands.COM_ACK)) {
					client.removeCommand(c);
					if (c.getArgs()[0].equals(Commands.COM_CHAT_G)) {
						return true;
					} else {
						return false;
					}
				} 
			}
			try {
				Thread.sleep(GlobalSettings.SLEEP_TIME);
				counter++;
				if (counter >= GlobalSettings.SLEEP_COUNT) {
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
		client.sendCommand(Commands.COM_MOVE, args);
		while (true) {
			for (Command c : client.getAnswers()) {
				if (c.getId().equals(Commands.COM_MOVE + Commands.COM_ACK)) {
					client.removeCommand(c);
					if (c.getArgs()[0].equals(Commands.COM_MOVE_G)) {
						return true;
					} else {
						return false;
					}
				} 
			}
			try {
				Thread.sleep(GlobalSettings.SLEEP_TIME);
				counter++;
				if (counter >= GlobalSettings.SLEEP_COUNT) {
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
		client.sendCommand(Commands.COM_LOGOUT, args);
		while (true) {
			for (Command c : client.getAnswers()) {
				if (c.getId().equals(Commands.COM_LOGOUT + Commands.COM_ACK)) {
					client.removeCommand(c);
					return;
				} 
			}
			try {
				Thread.sleep(GlobalSettings.SLEEP_TIME);
				counter++;
				if (counter >= GlobalSettings.SLEEP_COUNT) {
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
		client.sendCommand(Commands.COM_QUIT, args);
		while (true) {
			for (Command c : client.getAnswers()) {
				if (c.getId().equals(Commands.COM_QUIT + Commands.COM_ACK)) {
					client.removeCommand(c);
					return;
				} 
			}
			try {
				Thread.sleep(GlobalSettings.SLEEP_TIME);
				counter++;
				if (counter >= GlobalSettings.SLEEP_COUNT) {
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
