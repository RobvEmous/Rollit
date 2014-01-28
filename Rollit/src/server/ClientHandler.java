package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import clientAndServer.Command;
import clientAndServer.Tools;

/**
 * ClientHandler.
 * @author  Rob van Emous
 * @version v0.2
 */
public class ClientHandler extends Thread {

	private Server server;
	private ClientCommunicator c;
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	private String clientName;
	
	private ArrayList<Command> commands;
	private ArrayList<Command> answers;
	private static final int MAX_SIZE = 50;
	public static final String ACKNOWLEDGED = "Ack";
	
	/**
	 * Constructs a ClientHandler object and initializes both Data streams.
	 */
	public ClientHandler(Server serverArg, Socket sockArg) throws IOException {
		server = serverArg;
		sock = sockArg;
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
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
	
	/** send a message to the ServerHandler. */
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
	 * Returns the list of commands from the client.
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
	 * Returns the list of answers from the client.
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
	
	public String getClientName() {
		return clientName;
	}

}
