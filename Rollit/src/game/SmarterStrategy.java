package game;

import java.awt.Point;
import java.util.*;

import clientAndServer.Ball;
import clientAndServer.Board;

public class SmarterStrategy implements Strategy {

	private String name = "Smarter"; 
	private Random rand;
	
	private int[][] ranks;
	
	public SmarterStrategy() {
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
		int bestScore = 0;
		ArrayList<Point> bestMoves2 = new ArrayList<Point>();
		int oldOcc = board.countInstancesOf(ball);
		for (Point p : bestMoves) {
			Board b = board.deepCopy();
			b.setField(p, ball);
			int score = b.countInstancesOf(ball) - oldOcc;
			if (score > bestScore) {
				bestMoves2.clear();
				bestMoves2.add(p);
				bestScore = score;
			} else if (score == bestScore) {
				bestMoves2.add(p);
			}
		}
		return bestMoves2.get(rand.nextInt(bestMoves2.size()));
	}
	
}
