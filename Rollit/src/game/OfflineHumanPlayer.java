package game;

import java.awt.Point;
import java.util.Scanner;

import clientAndServer.Ball;
import clientAndServer.Board;

/**
 * Class for maintaining a human player in the Rollit game.
 * 
 * @author Rob van Emous
 * @version v0.8
 */
public class OfflineHumanPlayer extends HumanPlayer {
	
	public Point choice = null;
	public OfflineGameUI gui;
	
	/**
	 * If <code>true</code> uses UI to communicate, else uses System.out.
	 */
	private boolean useUI = true;
	
	private Scanner scanner;

    /**
     * Creates a new human player object.
     * 
     */
    public OfflineHumanPlayer(String name, Ball ball, boolean useUI) {
        super(name, ball);
        this.useUI = useUI;
        scanner = new Scanner(System.in);
    }
    
    /**
     * Creates a new human player object.
     * 
     */
    public OfflineHumanPlayer(String name, Ball ball) {
        super(name, ball);
        scanner = new Scanner(System.in);
    }
    
    /**
     * Asks the user to input the field where to place the next mark. This is
     * done either using the UI or the standard input/output.
     * 
     * @param board the game board
     * @return the player's chosen field
     */
    public Point determineMove(Board board) {
    	if (useUI) {
    		choice = null;
    		boolean valid = false;
    		while (choice == null || !valid) {
    			try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    			valid = board.isValidMove(getBall(), choice);
	    		if (choice != null && !valid) {
	    			OfflineGameUI.newPopup("Illegal move", "You can't turn that ball, " + 
	    					"please choose another one.", false);
	    			choice = null;
	    		}
    		}
    		return choice;
    	} else {
    		Point choice = null;
    		boolean valid = false;
    		do {
	            String promptX = "> " + getName() + " (" + getBall().toString() + ")"
	                    + ", make a choice.\nColumn?";
	            int x = readInt(promptX);
	            String promptY = "Row?";
	            int y = readInt(promptY);
	            choice = new Point(x, y);
	            valid = board.isField(choice) && board.isValidMove(getBall(), choice);
	            if (!valid) {
	                System.out.println("ERROR: field (" + choice.x + "," + choice.y
	                        + ") is no valid choice.");
	            }
	        } while (!valid);
            return choice;
    	}
    }

    /**
     * Writes a prompt to standard out and tries to read an int value from
     * standard in. This is repeated until an int value is entered.
     * 
     * @param prompt the question to prompt the user
     * @return the first int value which is entered by the user
     */
    private int readInt(String prompt) {
        int value = 0;
        boolean intRead = false;
        do {
            System.out.print(prompt);
            String line = scanner.nextLine();
            Scanner scannerLine = new Scanner(line);
            if (scannerLine.hasNextInt()) {
                intRead = true;
                value = scannerLine.nextInt();
            }
            try {
            	Thread.sleep(50);
            } catch (InterruptedException e) {
            	// do not sleep
            }
            scannerLine.close();
        } while (!intRead);
        return value;
    }

}
