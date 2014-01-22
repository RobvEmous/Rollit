package client;

import java.awt.Point;

public interface Strategy {
	public String getName();
	public Point determineMove(Board b, Ball ball);
}
