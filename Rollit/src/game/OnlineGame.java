package game;

import java.awt.Point;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import server.Player;

import client.Main;
import client.ServerCommunicator;
import clientAndServer.Ball;
import clientAndServer.Board;
import clientAndServer.Command;
import clientAndServer.Commands;
import clientAndServer.GlobalData;
import clientAndServer.GlobalSettings;
import exceptions.NotSameStateException;
import exceptions.ProtocolNotFollowedException;

/**
 * Class for maintaining the Rollit game.
 * 
 * @author Rob van Emous
 * @version 0.1
 */
public class OnlineGame implements Observer {
	
	/**
	 * The main menu.
	 */
	private Main main;

    /**
     * The board of the game.
     */
    private Board board;
    
    /**
     * The GUI of the game.
     */
    private OnlineGameUI gameUI;

    /**
     * The number of players of the game (2-4).
     */
    private int nrOfPlayers = 0;
    
    /**
     * The client-player of the game.
     */
    private GamePlayer clientPlayer;
    
    /**
     * Whether this game has a human client or not
     */
    private boolean hasHuman;
    
    private boolean clientHasTurn = false;
    
    /**
     * The server-players of the game.
     */
    private ArrayList<GamePlayer> serverPlayers;
    
    private ServerCommunicator sc;
    
    /**
     * Creates a new Game.
     * 
     * @param thePlayers the players
     */
    public OnlineGame(Main main, ServerCommunicator sc, GamePlayer clientPlayer, String[] serverPlayerNames) {
    	this.main = main;
    	this.sc = sc;
    	sc.addObserver(this);
    	nrOfPlayers = serverPlayerNames.length + 1;
        board = new Board();
        this.clientPlayer = clientPlayer;
        serverPlayers = new ArrayList<GamePlayer>();
        createServerPlayers(serverPlayerNames);
        board.setInitial();
        hasHuman = initHasHuman();
    }
    
    private void createServerPlayers(String[] serverPlayerNames) {
    	Ball ball = Ball.RED;
		for (String serverPlayerName : serverPlayerNames) {
			serverPlayers.add(new GamePlayer(serverPlayerName, ball.next()) {				
				@Override
				public Point determineMove(Board board) {
					return null;
				}
			});
		}	
	}

	/**
     * Starts the online Rollit game. <br>
     * First the initial board is shown (the four center balls have
     * already been set).<br> Then the game is played until the server
     * signals the game is over.<br> 
     * After each move, the changed game situation is printed.
     */
    public void start() {
    	sc.addObserver(this);
        gameUI = new OnlineGameUI(this, hasHuman);
        gameUI.update(board);
    }

	/**
     * Prints the game situation.
     */
    private void updateScreen() {
   		gameUI.update(board);
    }
    
    private int getNumberOfPlyers() {
    	return nrOfPlayers;
    }
    
    public GamePlayer getClient() {
    	return clientPlayer;
    }
    
    private GamePlayer getServerPlayer(String name) {
    	for (GamePlayer serverPlayer : serverPlayers) {
    		if (serverPlayer.getName().equals(name)) {
    			return serverPlayer;
    		}
    	}
    	return null;
    }
    
    public GamePlayer[] getServerPlayers() {
    	GamePlayer[] players = new GamePlayer[serverPlayers.size()];
    	for (int i = 0; i < players.length; i++) {
    		players[i] = serverPlayers.get(i);
    	}
    	return players;
    }
        
    public boolean ClientHasturn() {
    	return clientHasTurn;
    }
    
    /**
     * Returns the winning player or null if it is a draw.
     */
    public GamePlayer getWinner() {
    	GamePlayer player = null;
    	Ball winner = board.getWinner();
    	if (winner != null) {
    		if (clientPlayer.equals(winner)) {
    			player = clientPlayer;
    		} else {
    	    	for (GamePlayer serverPlayer : serverPlayers) {
    	    		if (serverPlayer.equals(winner)) {
    	    			player = serverPlayer;
    	    		}
    	    	}
    		}

    	}
    	return player;
    }
    
    /**
     * Returns the 'drawing' players or null if a player has won.
     */
    public ArrayList<GamePlayer> getDrawers() {
    	ArrayList<Ball> drawers = board.getDrawers();
    	ArrayList<GamePlayer> players = new ArrayList<GamePlayer>();
    	if (drawers != null) {
    		if (drawers.contains(clientPlayer.getBall())) {
    			players.add(clientPlayer);
    		} else {
    	    	for (GamePlayer serverPlayer : serverPlayers) {
    	    		if (drawers.contains(serverPlayer.getBall())) {
    	    			players.add(serverPlayer);
    	    		}
    	    	}
    		}
    	}
    	return players;
    }
    
    public boolean hasHuman() {
    	return hasHuman;
    }
    
    private boolean initHasHuman() {
    	return (clientPlayer instanceof HumanPlayer);
    }
    
    public Point getHint() {
    	return board.getHint(clientPlayer.getBall());
    }
    
	public void goBack(boolean kicked) {
		gameUI.dispose();
		sc.deleteObserver(this);
		main.returnFromAction();
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o.equals(sc)) {
			Command comm = (Command) arg;
			if (comm.getId().equals(Commands.COM_YOURTURN)) {
				try {
					sc.sendAck(comm.getId(), new String(""));
					clientHasTurn = true;
					performClientMove();
				} catch (IOException e) {
					gameUI.addPopup("Send Ack", main.getClientName() + GlobalData.ERR_CLIENT_CONNECTION, true);
					goBack(false);
					e.printStackTrace();
				}
			} else if (comm.getId().equals(Commands.COM_MOVETOOSLOW)) {
				try {
					sc.sendAck(comm.getId(), new String(""));
					clientHasTurn = false;
					gameUI.addPopup("To slow", main.getClientName() + ", you have taken too log to perform a move.\n" +
							"A forced move has been send from the server.", true);
					String[] newArgs;
					try {
						newArgs = new String[]{main.getClientName(), comm.getArgs()[0], comm.getArgs()[1]};
					} catch (ArrayIndexOutOfBoundsException e) {
						throw new ProtocolNotFollowedException();
					}
					Command newComm = new Command(Commands.COM_MOVE, newArgs);
					performServerMove(newComm, true);
					updateScreen();
				} catch (IOException e) {
					gameUI.addPopup("Send Ack", main.getClientName() + GlobalData.ERR_CLIENT_CONNECTION, true);
					goBack(false);
					e.printStackTrace();
				} catch (ProtocolNotFollowedException e) {
					gameUI.addPopup("Forced Move", main.getClientName() + GlobalData.ERR_PROTECOL, true);
					goBack(false);
					e.printStackTrace();
				}	
			} else if (comm.getId().equals(Commands.COM_UPDATE)) {
				try {
					sc.sendAck(comm.getId(), new String(""));
					performServerMove(comm, false);
					updateScreen();
				} catch (IOException e) {
					gameUI.addPopup("Send Ack", main.getClientName() + GlobalData.ERR_CLIENT_CONNECTION, true);
					goBack(false);
					e.printStackTrace();
				} catch (ProtocolNotFollowedException e) {
					gameUI.addPopup("Server Move", main.getClientName() + GlobalData.ERR_PROTECOL, true);
					goBack(false);
					e.printStackTrace();
				}	
			} else if (comm.getId().equals(Commands.COM_MESSAGE)) {
				try {
					sc.sendAck(comm.getId(), new String(""));
					receiveChatMessage(comm);
				} catch (IOException e) {
					gameUI.addPopup("Send Ack", main.getClientName() + GlobalData.ERR_CLIENT_CONNECTION, true);
					goBack(false);
					e.printStackTrace();
				} catch (ProtocolNotFollowedException e) {
					gameUI.addPopup("Receive chat message", main.getClientName() + GlobalData.ERR_PROTECOL, true);
					goBack(false);
					e.printStackTrace();
				}
			} else if (comm.getId().equals(Commands.COM_PLAYERQUIT)) {
				try {
					sc.sendAck(comm.getId(), new String(""));
					removePlayer(comm);
					updateScreen();
				} catch (IOException e) {
					gameUI.addPopup("Send Ack", main.getClientName() + GlobalData.ERR_CLIENT_CONNECTION, true);
					goBack(false);
					e.printStackTrace();
				} catch (ProtocolNotFollowedException e) {
					gameUI.addPopup("Remove player", main.getClientName() + GlobalData.ERR_PROTECOL, true);
					goBack(false);
					e.printStackTrace();
				}	

			} else if (comm.getId().equals(Commands.COM_GAMEOVER)) {
				try {
					sc.sendAck(comm.getId(), new String(""));
					gameOver();
					updateScreen();
				} catch (IOException e) {
					gameUI.addPopup("Send Ack", main.getClientName() + GlobalData.ERR_CLIENT_CONNECTION, true);
					goBack(false);
					e.printStackTrace();
				} catch (ProtocolNotFollowedException e) {
					gameUI.addPopup("Game over", main.getClientName() + GlobalData.ERR_PROTECOL, true);
					goBack(false);
					e.printStackTrace();
				}	
			}
		}
	}
	
	private void gameOver() throws ProtocolNotFollowedException {
		Board tempBoard = board.deepCopy();
		removeQuitersFromBoard(tempBoard);
		String message = "";
		if (nrOfPlayers == 1 || tempBoard.gameOver()) {
			if (tempBoard.hasWinner()) {
				GamePlayer winner = getWinner();
				int points = tempBoard.countInstancesOf(winner.getBall());
				if (winner.equals(clientPlayer)) {
					message = ", you have won!\n" +
							"You have got: " + points + " points.";	
				} else  {	
					message = ", you have lost :(\n" +
							winner.getName() + " has won\n" +
							"He has got: " + points + " points.";	
				}
			} else {
				ArrayList<GamePlayer> drawers = getDrawers();
				int points = tempBoard.countInstancesOf(drawers.get(0).getBall());
				if (drawers.contains(clientPlayer)) {
					message = ", you have a draw-win with: \n";
					if (drawers.size() == 2) {
						for (GamePlayer player : drawers) {
							if (!player.equals(clientPlayer)) {
								message += player.getName() + ".\n";
							}
						}
					} else {
						if (!drawers.get(0).equals(clientPlayer)) {
							message += drawers.get(0).getName() + " and\n";
							if (!drawers.get(1).equals(clientPlayer)) {
								message += drawers.get(1).getName() + ".\n";
							} else {
								message += drawers.get(2).getName() + ".\n";
							}
						} else {
							message += drawers.get(1).getName() + " and\n";
							message += drawers.get(2).getName() + ".\n";
						}
					}
					message += "You all have got: " + points + " points.";	
				} else  {
					message += ", you have draw-lost to: ";
					if (drawers.size() == 2) {
						message += drawers.get(0).getName() + " and ";
						message += drawers.get(1).getName() + ".";
					} else {
						message += drawers.get(0).getName() + ", ";
						message += drawers.get(0).getName() + " and ";
						message += drawers.get(2).getName() + ".";
					}
				}
			}
		} else {
			throw new ProtocolNotFollowedException();
		}
		gameUI.addPopup("Game over", main.getClientName() + message, false);
		gameUI.gameOver(main.getClientName() + message);
		
	}

	private Board removeQuitersFromBoard(Board tempBoard) {
		ArrayList<Ball> badBalls = new ArrayList<Ball>();
		for (Ball ball : Ball.values()) {
			badBalls.add(ball);
			for (GamePlayer serverPlayer : serverPlayers) {
				if (serverPlayer.getBall().equals(ball)) {
					badBalls.remove(ball);
				}
			}
		}
		for (Ball ball : badBalls) {
			tempBoard.removeBallFromBoard(ball);
		}	
		return tempBoard;
	}

	private void receiveChatMessage(Command comm) throws ProtocolNotFollowedException {
		try {
			String playerName = comm.getArgs()[0];
			String message = comm.getArgs()[1];
			gameUI.addChatMessage(playerName, message);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ProtocolNotFollowedException();
		}
	}

	private void removePlayer(Command comm) throws ProtocolNotFollowedException {
		try {
			String playerName = comm.getArgs()[0];
			serverPlayers.remove(getServerPlayer(playerName));
			final String name = playerName;
			Thread remove = new Thread(new Runnable() {	
				@Override
				public void run() {
					gameUI.addPopup("Player rage-quited", main.getClientName() + ", player: " + name, true);
				}
			});
			remove.start();
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ProtocolNotFollowedException();
		}
	}
	
	private void performServerMove(Command comm, boolean forced) throws ProtocolNotFollowedException {
		String[] args = comm.getArgs();
		Point move = null;
		try {
			String playerName = args[0];
			int x = Integer.parseInt(args[1]);
			int y = Integer.parseInt(args[2]);
			move = new Point(x,y);
			if (forced) {
				board.setField(move, clientPlayer.getBall());
			} else {	
				board.setField(move, getServerPlayer(playerName).getBall());
			}
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
			throw new ProtocolNotFollowedException();
		}	
	}

	private void performClientMove() {
		updateScreen();
		if (hasHuman) {
			gameUI.addPopup("Your turn", main.getClientName() + " , it is your turn!\n" +
					"Your have got: " + (GlobalSettings.THINK_TIME / 1000) + " seconds to pick a field.", true);
		}
		clientPlayer.determineMove(board);
		updateScreen();
	}
	
   public void move(Point p) {
    	String title = "Move";
    	try {
    		board.setField(p, clientPlayer.getBall());
    		updateScreen();
			sc.move(p.x, p.y);			
		} catch (ProtocolNotFollowedException e) {
			gameUI.addPopup(title, main.getClientName() + " , cannot send a move message!", true);
			goBack(false);
			e.printStackTrace();
		} catch (IOException e) {
			gameUI.addPopup(title, main.getClientName() + " , the server is offline!", true);
			goBack(false);
			e.printStackTrace();
		} catch (NotSameStateException e) {
			gameUI.addPopup(title, main.getClientName() + " , the server is not in the same state?!", true);
			goBack(true);
			e.printStackTrace();
		}
    }
	
    public void chat(String message) {
    	String title = "Chat";
    	try {
			sc.chat(message);
		} catch (ProtocolNotFollowedException e) {
			gameUI.addPopup(title, main.getClientName() + " , cannot send a chat message!", true);
			goBack(false);
			e.printStackTrace();
		} catch (IOException e) {
			gameUI.addPopup(title, main.getClientName() + " , the server is offline!", true);
			goBack(false);
			e.printStackTrace();
		} catch (NotSameStateException e) {
			gameUI.addPopup(title, main.getClientName() + " , the server is not in the same state?!", true);
			goBack(true);
			e.printStackTrace();
		}
    }

	public static void main(String[] args) {
		OnlineGame game = null;
		try {
			game = new OnlineGame(new Main(), new ServerCommunicator("hoi", InetAddress.getByName("192.168.2.10"), 8080), new OnlineHumanPlayer("hoi", Ball.RED), new String[]{"henk"});
		} catch (IOException e) {
			e.printStackTrace();
		}
		game.start();
	}

	public int getNrOfPlayers() {
		return nrOfPlayers;
	}
}
