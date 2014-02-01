package server;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import clientAndServer.*;

import clientAndServer.Board;

/**
 * Class for maintaining the Rollit game.
 * 
 * @author Rob van Emous
 * @version 0.1
 */
public class ServerGame implements Observer {
	
    /**
     * The board of the game.
     */
    private Board board;
    
    /**
     * The number of players of the game (2-4).
     */
    private int nrOfPlayers = 0;
    
    /**
     * The player of the game.
     */
    private ArrayList<GamePlayer> players;
    
    /*
     * The score-writer object to be able to save the scores
     */
    private ScoreRW scores;
    
    /**
     * Index of the current player.
     */
    private int current = 0;
    
    /**
     * Index of the player which can make the first move
     */
    private int starter = -1;
    
    private Random rand;
    
    /**
     * Creates a new Game.
     * 
     * @param thePlayers the players
     */
    public ServerGame(ArrayList<GamePlayer> players, ScoreRW scores) {
    	this.players = players;
    	this.scores = scores;
    	nrOfPlayers = players.size();
    	this.players = players;
        board.setInitial();
    }
    
	/**
     * Starts the online Rolit game. <br>
     * First the initial board is shown (the four center balls have
     * already been set).<br> Then the game is played until the server
     * signals the game is over.<br> 
     * After each move, the changed game situation is printed.
     */
    private void start() {
    	for (GamePlayer player : players) {
    		player.add
    	}
    }

	/**
     * Prints the game situation.
     */
    private void updateScreen() {
		System.out.println(
				"\n" + curr + "\n\n" + board.toString());
		System.out.println(ballOccurences());
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

	public void rageQuit() {
		String infoTitle = "Rage Quit";
		gameUI.dispose();
		sc.deleteObserver(this);
		try {		
			sc.quitGame();
		} catch (ProtocolNotFollowedException e) {
			gameUI.addPopup(infoTitle, main.getClientName() + GlobalData.ERR_PROTECOL, true);
			goBack(false);
			e.printStackTrace();
		} catch (IOException e) {
			gameUI.addPopup(infoTitle, main.getClientName() + GlobalData.ERR_CLIENT_CONNECTION, true);
			goBack(false);
			e.printStackTrace();
		} catch (NotSameStateException e) {
			gameUI.addPopup(infoTitle, main.getClientName() + GlobalData.ERR_STATE, true);
			goBack(true);
			e.printStackTrace();
		}
	}

	public void chat(String text) {
		String infoTitle = "Chat";
		try {
			sc.chat(text);
		} catch (ProtocolNotFollowedException e) {
			gameUI.addPopup(infoTitle, main.getClientName() + GlobalData.ERR_PROTECOL, true);
			goBack(false);
			e.printStackTrace();
		} catch (IOException e) {
			gameUI.addPopup(infoTitle, main.getClientName() + GlobalData.ERR_CLIENT_CONNECTION, true);
			goBack(false);
			e.printStackTrace();
		} catch (NotSameStateException e) {
			gameUI.addPopup(infoTitle, main.getClientName() + GlobalData.ERR_STATE, true);
			goBack(true);
			e.printStackTrace();
		}
		
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o.equals(sc)) {
			Command comm = (Command) arg;
			if (comm.equals(Commands.COM_YOURTURN)) {
				try {
					sc.sendAck(comm.getId(), new String(""));
					clientHasTurn = true;
					performClientMove();
				} catch (IOException e) {
					gameUI.addPopup("Send Ack", main.getClientName() + GlobalData.ERR_CLIENT_CONNECTION, true);
					goBack(false);
					e.printStackTrace();
				}
			} else if (comm.equals(Commands.COM_MOVETOOSLOW)) {
				try {
					sc.sendAck(comm.getId(), new String(""));
					String[] newArgs = {main.getClientName(), comm.getArgs()[0], comm.getArgs()[1]};
					Command newComm = new Command(Commands.COM_MOVE, newArgs);
					performServerMove(newComm);
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
			} else if (comm.equals(Commands.COM_UPDATE)) {
				try {
					sc.sendAck(comm.getId(), new String(""));
					performServerMove(comm);
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
			} else if (comm.equals(Commands.COM_MESSAGE)) {
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
			} else if (comm.equals(Commands.COM_PLAYERQUIT)) {
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

			} else if (comm.equals(Commands.COM_GAMEOVER)) {
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
		if (tempBoard.gameOver()) {
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
			gameUI.addPopup("Player rage-quited", main.getClientName() + ", player: " + playerName, true);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ProtocolNotFollowedException();
		}
	}
	
	private void performServerMove(Command comm) throws ProtocolNotFollowedException {
		String[] args = comm.getArgs();
		Point move = null;
		try {
			String playerName = args[0];
			int x = Integer.parseInt(args[1]);
			int y = Integer.parseInt(args[2]);
			move = new Point(x,y);
			board.setField(move, getServerPlayer(playerName).getBall());
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
			throw new ProtocolNotFollowedException();
		}	
	}

	private void performClientMove() {
		gameUI.addPopup("Your turn", main.getClientName() + " , it is your turn!\n" +
				"Your have got: " + (GlobalSettings.THINK_TIME / 1000) + "seconds to pick a field.", true);
	}

	public int getNrOfPlayers() {
		return nrOfPlayers;
	}

}