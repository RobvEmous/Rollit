package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.Scanner;

import clientAndServer.Ball;
import clientAndServer.Board;
import exceptions.ClientTooSlowException;
import exceptions.ProtecolNotFollowedException;

/**
 * Class for maintaining the Rollit game.
 * 
 * @author Rob van Emous
 * @version 0.1
 */
public class ServerGame extends Observable {

    /**
     * The board of the game.
     */
    private Board board;

    /**
     * The number of GamePlayers of the game (2-4).
     */
    private int nrOfGamePlayers = 0;
    
    /**
     * The 2-4 GamePlayers of the game.
     */
    private ArrayList<GamePlayer> gamePlayers;

    /**
     * Index of the current GamePlayer.
     */
    private int current;
    
    /**
     * Index of the GamePlayer which can make the first move
     */
    private int starter;
    
    private Random rand;
    
    private boolean useUI = true;
    
    private int turnCounter = 0;
    
    private int thinkTime;
    
    private String curr = "Current game situation:";

	/**
	 * Creates a new Game object.
	 *  
	 * @param theGamePlayers 
	 * @param thinkTime
	 */
    public ServerGame(ArrayList<GamePlayer> gamePlayers, int thinkTime) {
    	this.gamePlayers = gamePlayers;
    	this.thinkTime = thinkTime;
    	nrOfGamePlayers = gamePlayers.size();    
        
        board = new Board();
        board.setInitial();
        
        current = 0;
        starter = -1;
        
        rand = new Random();
    }

    /**
     * Starts the Rollit game. <br>
     */
    public void start() {
    	Thread play = new Thread(new Runnable() {		
			@Override
			public void run() {
				play();
			}
		});
    	play.start();
    }


    /**
     * Resets the game. <br>
     * The board is emptied and a random GamePlayer becomes the current 
     * GamePlayer.
     */
    private void set() {
    	if (starter == -1) {
    		starter = rand.nextInt(nrOfGamePlayers);
    	} else {
    		int tempStarter = rand.nextInt(nrOfGamePlayers);
    		while (tempStarter == starter) {
    			tempStarter = rand.nextInt(nrOfGamePlayers);
    		}
    		starter = tempStarter;
    	}
    	current = starter;
    	board.reset();
    	board.setInitial();
    	turnCounter = 0;
    }
    
    private void removePlayer(GamePlayer player) {
    	gamePlayers.remove(player);
    	nrOfGamePlayers--;
    }

    /**
     * Plays the Rollit game. <br>
     * First the initial board is shown (the four center balls have
     * already been set).<br> Then the game is played until one GamePlayer has
     * won or it is a draw. GamePlayers can make a move one after the other. 
     * After each move, the changed game situation is printed.
     */
    private void play() {
    	updateScreen();
        while (!board.gameOver()) {    		
       		try {
				gamePlayers.get(current).determineMove(board);
			} catch (ClientTooSlowException e) {
				//TODO send: too
				e.printStackTrace();
			} catch (ProtecolNotFollowedException | IOException e ) {
				removePlayer(gamePlayers.get(current));
				informOtherPlayers();
				e.printStackTrace();
			}
        	updateScreen();             
        	nextCurrent();
        }
        updateScreen();
        printResult();
    }


    private void informOtherPlayers() {
		// TODO Auto-generated method stub
		
	}

	private void hasMoves(GamePlayer GamePlayer) {
   		System.out.println(GamePlayer.getName() + "(" + GamePlayer.getBall() + ") has the turn.");	
	}
    
	private void nextCurrent() {
		if (current < GamePlayers.length - 1) {
			current++;
		} else {
			current = 0;
		}
		turnCounter++;
	}

	/**
     * Prints the game situation.
     */
    private void updateScreen() {
    	if (useUI) {
    		setChanged();
    		notifyObservers(board.getFields());
  			System.out.println(ballOccurences());
    	} else {
   			System.out.println(
    				"\n" + curr + "\n\n" + board.toString());
  			System.out.println(ballOccurences());
    	}
    }
    
    private String ballOccurences() {
    	String text = "";
    	for (int i = 0; i < nrOfGamePlayers; i++) {
    		text += GamePlayers[i].getName() + "(" 
    				+ GamePlayers[i].getBall() + ") = " 
    				+ board.countInstancesOf(GamePlayers[i].getBall())
    				+ "\n";
    	}
		return text;
	}

    /**
     * Prints the result of the last game. <br>
     */
    private void printResult() {
        if (board.hasWinner()) {
            GamePlayer winner = getGamePlayer(board.getWinner());
            System.out.println("Speler " + winner.getName() + " ("
                    + winner.getBall().toString() + ") has won!");      
        } else {
            System.out.println("Draw. There is no winner!");
        }
        System.out.println("This game took: " + turnCounter + " turns.");
    }
    
    private GamePlayer getGamePlayer(Ball b) {
    	for (int i = 0; i < nrOfGamePlayers; i++) {
    		if (GamePlayers[i].getBall().equals(b)) {
    			return GamePlayers[i];
    		}
    	}
    	return null;
    }
    
    public static void main(String[] args) {

    }

}
