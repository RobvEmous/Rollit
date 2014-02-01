package server;

public class Main {
	
	private MainUI mainGUI;
	private ClientManager clientmanager;
	private GameManager gameManager;
	private Server server;
	
	private boolean started = false;
	
	public Main() {
		mainGUI = new MainUI(this);
		mainGUI.setVisible(true);
	}
	
	/**
	 * Stops the whole server-side of the Rolit game.
	 */
	public void stop() {
		mainGUI.dispose();
		if (started) {
			stopListening();
		}
	}
	
	/**
	 * Stops the server-side of the Rolit game, but the GUI remains so the
	 * server can restart.
	 */
	public void stopListening() {
		server.shutDown();
		clientmanager.shutDown();
		gameManager.shutDown();
	}

	public void startListening(int port) {
		gameManager = new GameManager(this);
		clientmanager = new ClientManager(this, gameManager);
		server = new Server(this, clientmanager, port);
		server.start();	
		started = true;
	}
	
	public synchronized void addMessage(Object source, String message) {
		if (source.equals(this)) {
			mainGUI.addMainMessage(message);
		} else if (source.equals(clientmanager)) {
			mainGUI.addClientManagerMessage(message);
		} else if (source.equals(gameManager)) {
			mainGUI.addGameManagerMessage(message);
		}
	}
	
	public static void main(String[] args) {
		Main main = new Main();
	}

}
