package game;

import java.awt.Point;
import java.util.*;

import clientAndServer.Ball;
import clientAndServer.Board;


public class NaiveStrategy implements Strategy {

	private String name = "Naive"; 
	private Random rand;
	
	public NaiveStrategy() {
		rand = new Random();
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Point determineMove(Board board, Ball ball) {
		ArrayList<Point> moves = board.getMoves(ball);
		return moves.get(rand.nextInt(moves.size()));
	}
	
}
