package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import clientAndServer.Command;

/**
 * Server. A Thread class that listens to a socket connection on a 
 * specified port. For every socket connection with a Client, a new 
 * ClientHandler thread is started. 
 * @author  Rob van Emous
 * @version v0.3
 */
public class Server extends Thread implements Observer {
	private int port;
	private MessageUI mui;
	private Collection<ClientCommunicator> clients;


    /** Constructs a new Server object */
	public Server(int portArg, MessageUI muiArg) {
		port = portArg;
		mui = muiArg;
		clients = new ArrayList<ClientCommunicator>();
	}

	/**
	 * Listens to a port of this Server if there are any Clients that 
     * would like to connect. For every new socket connection a new
     * ClientHandler thread is started that takes care of the further
     * communication with the Client. 
	 */
	public void run() {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(port);
			while (true) {
				Socket s = socket.accept();
				ClientCommunicator c = new ClientCommunicator(this, s); 
				c.addObserver(this);
				addHandler(c);				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends a message using the collection of connected ClientHandlers
         * to all connected Clients.
	 * @param msg message that is send
	 */
	public synchronized void broadcast(String msg) {
		//@ TODO
		mui.addMessage(msg);
		for (ClientHandler handler : threads) {
			handler.sendMessage(msg);
		}
	}

	/**
	 * Add a ClientHandler to the collection of ClientHandlers.
	 * @param handler ClientHandler that will be added
	 */
	public void addHandler(ClientCommunicator handler) {
		clients.add(handler);
	}

	/**
	 * Remove a ClientHandler from the collection of ClientHanlders. 
	 * @param handler ClientHandler that will be removed
	 */
	public synchronized void removeHandler(ClientHandler handler) {
		clients.remove(handler);
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}

}
