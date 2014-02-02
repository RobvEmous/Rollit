package server;

import java.util.HashMap;

public class HighScore {
	
	private int nrOfPlayers;
	private HashMap<GamePlayer, Integer> scores;
	
	public HighScore(int nrOfPlayers) {
		this.nrOfPlayers = nrOfPlayers;
		scores = new HashMap<GamePlayer, Integer>(nrOfPlayers);
	}
	
	public int getNrOfPlayers() {
		return nrOfPlayers;
	}
	
	public void setScores(HashMap<GamePlayer, Integer> scores) {
		this.scores = scores;
	}
	
	public void addScore(GamePlayer player, int score) {
		scores.put(player, score);
	}
	
	public HashMap<GamePlayer, Integer> getScores() {
		return scores;
	}
}
