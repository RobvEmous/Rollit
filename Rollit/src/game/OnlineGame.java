package game;

import java.awt.Point;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.Scanner;

import client.Main;
import client.OfflineGameSetup;
import clientAndServer.Ball;
import clientAndServer.Board;

/**
 * Class for maintaining the Rollit game.
 * 
 * @author Rob van Emous
 * @version 0.1
 */
public class OnlineGame extends Observable {
	
	/**
	 * The offline game setup menu.
	 */
	private Main main;

    /**
     * The board of the game.
     */
    private Board board;
    
    /**
     * The GUI of the game.
     */
    private GameUI gameUI;

    /**
     * The number of players of the game (2-4).
     */
    private int nrOfPlayers = 0;
    
    /**
     * The 2-4 players of the game.
     */
    private GamePlayer[] players;

    /**
     * Index of the current player.
     */
    private int current;
    
    /**
     * Index of the player which can make the first move
     */
    private int starter;
    
    private Random rand;
    
    private boolean useUI = true;
    
    private int turnCounter = 0;
    
    private int time = 1000;
    
    private String curr = "Current game situation:";

    /**
     * Creates a new Game.
     * 
     * @param thePlayers the players
     */
    public OnlineGame(Main s, GamePlayer[] thePlayers) {
    	main = s;
    	nrOfPlayers = thePlayers.length;
        board = new Board();
        players = thePlayers;
        board.setInitial();
        current = 0;
        starter = -1;
        rand = new Random();
        gameUI = new GameUI(this);
        addObserver(gameUI);
    }
    
    /**
     * Creates a new Game.
     * 
     * @param s0 the first player
     * @param s1 the second player
     * @param s2 the third player
     * @param s3 the fourth player
     */
    public OnlineGame(Main m, GamePlayer s0, GamePlayer s1, GamePlayer s2, GamePlayer s3) {
    	this(m, new GamePlayer[]{s0, s1, s2, s3});
    }
    
    /**
     * Creates a new Game.
     * 
     * @param s0 the first player
     * @param s1 the second player
     * @param s2 the third player
     */
    public OnlineGame(Main m, GamePlayer s0, GamePlayer s1, GamePlayer s2) {
    	this(m, new GamePlayer[]{s0, s1, s2});
    }
    
    /**
     * Creates a new Game.
     * 
     * @param s0 the first player
     * @param s1 the second player
     */
    public OnlineGame(Main m, GamePlayer s0, GamePlayer s1) {
    	this(m, new GamePlayer[]{s0, s1});
    }

    /**
     * Starts the Rollit game. <br>
     * Asks after each ended game if the user want to continue. Continues until
     * the user does not want to play anymore.
     */
    public void start() {
        boolean stop = false;
        while (!stop) {
            reset();
            play();
            stop = !readBoolean("\n> Play another time? (y/n)?", "y", "n");
        }
    }

    /**
     * Prints a question which can be answered by yes (true) or no 
     * (false). After prompting the question on standard out, this method 
     * reads a String from standard in and compares it to the parameters 
     * for yes and no. If the user inputs a different value, the prompt is
     * repeated and te method reads input again.
     * 
     * @parom prompt the question to print
     * @param yes the String corresponding to a yes answer
     * @param no the String corresponding to a no answer
     * @return true is the yes answer is typed, false if the no answer is typed
     */
    private boolean readBoolean(String prompt, String yes, String no) {
        String answer;
        do {
            System.out.print(prompt);
            Scanner in = new Scanner(System.in);
            answer = in.hasNextLine() ? in.nextLine() : null;
        } while (answer == null || (!answer.equals(yes) && !answer.equals(no)));
        return answer.equals(yes);
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
    	turnCounter = 0;
    }

    /**
     * Plays the Rollit game. <br>
     * First the initial board is shown (the four center balls have
     * already been set).<br> Then the game is played until one player has
     * won or it is a draw. Players can make a move one after the other. 
     * After each move, the changed game situation is printed.
     */
    private void play() {
    	updateScreen();
        while (!board.gameOver()) {
       		hasMoves(players[current]);      		
       		long endTime = System.currentTimeMillis() + time;
       		players[current].makeMove(board);
       		if (System.currentTimeMillis() < endTime) {
       			try {
					Thread.sleep(endTime - System.currentTimeMillis());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
       		}
        	updateScreen();                 
        	nextCurrent();
        }
        updateScreen();
        printResult();
    }


    private void hasMoves(GamePlayer player) {
   		System.out.println(player.getName() + "(" + player.getBall() + ") has the turn.");	
	}
    
	private void nextCurrent() {
		if (current < players.length - 1) {
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
    		notifyObservers(board);
  			System.out.println(ballOccurences());
    	} else {
   			System.out.println(
    				"\n" + curr + "\n\n" + board.toString());
  			System.out.println(ballOccurences());
    	}
    }
    
    private String ballOccurences() {
    	String text = "";
    	for (int i = 0; i < nrOfPlayers; i++) {
    		text += players[i].getName() + "(" 
    				+ players[i].getBall() + ") = " 
    				+ board.countInstancesOf(players[i].getBall())
    				+ "\n";
    	}
		return text;
	}

    /**
     * Prints the result of the last game. <br>
     */
    private void printResult() {
        if (board.hasWinner()) {
            GamePlayer winner = getPlayer(board.getWinner());
            System.out.println("Speler " + winner.getName() + " ("
                    + winner.getBall().toString() + ") has won!");      
        } else {
            System.out.println("Draw. There is no winner!");
        }
        System.out.println("This game took: " + turnCounter + " turns.");
    }
    
    private GamePlayer getPlayer(Ball b) {
    	for (int i = 0; i < nrOfPlayers; i++) {
    		if (players[i].getBall().equals(b)) {
    			return players[i];
    		}
    	}
    	return null;
    }
    
    public GamePlayer getCurrentPlayer() {
    	return players[current];
    }
    
    public GamePlayer getNextPlayer() {
    	GamePlayer player = null;
		try {
    		player = players[current + 1];
    	} catch (ArrayIndexOutOfBoundsException e) {
    		player = players[0];
    	}
    	return player;
    }
    
    public GamePlayer getWinner() {
    	GamePlayer player = null;
    	Ball winner = board.getWinner();
    	for (int i = 0; i < players.length; i++) {
    		if (players[i].getBall().equals(winner)) {
    			player = players[i];
    		}
    	}
    	return player;
    }
    
    public boolean hasHuman() {
    	boolean hasHuman = false;
    	for (int i = 0; i < players.length; i++) {
    		if (players[i] instanceof HumanPlayer) {
    			hasHuman = true;
    		}
    	}
    	return hasHuman;
    }
    
    public Point getHint() {
    	return board.getHint(players[current].getBall());
    }
    
    public static void main(String[] args) {
    	GamePlayer s0 = new ComputerPlayer(Ball.RED,new SmartStrategy());
    	GamePlayer s1 = new ComputerPlayer(Ball.BLUE,new SmartStrategy());
    	GamePlayer s2 = new ComputerPlayer(Ball.GREEN,new SmartStrategy());
    	GamePlayer s3 = new ComputerPlayer(Ball.YELLOW,new SmartStrategy());
    	
    	GamePlayer n0 = new ComputerPlayer(Ball.RED,new NaiveStrategy());
    	GamePlayer n1 = new ComputerPlayer(Ball.BLUE,new NaiveStrategy());
    	GamePlayer n2 = new ComputerPlayer(Ball.GREEN,new NaiveStrategy());
    	GamePlayer n3 = new ComputerPlayer(Ball.YELLOW,new NaiveStrategy());
    	
    	GamePlayer h0 = new HumanPlayer("Rob",Ball.RED, true);
    	GamePlayer h1 = new HumanPlayer("René",Ball.BLUE, true);
    	
    	//Game game = new Game(s0, s1);
    	//Game game = new Game(n0, n1);
    	//Game game = new Game(s0, n1, n2, n3);
    	OnlineGame game = new OnlineGame(h0, h1, s2, s3);
    	
		game.start();
    }

	public void goBack() {
		gameUI.dispose();
		setup.returnFromAction();
	}

}
