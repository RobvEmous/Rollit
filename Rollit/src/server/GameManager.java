package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import clientAndServer.Ball;
import clientAndServer.Command;
import clientAndServer.Commands;
import clientAndServer.GlobalSettings;
import clientAndServer.Tools;

import exceptions.NotSameStateException;

/**
 * Class is thread-safe
 * @author Rob van Emous
 * @version 0.2
 */
public class GameManager implements Observer {
	
	public static final int MAX_NR_OF_GAMEPLAYERS = 4;
	public static final int MIN_NR_OF_GAMEPLAYERS = 2;
	
	private HashMap<ClientCommunicator, String> waitersName;
	private HashMap<ClientCommunicator, Integer> waitersGameKind;
	private ArrayList<ServerGame> games;
		
	private Main main;	
	private ClientManager clientManager;
	private ScoreRW scoreRW;
	
	private String waiterAdded = "Waiter added: ";
	private String waitersUpgraded = "Waiters started playing: ";
	private String waiterKicked = "Waiter kicked: ";
	private String waiterDisconnected= "Waiter disconnected: ";
	private String waiterDisjoined= "Waiter disjoined: ";
	private String gameStarted = "Game started with: ";
	private String gameStopped = "Game stopped with: ";
	private String playerStopped = "Player rage-quited: ";
	private String playerKicked = "Player kicked: ";
	private String playerDisconnected = "Player disconnected: ";
	
	private boolean stop = false;
	

	public GameManager(Main main, ClientManager clientManager, ScoreRW scoreRW) {
		this.main = main;
		this.clientManager = clientManager;
		this.scoreRW = scoreRW;
		waitersName = new HashMap<ClientCommunicator, String>();
		waitersGameKind = new HashMap<ClientCommunicator, Integer>();
		games = new ArrayList<ServerGame>();
		//gameCreator();
	}
	
	private void gameCreator() {
		Thread gameCreator = new Thread(new Runnable() {		
			@Override
			public void run() {
				while (!stop) {
					for (int i = MIN_NR_OF_GAMEPLAYERS; i <= MAX_NR_OF_GAMEPLAYERS; i++) {
						ArrayList<ClientCommunicator> players = getPlayers(i);
						if (players.size() >= i) {
							players = Tools.getFirstP(players, i);
							ArrayList<GamePlayer> gamePlayers = convertToGamePlayers(players);
							sendMessage(gameStarted + Tools.ArrayListToString(players));
							ServerGame game = new ServerGame(gamePlayers);
							
							game.addObserver(this);
							game.start();
						}
					}
					Thread.sleep(20);
				}
			}

			private ArrayList<GamePlayer> convertToGamePlayers(
					ArrayList<ClientCommunicator> players) {
				ArrayList<GamePlayer> gamePlayers = new ArrayList<GamePlayer>();
				Ball ball = Ball.RED;
				for (ClientCommunicator player : players) {
					gamePlayers.add(new GamePlayer(waitersName.get(player), player, ball));
					ball = ball.next();
				}
				return gamePlayers;
			}

		});
		gameCreator.start();
	}

	/**
	 * Adds a player the lobby to wait for other players who want to play 
	 * against the same number of opponents.
	 * @param client the client to add
	 * @param player the player(info) of this client
	 * @param nrOfGamePlayers the number of GamePlayers the GamePlayer wants to play 
	 * with (including himself).
	 */
	public void addWaiter(ClientCommunicator client, Player player, int nrOfGamePlayers)  {
		synchronized (waitersName) {
			synchronized (waitersGameKind) {
				waitersName.put(client, player.getName());
				waitersGameKind.put(client, nrOfGamePlayers);
			}
		}
	}
	
	public void addQuitter(GamePlayer player)  {
		sendMessage(playerStopped + player.toString());
	}
	
	public void addKicked(GamePlayer player)  {
		sendMessage(playerKicked + player.toString());
	}
	
	public void addDisconnected(GamePlayer player)  {
		sendMessage(playerDisconnected + player.toString());
	}
	
	public void addDonePlaying(GamePlayer player)  {
		sendToClientManager(player);
	}
	
	private void sendToClientManager(GamePlayer player) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Removes certain waiters from the 'waiting room'.
	 * @param waiters the waiters to remove
	 * @param reason 0 = kicked, 1 = disconnected, 2 = disjoined.
	 */
	private void removeWaiters(ArrayList<ClientCommunicator> waiters, int reason) {
		for (ClientCommunicator waiter : waiters) {
			removeWaiter(waiter, reason);
		}
	}
	
	/**
	 * Removes a waiter from the 'waiting room'.
	 * @param waiter the waiter to remove
	 * @param reason 0 = kicked, 1 = disconnected, 2 = disjoined.
	 */
	private void removeWaiter(ClientCommunicator waiter, int reason) {
		String theReason = "";
		switch (reason) {
		case 0:
			theReason = waiterKicked;
			break;
		case 1:
			theReason = waiterDisconnected;
			break;
		case 2:
			theReason = waiterDisjoined;
			break;
		}
		sendMessage(theReason + waiterInfo(waiter));
		waiter.deleteObserver(this);
		synchronized (waitersName) {
			synchronized (waitersGameKind) {
				waitersName.remove(waiter);
				waitersGameKind.remove(waiter);
			}
		}
	}
	
	public void addGame(ServerGame game, GamePlayer[] GamePlayers) {
		synchronized (games) {
			games.add(game);
		}
	}
	
	/**
	 * Returns the players which want to play this type of game.
	 * @param nrOfGamePlayers the number of GamePlayers of the game
	 */
	private ArrayList<ClientCommunicator> getPlayers(int nrOfGamePlayers) {
		ArrayList<ClientCommunicator> players = new ArrayList<ClientCommunicator>();
		synchronized (waitersGameKind) {
			for (ClientCommunicator player : waitersGameKind.keySet()) {
				if (waitersGameKind.get(player) == nrOfGamePlayers) {
					players.add(player);
				}
			}
		}
		return players;
	}

	private String waiterInfo(ClientCommunicator client) {
		return waitersName.get(client) + " - " + waitersGameKind.get(client);
	}
	
	private void sendMessage(String text) {
		main.addMessage(this, text);
	}
	
	public void shutDown() {
		stop = true;
		scoreRW.close();	
		for (ClientCommunicator client : waitersName.keySet()) {
			client.deleteObserver(this);
			client.shutdown();
		}
		for (ServerGame game : games) {
			game.deleteObserver(this);
			game.shutdown();
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		ClientCommunicator client = null;
		ServerGame game = null;
		try {
			client = (ClientCommunicator) o;
		} catch (ClassCastException e) {
			try {
				game = (ServerGame) o;
			} catch (ClassCastException e1) {
				e.printStackTrace();
			}
		}
		if (client != null && waitersName.containsKey(client)) {
			Command comm = (Command) arg;
			if  (comm.getId().equals(Commands.COM_DISJOIN)) {
				try {
					client.sendAck(comm.getId(), Commands.ANS_GEN_GOOD);
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			} else {
				try {
					client.sendAck(comm.getId(), Commands.ANS_GEN_BAD);
				} catch (IOException e) {
					removeWaiter(client, 0);
					e.printStackTrace();
				}
			}
		} else if (game != null && games.contains(o)) {
			HighScore highScore = (HighScore) arg;
			scoreRW.addHighScore(highScore);
		}
		
	}
	
}
