package server;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

/**
 * <h1>Class for reading and writing save-files for the Rollit game.</h1>
 * The class serves two mayor purposes:<br>
 * Firstly it can be used to manage
 * the accounts of Players and secondly it can be used to manage the 
 * (high)scores.<br> It can only be used to read and write save-files and
 * not be to search in the files or data. this has to be done by a class which
 * uses the runtime data from this class.
 * 
 * @author Rob van Emous
 * @version 0.7
 */
public class PlayerRW {
	
	private static final String ADMIN_PASS = "verysafe";	
	private String filePath = "files\\";
	private String fileName = "ppwd.dat"; // player password data
	private File file;
	
	private FileWriter out;
	private ArrayList<Player> players = null;
	
	public PlayerRW() {
		file = new File(filePath + fileName);
		players = new ArrayList<Player>();
	}
	
	public void open() {
		if (file.exists()) {
			readFile();
		}
	}
	
	public void close() {
		writeFile();	
		players.clear();
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}
	
	private void writeFile() {
		try {
			out = new FileWriter(file, false);
			for (Player player : players) {
				out.write(player.toString() 
							+ " " 
							+ player.getPassword().getPass(ADMIN_PASS)
							+ "\r\n");
				out.flush();
			}	
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readFile() {
		try {
			Scanner lines = new Scanner(file);
			Scanner words = null;
			if (lines.hasNextLine()) {
				words = new Scanner(lines.nextLine());	
			}
			while (words != null) {
				String name = words.next();
				Password pass = new Password();
				pass.setPassword(Password.INITIAL, words.next());
				players.add(new Player(name, pass));
				if (lines.hasNextLine()) {
					words = new Scanner(lines.nextLine());	
				} else {
					break;
				}
			}
			lines.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean writeNewPlayer(Player p) {
		for (Player player : players) {
			if (player.getName().equalsIgnoreCase(p.getName())) {
				return false;
			}
		}
		players.add(p);
		return true;
	}
	
	public boolean changePassword(Player p, String oldPass, String newPass) {
		return (p.getPassword().acceptable(newPass) && p.getPassword().setPassword(oldPass, newPass));
	}
	
	public static void main(String[] args) {
		PlayerRW playerRW = new PlayerRW();
		playerRW.open();
		Password henk = new Password();
		henk.setPassword(Password.INITIAL, "lolligevent");
		playerRW.writeNewPlayer(new Player("Henk", henk));
		Password peter = new Password();
		peter.setPassword(Password.INITIAL, "perenboom");
		playerRW.writeNewPlayer(new Player("Peter", peter));
		playerRW.close();
		playerRW.open();
		Password hanz = new Password();
		hanz.setPassword(Password.INITIAL, "zonderhaaropznhoofd");
		playerRW.writeNewPlayer(new Player("Hanz", hanz));
		playerRW.close();
		playerRW.open();
		ArrayList<Player> myPlayers = playerRW.getPlayers();
		for (Player player : myPlayers) {
			System.out.println("Player: " + player.getName() + ", Password: " + player.getPassword().getPass(ADMIN_PASS));
		}
	}
		
}
