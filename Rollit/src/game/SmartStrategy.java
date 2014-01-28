package game;

import java.awt.Point;
import java.util.*;

import clientAndServer.Ball;
import clientAndServer.Board;

public class SmartStrategy implements Strategy {

	private String name = "Smart"; 
	private Random rand;
	
	private int[][] ranks;
	
	public SmartStrategy() {
		rand = new Random();
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Point determineMove(Board board, Ball ball) {
		int highestRank = 0;
		ArrayList<Point> bestMoves = new ArrayList<Point>();
		ArrayList<Point> moves = board.getMoves(ball);
		for (Point move : moves) {
			int tempRank = board.getRank(move);
			if (tempRank > highestRank) {
				bestMoves.clear();
				bestMoves.add(move);
				highestRank = tempRank;
			} else if (tempRank == highestRank) {
				bestMoves.add(move);
			}
		}
		return bestMoves.get(rand.nextInt(bestMoves.size()));
	}
	
}
