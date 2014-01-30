package server;

import java.io.IOException;
import java.net.Socket;
import java.util.Observable;

import clientAndServer.Command;
import clientAndServer.Commands;
import clientAndServer.GlobalSettings;
import exceptions.ProtecolNotFollowedException;

public class ClientCommunicator extends Observable {
		
	private ClientHandler clientHandler;
	
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
						Thread.sleep(GlobalSettings.SLEEP_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}		
			}
		});
		reader.setDaemon(true);
		reader.start();		
	}
	
	public synchronized void sendAck(String id, String arg) throws IOException {
		String[] args = {arg};
		clientHandler.sendCommand(id + Commands.COM_ACK, args);
	}

	/**
	 * 
	 * @param players
	 * @throws ProtecolNotFollowedException
	 * @throws IOException
	 */
	public void newGame(String[] players) throws ProtecolNotFollowedException, IOException {
		int counter = 0;
		String[] args = players;
		clientHandler.sendCommand(Commands.COM_NEWGAME, args);
		while (true) {
			for (Command c : clientHandler.getAnswers()) {
				if (c.getId().equals(Commands.COM_NEWGAME + Commands.COM_ACK)) {
					clientHandler.removeCommand(c);
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
	 * @param playerName
	 * @param x
	 * @param y
	 * @throws ProtecolNotFollowedException
	 * @throws IOException
	 */
	public void update(String playerName, int x, int y) throws ProtecolNotFollowedException, IOException {
		int counter = 0;
		String[] args = {playerName, x + "", y + ""};
		clientHandler.sendCommand(Commands.COM_UPDATE, args);
		while (true) {
			for (Command c : clientHandler.getAnswers()) {
				if (c.getId().equals(Commands.COM_UPDATE + Commands.COM_ACK)) {
					clientHandler.removeCommand(c);
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
	 * @throws ProtecolNotFollowedException
	 * @throws IOException
	 */
	public void yourTurn() throws ProtecolNotFollowedException, IOException {
		int counter = 0;
		String[] args = {};
		clientHandler.sendCommand(Commands.COM_YOURTURN, args);
		while (true) {
			for (Command c : clientHandler.getAnswers()) {
				if (c.getId().equals(Commands.COM_YOURTURN + Commands.COM_ACK)) {
					clientHandler.removeCommand(c);
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
	 * @throws ProtecolNotFollowedException
	 * @throws IOException
	 */
	public void PlayerQuited() throws ProtecolNotFollowedException, IOException {
		int counter = 0;
		String[] args = {};
		clientHandler.sendCommand(Commands.COM_GAMEOVER, args);
		while (true) {
			for (Command c : clientHandler.getAnswers()) {
				if (c.getId().equals(Commands.COM_GAMEOVER + Commands.COM_ACK)) {
					clientHandler.removeCommand(c);
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
	 * @throws ProtecolNotFollowedException
	 * @throws IOException
	 */
	public void gameOver() throws ProtecolNotFollowedException, IOException {
		int counter = 0;
		String[] args = {};
		clientHandler.sendCommand(Commands.COM_GAMEOVER, args);
		while (true) {
			for (Command c : clientHandler.getAnswers()) {
				if (c.getId().equals(Commands.COM_GAMEOVER + Commands.COM_ACK)) {
					clientHandler.removeCommand(c);
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
}
