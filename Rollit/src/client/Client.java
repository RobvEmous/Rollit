package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The Client 
 * 
 * @author  Rob van Emous
 * @version v0.1
 */
public class Client extends Thread {

	private String clientName;
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;

	/**
	 * Constructs a Client-object and tries to make a socket connection
	 */
	public Client(String name, InetAddress host, int port) throws IOException {
		clientName = name;
		sock = new Socket(host, port);
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
	}

	/**
	 * Reads the messages in the socket connection. Each message will be forwarded to the MessageUI
	 */
	public void run() {
		try {
			while (true) {
				Command command = waitForCommand();
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
	private void sendMessage(String msg) throws IOException {
		try {
			out.write(msg + "\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	private void sendCommand(String command, String[] args) throws IOException {
		sendMessage(command + " " + Tools.ArrayToString(args));
	}


	/** returns the client name */
	public String getClientName() {
		return clientName;
	}
	
	/**
	 * 
	 * @param name
	 * @param password
	 * @return 0 if exception, 1 if wrong password, 2 if succes
	 */
	public int login(String name, String password) {
		String[] args = {password};
		try {
			sendCommand(name, args);
			Command answer = waitForCommand();
			return 2;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		
	}
	
	public boolean join(int nrOfPlayers) {
		return false;
		
	}
	
	public boolean challenge(String[] playerNames) {
		return false;
		
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
	
	/** close the socket connection. */
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
