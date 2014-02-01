package clientAndServer;

public class GlobalData {
	
	public static final String MSG_WAITING_J = ", Join succesfull!.\n" +
			"Now you must wait for enough players who want to play the " +
			"same game to connect to the server.";
	public static final String MSG_WAITING_C = ", Challenge succesfull!.\n" +
			"Now you must wait for the right players to connect to the server.";
	
	public static final String ERR_PROTECOL = ", the server does not respond (appropriately).\n" +
			"Either the server is offline, or it uses a different communication protocol.";
	public static final String ERR_STATE = ", the server responds that it has kicked this client because " +
			"it is not in the same state as the client.\n" +
			"This should never happen, so I hope Mr. Lennard does not see this, EVER";
	public static final String ERR_CLIENT_CONNECTION = ", I cannot connect to the server.\n" +
			"Either the server is offline, or a wrong adress or port have been used";
	public static final String ERR_CLIENT_TO_SLOW = ", you have not performed a move fast enough.\n" +
			"You will skip this turn and a random move has been performed for you.";
	
	private static final String PLAYER_HUMAN = "Human";
	private static final String PLAYER_AI_NAIVE = "Stupid PC";
	private static final String PLAYER_AI_SMART = "Smart PC";
	private static final String PLAYER_AI_SMARTER = "Smarter PC";
	/**
	 * String array containing all possible players
	 */
	public static final String[] PLAYERS = {PLAYER_HUMAN, PLAYER_AI_NAIVE, PLAYER_AI_SMART, PLAYER_AI_SMARTER};

	
}
