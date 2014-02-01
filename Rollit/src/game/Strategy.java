package game;

import java.awt.Point;

import clientAndServer.Ball;
import clientAndServer.Board;


public interface Strategy {
	public String getName();
	public Point determineMove(Board b, Ball ball);
}
