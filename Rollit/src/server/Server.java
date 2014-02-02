package server;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import clientAndServer.GlobalSettings;

/**
 * Server. A Thread class that listens to a socket connection on a 
 * specified port. For every socket connection with a Client, a new 
 * {@link ClientCommunicator} is created and this ClientCommunicator is 
 * passed to the {@link ClientManager}. 
 * @author  Rob van Emous
 * @version v1.0
 */
public class Server extends Thread {
	
	private boolean stop = false;
	private boolean stopped = false;
	
	private ServerSocket socket;
	private ClientManager manager;

    /** Constructs a new Server object */
	public Server(Main main, ClientManager manager, ServerSocket socket) {
		this.socket = socket;
		this.manager = manager;
	}

	/**
	 * Listens for Clients that would like to connect the port of this 
	 * Server. For every socket connection with a Client, a new 
	 * {@link ClientCommunicator} is created and this ClientCommunicator 
	 * is passed to the {@link ClientManager}.
	 */
	public void run() {
		try {
			socket.setSoTimeout(GlobalSettings.TIME_OUT);
			while (!stop) {
				try {
					Socket s = socket.accept();
					ClientCommunicator c = new ClientCommunicator(s); 
					manager.addGuest(c);	
				} catch (SocketTimeoutException e) {
					// The timeout is only used so the server can be 
					// stopped if necessary.
				}
			}
			socket.close();
			stopped = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void shutDown() {
		stop = true;
		waitForStopped();
	}

	private void waitForStopped() {
		while (!stopped) {
			try {
				Thread.sleep(GlobalSettings.SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
	}

}
