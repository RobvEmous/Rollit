package server;

import clientAndServer.Password;

public class Player {

	private String name;
	private Password pass;
	
	public Player(String name, Password pass) {
		this.name = name;
		this.pass = pass;
	}
	
	public String getName() {
		return name;
	}
	
	public Password getPassword() {
		return pass;
	}
	
	public String toString() {
		return name;
	}

}
