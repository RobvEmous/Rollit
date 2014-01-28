package game;

import java.awt.Point;

public class ComputerPlayer extends GamePlayer {

	private Strategy strategy;
	
	public ComputerPlayer(Ball ball) {
		this(ball, new NaiveStrategy());
	}	
	public ComputerPlayer(Ball ball, Strategy strategy) {
		super(strategy.getName() + "-AI", ball);
		this.strategy = strategy;
	}
	
	@Override
	public Point determineMove(Board board) {
		return strategy.determineMove(board, super.getBall());
	}
	
	@Override
	public String getName() {
		return super.getName();
	}
	
	public Strategy getStrategy() {
		return strategy;
	}
	
	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}
	
	public static void main(String[] args) {
		// only for testing purposes
	}

}
