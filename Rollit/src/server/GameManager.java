package server;

import java.util.ArrayList;
import java.util.HashMap;

import clientAndServer.GlobalSettings;
import clientAndServer.Tools;

import exceptions.RepeatedActionException;

/**
 * Class is thread-safe
 * @author Rob van Emous
 * @version 0.2
 */
public class GameManager {
	
	public static final int MAX_NR_OF_GAMEPLAYERS = 4;
	public static final int MIN_NR_OF_GAMEPLAYERS = 2;
	
	private HashMap<GamePlayer, Integer> waiters;
	private HashMap<ServerGame, GamePlayer[]> games;
	
	private boolean stop = false;


	public GameManager() {
		waiters = new HashMap<GamePlayer, Integer>();
		games = new HashMap<ServerGame, GamePlayer[]>();
		gameCreator();
	}
	
	private void gameCreator() {
		Thread gameCreator = new Thread(new Runnable() {		
			@Override
			public void run() {
				while (!stop) {
					for (int i = MIN_NR_OF_GAMEPLAYERS; i <= MAX_NR_OF_GAMEPLAYERS; i++) {
						ArrayList<GamePlayer> GamePlayers = getGamePlayers(i);
						if (waiters.size() >= i) {
							ServerGame game = new ServerGame(GamePlayers, GlobalSettings.THINK_TIME);
							game.start();
							games.put(game, );
							moveToGamePlayers(Tools.getFirstP(GamePlayers, i));
						}
					}
					Thread.sleep(20);
				}
			}

		});
		gameCreator.start();
	}

	/**
	 * Adds a GamePlayer the lobby to wait for other GamePlayers who want to play 
	 * against the same number of opponents.
	 * @param GamePlayer the GamePlayer waiting to play a game.
	 * @param nrOfGamePlayers the number of GamePlayers the GamePlayer wants to play 
	 * with (including himself).
	 * @throws RepeatedActionException
	 */
	public void addWaiter(GamePlayer GamePlayer, int nrOfGamePlayers) throws RepeatedActionException {
		synchronized (waiters) {
			if (waiters.containsKey(GamePlayer) || games.containsKey(GamePlayer)) {
				throw new RepeatedActionException();
			}
			waiters.put(GamePlayer, nrOfGamePlayers);
		}
	}
	
	private void moveWaitersToGamePlayers(ArrayList<GamePlayer> theWaiter) {
		
	}
	
	private void removeWaiters(ArrayList<GamePlayer> GamePlayers) {
		for (GamePlayer GamePlayer : GamePlayers) {
			removeWaiter(GamePlayer);
		}
	}
	
	
	
	public void removeWaiter(GamePlayer GamePlayer) {
		synchronized (waiters) {
			if (waiters.containsKey(GamePlayer)) {
				waiters.remove(GamePlayer);
			}
		}
	}
	
	public void addGame(ServerGame game, GamePlayer[] GamePlayers) {
		synchronized (games) {
			games.put(game, GamePlayers);
		}
	}
	
	/**
	 * Returns the GamePlayers which want to play this type of game.
	 * @param nrOfGamePlayers the number of GamePlayers of the game
	 */
	private ArrayList<GamePlayer> getGamePlayers(int nrOfGamePlayers) {
		ArrayList<GamePlayer> GamePlayers = new ArrayList<GamePlayer>();
		synchronized (waiters) {
			for (GamePlayer GamePlayer : waiters.keySet()) {
				if (waiters.get(GamePlayer) == nrOfGamePlayers) {
					GamePlayers.add(GamePlayer);
				}
			}
		}
		return GamePlayers;
	}
	
}
