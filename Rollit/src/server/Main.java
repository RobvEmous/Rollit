package server;

import java.util.Observable;
import java.util.Observer;

import clientAndServer.GlobalSettings;

public class Main implements Observer {
	
	private MainUI mainGUI;
	private Server server;
	
	public Main() {
		mainGUI = new MainUI(this);
		while (server == null) {
			try {
				Thread.sleep(GlobalSettings.SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		server.
	}
	

	public void exit() {
		
	}

	@Override
	public void update(Observable o, Object arg) {
		
	}
	
	public static void main(String[] args) {
		Main main = new Main();
	}


	public void startServer(int portArg) {
		server = new Server(this, portArg);
		server.start();	
	}

}
