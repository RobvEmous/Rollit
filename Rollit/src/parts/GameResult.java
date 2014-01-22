package parts;

import java.util.HashMap;
import java.util.Map;

import client.Board;
import client.GamePlayer;

public class GameResult {

	Map<GamePlayer,Integer> result = null;
	
	public GameResult(Map<GamePlayer,Integer> players, int[] scores) {
		for ()
		result = new HashMap<GamePlayer, Integer>(players.length);
		for (int i = 0; i < result.size(); i++) {
			result.put(players[i], scores[i]);
		}
	}

}
