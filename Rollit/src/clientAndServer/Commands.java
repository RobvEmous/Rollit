package clientAndServer;

public final class Commands {
	
	/**
	 * General sign to indicate a command has been understood 
	 * (not necessarily accepted).
	 */
	public static final String COM_ACK = "Ack";

	// commands from the client to the server
	public static final String COM_LOGIN = "login";
	
	public static final String COM_JOIN = "join";
	
	public static final String COM_CHALLENGE = "challenge";
	
	public static final String COM_DISJOIN = "disjoin";
	
	public static final String COM_CHAT = "chat";
	
	public static final String COM_MOVE = "move";

	public static final String COM_QUIT = "quitGame";
	
	public static final String COM_HIGHSCORES = "getHighScores";
	
	public static final String COM_LOGOUT= "logOut";

	// commands from the server to the client
	public static final String COM_NEWGAME = "newGame";
	
	public static final String COM_MESSAGE = "message";
	
	public static final String COM_UPDATE = "update";
	
	public static final String COM_YOURTURN = "yourTurn";
	
	public static final String COM_MOVETOOSLOW = "moveTooSlow";
	
	public static final String COM_PLAYERQUIT = "playerQuit";
	
	public static final String COM_GAMEOVER = "gameOver";
	
	// answers from the server to the client
	public static final String ANS_GEN_GOOD = "ok";
	public static final String ANS_GEN_BAD = "kick";
	
	public static final String ANS_LOGIN_GOOD = "welcome";
	public static final String ANS_LOGIN_BAD = "incorrect";
	
}
