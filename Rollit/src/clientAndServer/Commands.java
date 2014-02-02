package clientAndServer;

/**
 * All commands as has been agreed upon by the work group.
 * 
 * @author Rob van Emous
 * @version 1.0
 */
public final class Commands {
	
	/**
	 * General sign to indicate a command has been understood 
	 * (not necessarily accepted).
	 */
	public static final String COM_ACK = "Ack";

	// commands from the client to the server
	/**
	 * Used to log in to the Server.<br>
	 * Client --> Server.
	 */
	public static final String COM_LOGIN = "login";
	
	/**
	 * Used to join the 'waiting room' to play a game.<br>
	 * Client --> Server.
	 */
	public static final String COM_JOIN = "join";
	
	/**
	 * Used to challenge another client to play a game.<br>
	 * Client --> Server --> Client2.
	 */
	public static final String COM_CHALLENGE = "challenge";
	
	/**
	 * Used to leave the 'waiting room' to play a game.<br>
	 * Client --> Server.
	 */
	public static final String COM_DISJOIN = "disjoin";
	
	/**
	 * Used to send another player a chat message.<br>
	 * Client --> Server --> All other Clients of a game.
	 */
	public static final String COM_CHAT = "chat";
	
	/**
	 * Used to perform a move in a game.<br>
	 * Client --> Server.
	 */
	public static final String COM_MOVE = "move";
	
	/**
	 * Used to give up while playing a game.<br>
	 * Client --> Server.
	 */
	public static final String COM_QUIT = "quitGame";
	
	/**
	 * Used to obtain high-scores.<br>
	 * Client --> Server --> Client.<br>
	 */
	public static final String COM_HIGHSCORES = "getHighScores";
	
	/**
	 * Used to log out from the Server.<br>
	 * Client --> Server.
	 */
	public static final String COM_LOGOUT= "logOut";

	// commands from the server to the client
	/**
	 * Used to signal the Client a new game has started.<br>
	 * Server --> Client.
	 */
	public static final String COM_NEWGAME = "newGame";
	
	/**
	 * Used to send the Client a new chat message.<br>
	 * Server --> Client.
	 */
	public static final String COM_MESSAGE = "message";
	
	/**
	 * Used to send the Client a move of another player.<br>
	 * Server --> Client.
	 */
	public static final String COM_UPDATE = "update";
	
	/**
	 * Used to signal the Client that it is his turn.<br>
	 * Server --> Client.
	 */
	public static final String COM_YOURTURN = "yourTurn";
	
	/**
	 * Used to signal the Client that he has taken to long to perform a 
	 * move.<br>
	 * Server --> Client.
	 */
	public static final String COM_MOVETOOSLOW = "moveTooSlow";
	
	/**
	 * Used to signal the Client that another player has quit the 
	 * game.<br>
	 * Server --> Client.
	 */
	public static final String COM_PLAYERQUIT = "playerQuit";
	
	/**
	 * Used to signal the Client that the game is over.<br>
	 * Server --> Client.
	 */
	public static final String COM_GAMEOVER = "gameOver";
	
	// answers from the server to the client
	/**
	 * Used to signal the Client that he has send a legal command.<br>
	 * Server --> Client.
	 */
	public static final String ANS_GEN_GOOD = "ok";
	
	/**
	 * Used to signal the Client that he has send an illegal command 
	 * and he will be kicked.<br>
	 * Server --> Client.
	 */
	public static final String ANS_GEN_BAD = "kick";
	
	/**
	 * Used to signal the Client that he has logged in successful.<br>
	 * Server --> Client.
	 */	
	public static final String ANS_LOGIN_GOOD = "welcome";
	
	/**
	 * Used to signal the Client that he has <b>not</b> logged in 
	 * successful.<br>
	 * Server --> Client.
	 */	
	public static final String ANS_LOGIN_BAD = "incorrect";
	
}
