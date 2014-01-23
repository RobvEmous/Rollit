package client;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.Scanner;

/**
 * Class for maintaining the Rollit game.
 * 
 * @author Rob van Emous
 * @version 0.1
 */
public class Game extends Observable {

    /**
     * The board of the game.
     */
    private Board board;

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
    
    private int time = 500;
    
    private String curr = "Current game situation:";

    /**
     * Creates a new Game object.
     * 
     * @param s0 the first player
     * @param s1 the second player
     */
    public Game(GamePlayer s0, GamePlayer s1, GamePlayer s2, GamePlayer s3) {
    	nrOfPlayers = 4;
        board = new Board();
        players = new GamePlayer[nrOfPlayers];
        players[0] = s0;
        players[1] = s1;
        players[2] = s2;
        players[3] = s3;
        board.setInitial(players);
        current = 0;
        starter = -1;
        rand = new Random();
    }
    
    public Game(GamePlayer s0, GamePlayer s1, GamePlayer s2) {
    	nrOfPlayers = 3;
        board = new Board();
        players = new GamePlayer[nrOfPlayers];
        players[0] = s0;
        players[1] = s1;
        players[2] = s2;
        board.setInitial(players);
        current = 0;
        starter = -1;
        rand = new Random();
    }
    
    public Game(GamePlayer s0, GamePlayer s1) {
    	nrOfPlayers = 2;
        board = new Board();
        players = new GamePlayer[nrOfPlayers];
        players[0] = s0;
        players[1] = s1;
        board.setInitial(players);
        current = 0;
        starter = -1;
        rand = new Random();
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
    	board.setInitial(players);
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
    	
    	GameUI gui = new GameUI((HumanPlayer) h0);
    	
    	//Game game = new Game(s0, s1);
    	//Game game = new Game(n0, n1);
    	//Game game = new Game(s0, n1, n2, n3);
    	Game game = new Game(h0, s1,s2,s3);
    	game.addObserver(gui);
    	
		game.start();
    }

}
