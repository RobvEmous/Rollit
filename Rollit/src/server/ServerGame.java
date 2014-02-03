package server;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import clientAndServer.*;

import clientAndServer.Board;
import exceptions.NotSameStateException;
import exceptions.ProtocolNotFollowedException;

/**
 * Class for maintaining the Rollit game.
 * 
 * @author Rob van Emous
 * @version 0.1
 */
public class ServerGame extends Observable implements Observer {
	
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
      
    /**
     * Index of the current player.
     */
    private int current = 0;
    
    /**
     * Index of the player which can make the first move
     */
    private int starter = -1;
    
    private Random rand;
    
    private Command moveCommand = null;
    
    private boolean stop = false;
    
    /**
     * Creates a new Game.
     * 
     * @param players the players
     */
    public ServerGame(ArrayList<GamePlayer> players) {
    	this.players = players;
    	nrOfPlayers = players.size();
    	board = new Board();
    	rand = new Random();
    }
    
	/**
     * Starts the online Rolit game. <br>
     */
    public void start() { 	
    	reset();
    	for (GamePlayer player : players) {
    		player.getClient().addObserver(this);
    	}
		try {
			Thread.sleep(GlobalSettings.TIME_OUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	while (!stop && !board.gameOver()) {
    		performMove();
    		nextCurrent();
    	}
    	if (!stop) {
    		gameOver();
    	} else {
    		shutdown();
    	}    	
    }

    /**
     * Resets the game. <br>
     * The board is emptied and a random player becomes the current 
     * player.
     */
    private void reset() {
    	if (starter == -1) {
    		starter = rand.nextInt(nrOfPlayers);
    	} else {
    		int tempStarter = rand.nextInt(nrOfPlayers);
    		while (tempStarter == starter) {
    			tempStarter = rand.nextInt(nrOfPlayers);
    		}
    		starter = tempStarter;
    	}
    	current = starter;
    	board.reset();
    	board.setInitial();
    }
    
    private void nextCurrent() {
    	if (current < nrOfPlayers - 1) {
    		current++;
    	} else {
    		current = 0;
    	}   
    }
    
    private GamePlayer getPlayer(ClientCommunicator c) {
    	for (GamePlayer player : players) {
    		if (player.getClient().equals(c)) {
    			return player;
    		}
    	}
    	return null;
    }
    
	@Override
	public void update(Observable o, Object arg) {
		ClientCommunicator client = (ClientCommunicator) o;
		GamePlayer player = getPlayer(client);
		if (players.contains(player)) {
			Command comm = (Command) arg;
			if (comm.getId().equals(Commands.COM_CHAT)) {
				String message = "";
				try {
					message = comm.getArgs()[0];
					ArrayList<GamePlayer> notRespondingToChat = new  ArrayList<GamePlayer>();
					for (GamePlayer leftoverPlayer : players) {
						try {
							leftoverPlayer.getClient().message(message);
						} catch (ProtocolNotFollowedException | IOException e) {
							notRespondingToChat.add(player);
						}
					}
					for (GamePlayer nonResponder : notRespondingToChat) {
						removePlayer(nonResponder);
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					try {
						player.getClient().sendAck(comm.getId(), Commands.ANS_GEN_BAD);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					removePlayer(player);
					e.printStackTrace();
				}
				try {
					player.getClient().sendAck(comm.getId(), Commands.ANS_GEN_GOOD);
				} catch (IOException e) {
					removePlayer(player);
					e.printStackTrace();
				}
			} else if (comm.getId().equals(Commands.COM_MOVE)) {
				if (players.get(current).equals(player)) {
					moveCommand = comm;
				} else {
					try {
						player.getClient().sendAck(comm.getId(), Commands.ANS_GEN_BAD);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					removePlayer(player);
				}
			} else if (comm.getId().equals(Commands.COM_QUIT)) {
				try {
					player.getClient().sendAck(comm.getId(), Commands.ANS_GEN_GOOD);
				} catch (IOException e) {
					e.printStackTrace();
				}
				removePlayer(player);
			} else if (comm.getId().equals(Commands.NOT_PLAYERDIED)) {
				removePlayer(player);
			}
		}
	}
	
	private void gameOver() {
		ArrayList<GamePlayer> notRespondingToGameOver = new  ArrayList<GamePlayer>();
		for (GamePlayer player : players) {
			try {
				player.getClient().gameOver();
			} catch (ProtocolNotFollowedException | IOException e) {
				notRespondingToGameOver.add(player);
				e.printStackTrace();
			}
		}		
		for (GamePlayer player : players) {
			player.getClient().deleteObserver(this);
		}
		for (GamePlayer nonResponder : notRespondingToGameOver) {
			players.remove(nonResponder);
		}
		notifyObservers(players);
		stop = true;
	}

	private void removePlayer(GamePlayer player) {
		player.getClient().deleteObserver(this);
		players.remove(player);
		nrOfPlayers--;
		ArrayList<GamePlayer> notRespondingToQuit = new  ArrayList<GamePlayer>();
		for (GamePlayer leftoverPlayer : players) {
			try {
				leftoverPlayer.getClient().PlayerQuited(player.getName());
			} catch (ProtocolNotFollowedException | IOException e) {
				notRespondingToQuit.add(leftoverPlayer);
				e.printStackTrace();
			}
		}
		if (nrOfPlayers < 2) {
			gameOver();
			return;
		}
		for (GamePlayer nonResponder : notRespondingToQuit) {
			removePlayer(nonResponder);
			nrOfPlayers--;
			if (nrOfPlayers < 2) {
				gameOver();
				return;
			}
		}

	}
	
	private void performMove() {
		GamePlayer currentP = players.get(current);
		Ball ball = currentP.getBall();
	 	Point performedMove = null;
	 	int counter = 0;
		moveCommand = null;
		try {
			currentP.getClient().yourTurn();
		} catch (ProtocolNotFollowedException | IOException e2) {
			removePlayer(currentP);
			e2.printStackTrace();
			return;
		}
		loop:
		while (moveCommand == null) {
			try {
				Thread.sleep(GlobalSettings.SLEEP_TIME);
				counter++;
				if (counter >= GlobalSettings.THINK_TIME / GlobalSettings.SLEEP_TIME) {
					performedMove = board.getHint(ball);		
					try {
						currentP.getClient().moveTooSlow(performedMove.x, performedMove.y);
						break loop;
					} catch (ProtocolNotFollowedException | IOException e) {
						removePlayer(currentP);
						e.printStackTrace();
						return;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (!players.contains(currentP)) {
				return;
			}
		}
		if (moveCommand != null) {		
			try {
				int x = Integer.parseInt(moveCommand.getArgs()[0]);
				int y = Integer.parseInt(moveCommand.getArgs()[1]);
				performedMove = new Point(x,y);
				if (!board.isValidMove(ball, performedMove)) {
					throw new NotSameStateException();
				}
			} catch (NumberFormatException | NotSameStateException e2) {
				try {
					currentP.getClient().sendAck(Commands.COM_MOVE, Commands.ANS_GEN_BAD);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				removePlayer(currentP);
				return;
			}
			try {
				currentP.getClient().sendAck(Commands.COM_MOVE, Commands.ANS_GEN_GOOD);
			} catch (IOException e) {
				removePlayer(currentP);
				e.printStackTrace();
				return;
			}
			
		}
		
		board.setField(performedMove, ball);
		
		ArrayList<GamePlayer> notRespondingToMove = new  ArrayList<GamePlayer>();
		for (GamePlayer leftoverPlayer : players) {
			if (!leftoverPlayer.equals(currentP)) {
				if (!updatePlayer(leftoverPlayer, currentP, performedMove)) {
					notRespondingToMove.add(leftoverPlayer);
				}
			}
		}
		for (GamePlayer nonResponder : notRespondingToMove) {
			removePlayer(nonResponder);
		}
	}
		
	private boolean updatePlayer(GamePlayer player, GamePlayer mover, Point move) {
		try {
			player.getClient().update(mover.getName(), move.x, move.y);
		} catch (ProtocolNotFollowedException | IOException e) {
			e.printStackTrace();
			return false;
			
		}
		return true;
	}

	@Override
	public void notifyObservers(Object argument) {
		setChanged();
		super.notifyObservers(argument);
	}


	/**
	 * Stops game and shuts down players.
	 */
	public void shutdown() {
		for (GamePlayer player : players) {
			player.getClient().deleteObserver(this);
			player.getClient().shutdown(false);
		}
		
	}

}