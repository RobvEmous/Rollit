package server;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import clientAndServer.GlobalSettings;

import exceptions.PortInUseException;

public class Main {
	
	private MainUI mainGUI;
	private ClientManager clientmanager;
	private GameManager gameManager;
	private Server server;
	
	private boolean started = false;
	
	public Main() {
		mainGUI = new MainUI(this);
		mainGUI.setVisible(true);
	}
	
	/**
	 * Stops the whole server-side of the Rolit game.
	 */
	public void stop() {
		mainGUI.dispose();
		if (started) {
			stopListening();
		}
	}
	
	/**
	 * Stops the server-side of the Rolit game, but the GUI remains so the
	 * server can restart.
	 */
	public void stopListening() {
		server.shutDown();
		clientmanager.shutDown();
		gameManager.shutDown();
	}

	public void startListening(int port) throws PortInUseException {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(port);
			socket.setSoTimeout(1);
			socket.accept();
		} catch (SocketTimeoutException e) {
			// this is good!
		} catch (IOException e) {
			throw new PortInUseException();
		} 
		clientmanager = new ClientManager(this, gameManager);
		server = new Server(this, clientmanager, socket);
		server.start();	
		started = true;
	}

	public synchronized void addMessage(Object source, String message) {
		if (source.equals(this)) {
			mainGUI.addMainMessage(message);
		} else if (source.equals(clientmanager)) {
			mainGUI.addClientManagerMessage(message);
		} else if (source.equals(gameManager)) {
			mainGUI.addGameManagerMessage(message);
		}
	}
	
	public static void main(String[] args) {
		Main main = new Main();
	}

}
