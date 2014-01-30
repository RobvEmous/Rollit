package clientAndServer;

import client.Client;
import server.Server;
import exceptions.ProtecolNotFollowedException;

public final class GlobalSettings {
	/**
	 * Standard wait time when a thread wants to save resources
	 */
	public static final int SLEEP_TIME = 20; //ms
	
	/**
	 * {@link Server} and {@link Client} timeout time (ms). It waits for 
	 * for an answer for this amount of time after sending a 
	 * {@link Command} before throwing a 
	 * {@link ProtecolNotFollowedException}.
	 */
	public static final int TIME_OUT = 2000;
	public static final int SLEEP_COUNT = TIME_OUT / SLEEP_TIME;
	
	/**
	 * The time the user has available to make a move in an online game.
	 */
	public static final int THINK_TIME = 60000; //ms
	
	/**
	 * Standard portnr.
	 */
	public static final int PORT_NR = 8080;
	
	/**
	 * Max command and answer buffer size for a well the client as the server.
	 */
	public static final int MAX_SIZE = 50;

}
