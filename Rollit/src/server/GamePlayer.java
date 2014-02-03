package server;

import clientAndServer.Ball;

/**
 * Represents a server Player in the Rollit game.
 * @author Rob van Emous
 */
public class GamePlayer {

	private String name;
    private Ball ball;
    private ClientCommunicator client;
    
	/**
	 * Creates a new server-side game player object.
	 * 
	 * @param client the client this player is connected to
	 * @param ball the Ball of the Player
	 */
    public GamePlayer(String name, ClientCommunicator client, Ball ball) {
    	this.name = name;
    	this.client = client;
        this.ball = ball;
    }
    
    /**
     * Returns the name of the player.
     */
    public String getName() {
		return name;
	}

    /**
     * Returns the ball of the player.
     */
    public Ball getBall() {
        return ball;
    }
    
    /**
     * Sets the ball of the player.
     * @param ball the ball of the player
     */
    public void setBall(Ball ball) {
        this.ball = ball;
    }
    
    /**
     * Returns the {@link ClientCommunicator} object associated with this 
     * player.
     */
    public ClientCommunicator getClient() {
		return client;
	}
    
    @Override
    public String toString() {
    	return name + " (" + ball + ") ";
    }

}
