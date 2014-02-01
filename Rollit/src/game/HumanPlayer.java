package game;

import clientAndServer.Ball;

/**
 * Abstract Class for maintaining a human player in the Rollit game.
 * 
 * @author Rob van Emous
 * @version v1.0
 */
public abstract class HumanPlayer extends GamePlayer {

	public HumanPlayer(String name, Ball ball) {
		super(name, ball);
	}
	
   
}
