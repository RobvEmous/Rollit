package parts;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import server.Player;

public class Score {
	private Player player;
	private long time = 0;
	private int points = 0;
	private int fields = 0;
	
	//SimpleDateFormat h = new  SimpleDateFormat(arg0)

	public Score(Player thePlayer, int nrOfPlayers, int yy) {
		time = System.currentTimeMillis();
		player = thePlayer;
		
		points = thePoints;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Date getDate() {
		return null;
	}
	
	public int getDay() {
		return Calendar.DAY_OF_WEEK;
	}
	
	public long getTime() {
		return (Long) null;
	}
	
	public int getPoints() {
		return points;
	}
	
	@Override
	public String toString() {
		return getDate().getTime() + ": " + getPlayer().toString() + ": " + getPoints();
	}
	
	public static void main(String[] args) {
		Score score = new Score(new Player("Peter"), 5);
		System.out.println(score.toString());
	}

}
