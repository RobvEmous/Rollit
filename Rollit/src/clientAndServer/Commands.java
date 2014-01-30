package clientAndServer;

public final class Commands {
	// general sign to indicate a command has been understood
	public static final String COM_ACK = "Ack";

	// commands from the client to the server
	public static final String COM_LOGIN = "login";
	public static final String COM_LOGIN_G = "welcome";
	public static final String COM_LOGIN_B = "incorrect";
	
	public static final String COM_JOIN = "join";
	
	public static final String COM_CHALLENGE = "challenge";
	
	public static final String COM_DISJOIN = "disjoin";
	
	public static final String COM_CHAT = "chat";
	public static final String COM_CHAT_G = "received";
	public static final String COM_CHAT_B = "error";
	
	public static final String COM_MOVE = "move";
	public static final String COM_MOVE_G = "ok";
	public static final String COM_MOVE_B = "kick";
	
	public static final String COM_PLAYERQUIT = "playerQuit";
	
	public static final String COM_QUIT = "quitGame";
	
	public static final String COM_HIGHSCORES = "getHighScores";
	
	public static final String COM_LOGOUT= "logOut";

	// commands from the server to the client
	public static final String COM_NEWGAME = "newGame";
	
	public static final String COM_MESSAGE = "message";
	
	public static final String COM_UPDATE = "update";
	
	public static final String COM_YOURTURN = "yourTurn";
	
	public static final String COM_GAMEOVER = "gameOver";
}
