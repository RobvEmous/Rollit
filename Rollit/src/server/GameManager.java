package server;

import java.util.ArrayList;
import java.util.HashMap;

import clientAndServer.Tools;

import exceptions.RepeatedActionException;

/**
 * Class is thread-safe
 * @author Rob van Emous
 * @version 0.2
 */
public class GameManager {
	
	public static final int MAX_NR_OF_PLAYERS = 4;
	public static final int MIN_NR_OF_PLAYERS = 2;
	
	private HashMap<Player, Integer> waiters;
	private ArrayList<ServerGame> games;
	
	private boolean stop = false;


	public GameManager() {
		waiters = new HashMap<Player, Integer>();
		games = new ArrayList<ServerGame>();
		gameCreator();
	}
	
	private void gameCreator() {
		Thread gameCreator = new Thread(new Runnable() {		
			@Override
			public void run() {
				while (!stop) {
					for (int i = MIN_NR_OF_PLAYERS; i <= MAX_NR_OF_PLAYERS; i++) {
						ArrayList<Player> players = getPlayers(i);
						if (players.size() >= i) {
							ServerGame game = new ServerGame(players);
							games.add(game);
							removeWaiters(Tools.getFirstP(players, i));
						}
					}
				}
			}
		});
		gameCreator.start();
	}

	/**
	 * Adds a player the lobby to wait for other players who want to play 
	 * against the same number of opponents.
	 * @param player the player waiting to play a game.
	 * @param nrOfPlayers the number of players the player wants to play 
	 * with (including himself).
	 * @throws RepeatedActionException
	 */
	public void addWaiter(Player player, int nrOfPlayers) throws RepeatedActionException {
		synchronized (waiters) {
			if (waiters.containsKey(player)) {
				throw new RepeatedActionException();
			}
			waiters.put(player, nrOfPlayers);
		}
	}
	
	private void removeWaiters(ArrayList<Player> players) {
		for (Player player : players) {
			removeWaiter(player);
		}
	}
	
	public void removeWaiter(Player player) {
		synchronized (waiters) {
			if (waiters.containsKey(player)) {
				waiters.remove(player);
			}
		}
	}
	
	private ArrayList<Player> getPlayers(int nrOfPlayers) {
		ArrayList<Player> players = new ArrayList<Player>();
		synchronized (waiters) {
			for (Player player : waiters.keySet()) {
				if (waiters.get(player) == nrOfPlayers) {
					players.add(player);
				}
			}
		}
		return players;
	}
	
}
