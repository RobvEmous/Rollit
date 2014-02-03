package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import javax.sound.sampled.Port;

import clientAndServer.Command;
import clientAndServer.Commands;
import clientAndServer.Tools;

/**
 * ClientHandler.
 * @author  Rob van Emous
 * @version v0.2
 */
public class ClientHandler extends Thread {

	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	
	private ArrayList<Command> commands;
	private ArrayList<Command> answers;
	private static final int MAX_SIZE = 50;
	
	private volatile boolean stop = false;
	
	/**
	 * Constructs a ClientHandler object and initializes both Data streams.
	 */
	public ClientHandler(Socket sockArg) throws IOException {
		sock = sockArg;
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		commands = new ArrayList<Command>();
		answers = new ArrayList<Command>();
	}
	
	/**
	 * Reads the messages in the socket connection. 
	 */
	public void run() {
		try {
			while (!stop) {
				Command c = waitForCommand();
				if (c.getId().contains(Commands.COM_ACK)) {
					synchronized (answers) {
						String id = c.getId();
						c.setId(id.substring(0, id.length() - Commands.COM_ACK.length()));
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
		while (!stop && (line = in.readLine()) == null) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (stop) {
			line = "";
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
		return new Command(first, (String[])command.toArray(new String[command.size()]));
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
	 * Removes all commands from the list.
	 * This should only be done after responding to all these commands.
	 */
	public void removeAllCommands() {
		synchronized (commands) {
			commands.clear();
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
	 * Removes all answers from the list.
	 * This should only be done after reading all these answers.
	 */
	public void removeAllAnswers() {
		synchronized (answers) {
			answers.clear();
		}		
	}
	
	/**
	 * Removes the specified answer from the list.
	 * This should only be done after reading this answer.
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
		stop = true;
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return sock.getPort() + "";
	}

}
