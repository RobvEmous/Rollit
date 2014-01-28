package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import clientAndServer.Command;
import clientAndServer.Tools;
import exceptions.ServerNotFoundException;

/**
 * The Client 
 * 
 * @author  Rob van Emous
 * @version v0.5
 */
public class Client extends Thread {

	private String clientName;
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	private ArrayList<Command> commands;
	private ArrayList<Command> answers;
	private static final int MAX_SIZE = 50;
	public static final String ACKNOWLEDGED = "Ack";
	public static final int PORT = 8080;

	/**
	 * Constructs a Client-object and tries to make a socket connection
	 */
	public Client(String name, InetAddress host, int port) throws ServerNotFoundException {
		clientName = name;	
		commands = new ArrayList<Command>();
		answers = new ArrayList<Command>();
		try {
			sock = new Socket(host, port);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServerNotFoundException();
		}
		
	}
	
	public Client(String name, int port) throws IOException {
		this(name, InetAddress.getLocalHost(), port);
	}
	
	public Client(String name, InetAddress host) throws IOException {
		this(name, host, PORT);
	}
	
	public Client(String name) throws IOException {
		this(name, PORT);
	}

	/** returns the client name */
	public String getClientName() {
		return clientName;
	}

	/**
	 * Reads the messages in the socket connection. 
	 */
	public void run() {
		try {
			while (true) {
				Command c = waitForCommand();
				if (c.getId().contains(ACKNOWLEDGED)) {
					synchronized (answers) {
						String id = c.getId();
						c.setId(id.substring(0, id.length() - ACKNOWLEDGED.length()));
						answers.add(c);
						if (answers.size() > MAX_SIZE) {
							removeOldestAnswer();
						}
					}
				} else {
					synchronized (commands) {
						commands.add(c);
						if (commands.size() > MAX_SIZE) {
							removeOldestCommand();
						}
					}
				}

			}	
		} catch (IOException e) {
			e.printStackTrace();
			shutdown();	
		} 
	}
	
	private String waitForLine() throws IOException {
		String line = null;
		while ((line = in.readLine()) == null) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return line;
	}
	
	private Command waitForCommand() throws IOException {
		String line = waitForLine();
		ArrayList<String> command = new ArrayList<String>();
		Scanner scanner = new Scanner(line);
		String first = "";
		if (scanner.hasNext()) {
			first = scanner.next();
		}
		while (scanner.hasNext()) {
			command.add(scanner.next());			
		}
		scanner.close();
		return new Command(first, (String[])command.toArray());
	}
	
	/** send a message to a ClientHandler. */
	private synchronized void sendMessage(String msg) throws IOException {
		try {
			out.write(msg + "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * 
	 * @param command
	 * @param args
	 * @throws IOException
	 */
	public void sendCommand(String command, String[] args) throws IOException {
		sendMessage(command + " " + Tools.ArrayToString(args));
	}
	
	/**
	 * Returns the list of commands from the server.
	 */
	public ArrayList<Command> getCommands() {
		synchronized (commands) {
			return commands;
		}		
	}
	
	/**
	 * Removes the specified command from the list.
	 * This should only be done after responding to this command.
	 * @param c the command to be removed
	 */
	public void removeCommand(Command c) {
		synchronized (commands) {
			commands.remove(c);
		}		
	}
	
	/**
	 * Removes the oldest command from the list (the one with index 0).
	 */
	public void removeOldestCommand() {
		synchronized (commands) {
			commands.remove(0);
		}	
	}
	
	/**
	 * Returns the list of answers from the server.
	 */
	public ArrayList<Command> getAnswers() {
		synchronized (answers) {
			return answers;
		}		
	}
	
	/**
	 * Removes the specified answer from the list.
	 * This should only be done after responding to this answer.
	 * @param c the command to be removed
	 */
	public void removeAnswers(Command c) {
		synchronized (answers) {
			answers.remove(c);
		}		
	}
	
	/**
	 * Removes the oldest answer from the list (the one with index 0).
	 */
	public void removeOldestAnswer() {
		synchronized (answers) {
			answers.remove(0);
		}	
	}

	/** 
	 * closes the socket connection. 
	 * */
	public void shutdown() {
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();	
		}
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
