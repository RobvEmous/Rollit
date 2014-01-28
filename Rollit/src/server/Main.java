package server;

import java.util.Observable;
import java.util.Observer;

public class Main implements Observer {
	
	private ServerGUI serverGUI;
	
	public Main() {
		serverGUI = new ServerGUI();
	}
	

	public void exit() {
		
	}

	@Override
	public void update(Observable o, Object arg) {
		
	}
	
	public static void main(String[] args) {
		Main main = new Main();
	}

}
