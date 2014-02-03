package clientAndServer;

import java.awt.Color;

/**
 * Represents a ball in the Rollit game. There are four playable values: <br>
 * <b>Ball.BLUE</b>, <b>Ball.RED</b>, <b>Ball.GREEN</b> and <b>Ball.YELLOW</b>.<br>
 * And two other states: 
 * <b>Ball.EMPTY</b> and <b>Ball.HINT</b>.
 * @author Rob van Emous
 * @version 1.0
 */
public enum Ball {
    
    RED, GREEN, YELLOW, BLUE, HINT, EMPTY;
    
	/*@
	   ensures this == Ball.BLUE ==> \result == Ball.RED;
	   ensures this == Ball.RED ==> \result == Ball.GREEN;
	   ensures this == Ball.GREEN ==> \result == Ball.YELLOW;
	   ensures this == Ball.YELLOW ==> \result == Ball.BLUE;
	   ensures this == Ball.EMPTY ==> \result == Ball.EMPTY;
	   ensures this == Ball.HINT ==> \result == Ball.HINT;
	 */
	/**
	 * Returns the next mark.
	 * 
	 * @return the next mark if this mark is not EMPTY or HINT else returns
	 * this
	 */
	 public Ball next() {
		 Ball ball = null;
	     if (this == RED) {
	    	 ball = GREEN;
	     } else if (this == GREEN) {
	    	 ball = YELLOW;
	     } else if (this == YELLOW) {
	    	 ball = BLUE;
	     } else if (this == BLUE){
	    	 ball = HINT;
	     } else {
	    	 ball = EMPTY;
	     }
	     return ball;
	 }
	 
	 /*@
	   ensures this == Ball.BLUE ==> \result.equals("B");
	   ensures this == Ball.RED ==> \result.equals("R");
	   ensures this == Ball.GREEN ==> \result.equals("G");
	   ensures this == Ball.YELLOW ==> \result.equals("Y");
	   ensures this == Ball.EMPTY ==> \result.equals("E");
	   ensures this == Ball.HINT ==> \result.equals("H");
	 */
	 /**
	  * Returns a representation of the ball in the form of one character. 
	  * @param b ball of interest
	  */
	 public String shortName(Ball b) {
		 String name = "";
	     if (b == BLUE) {
	    	 name = "BL";
	     } else if (b == RED) {
	    	 name = "RE";
	     } else if (b == GREEN) {
	    	 name = "GR";
	     } else if (b == YELLOW){
	    	 name = "YE";
	     } else if (b == EMPTY) {
	    	 name = "EM";
	     } else {
	    	 name = "HI";
	     }
	     return name;
	 }
	 
	 public String shortName() {
		 String name = "";
	     if (this == BLUE) {
	    	 name = "BLU";
	     } else if (this == RED) {
	    	 name = "RED";
	     } else if (this == GREEN) {
	    	 name = "GRE";
	     } else if (this == YELLOW){
	    	 name = "YEL";
	     } else if (this == EMPTY) {
	    	 name = "EMP";
	     } else {
	    	 name = "HIN";
	     }
	     return name;
	 }
	 
	 public String veryShortName() {
		 String name = "";
	     if (this == BLUE) {
	    	 name = "B";
	     } else if (this == RED) {
	    	 name = "R";
	     } else if (this == GREEN) {
	    	 name = "G";
	     } else if (this == YELLOW){
	    	 name = "Y";
	     } else if (this == EMPTY) {
	    	 name = "-";
	     } else {
	    	 name = "+";
	     }
	     return name;
	 }
	 
	 public Color getColor() {
		 Color color = null;
	     if (this == BLUE) {
	    	 color = Color.BLUE;
	     } else if (this == RED) {
	    	 color = Color.RED;
	     } else if (this == GREEN) {
	    	 color = Color.GREEN;
	     } else if (this == YELLOW){
	    	 color = Color.YELLOW;
	     } else if (this == EMPTY) {
	    	 color = Color.GRAY;
	     } else {
	    	 color = Color.WHITE;
	     }
	     return color;
	 }
	 
	 public static Ball getBall(int index) {
		 Ball ball = null;
	     if (index == 0) {
	    	 ball = RED;
	     } else if (index == 1) {
	    	 ball = GREEN;
	     } else if (index == 2) {
	    	 ball = YELLOW;
	     } else if (index == 3){
	    	 ball = BLUE;
	     } else if (index == 4){
	    	 ball = HINT;
	     } else {
	    	 ball = EMPTY;
	     }
	     return ball;
	 }

}
