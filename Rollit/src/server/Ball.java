package server;

/**
 * Represents a ball in the Rollit game. There are four playable values: <br>
 * <b>Ball.BLUE</b>, <b>Ball.RED</b>, <b>Ball.GREEN</b> and <b>Ball.YELLOW</b>.<br>
 * And two other states: 
 * <b>Ball.EMPTY</b> and <b>Ball.HINT</b>.
 * @author Rob van Emous
 * @version 1.0
 */
public enum Ball {
    
    BLUE, RED, GREEN, YELLOW, HINT, EMPTY;
    
	/*@
	   ensures this == Mark.BLUE ==> \result == Mark.RED;
	   ensures this == Mark.RED ==> \result == Mark.GREEN;
	   ensures this == Mark.GREEN ==> \result == Mark.YELLOW;
	   ensures this == Mark.YELLOW ==> \result == Mark.BLUE;
	   ensures this == Mark.EMPTY ==> \result == Mark.EMPTY;
	   ensures this == Mark.HINT ==> \result == Mark.HINT;
	 */
	/**
	 * Returns the next mark.
	 * 
	 * @return the next mark if this mark is not EMPTY or HINT else returns
	 * this
	 */
	 public Ball next() {
		 Ball ball = null;
	     if (this == BLUE) {
	    	 ball = RED;
	     } else if (this == RED) {
	    	 ball = GREEN;
	     } else if (this == GREEN) {
	    	 ball = YELLOW;
	     } else if (this == YELLOW){
	    	 ball = BLUE;
	     } else {
	    	 ball = EMPTY;
	     }
	     return ball;
	 }
	 
	 /*@
	   ensures this == Mark.BLUE ==> \result.equals("B");
	   ensures this == Mark.RED ==> \result.equals("R");
	   ensures this == Mark.GREEN ==> \result.equals("G");
	   ensures this == Mark.YELLOW ==> \result.equals("Y");
	   ensures this == Mark.EMPTY ==> \result.equals("E");
	   ensures this == Mark.HINT ==> \result.equals("H");
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

}
