package clientAndServer;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

/**
 * Game board for the Rollit game.
 * @author Rob van Emous
 * @version 0.9
 */
public class Board {

	/**
	 * The x-dimension of the board.
	 * This <b>must</b> be an even number larger than 4.
	 */
	/*@
	  invariant X_MAX >= 2 && X_MAX % 2 == 0;
	 */
    public static final int X_MAX = 8;
    
	/**
	 * The y-dimension of the board. 
	 * This <b>must</b> be an even number larger than 4.
	 */
    /*@
      invariant Y_MAX >= 2 && Y_MAX % 2 == 0;
     */
    public static final int Y_MAX = 8;
    
	/**
	 * Number of fields on the board. 
	 */
	/*@
	  invariant NR_OF_FIELDS == X_MAX * Y_MAX;
	 */
    public static final int NR_OF_FIELDS = X_MAX * Y_MAX;

    /**
     * The X_MAX by Y_MAX fields of the Rollit board.
     */
    private Ball[][] fields;
    private int[][] rankMap;
    private ArrayList<Point> otherMoves;
    
    private Point leftUpper = new Point((X_MAX - 1) / 2, (Y_MAX - 1) / 2);
    private Point leftLower = new Point((X_MAX - 1) / 2, Y_MAX / 2);
    private Point rightUpper = new Point(X_MAX / 2, (Y_MAX - 1) / 2);
    private Point rightLower = new Point(X_MAX / 2, Y_MAX / 2);
    
    private Random rand;
    
    /**
     * Creates an empty board.
     */
    public Board() {
    	rand = new Random();
        fields = new Ball[X_MAX][Y_MAX];
        reset();
        rankMap = createRankMap();
    }
    
	/**
     * Creates a pre-set board.
     */
    /*@
      requires newFields != null;
      ensures getFields() == newFields;
     */
    public Board(Ball[][] newFields) {
        fields = newFields;
        rankMap = createRankMap();
        otherMoves = getOtherMoves();
    }
    
    /**
     * Creates a rank map according to the size of the board which 
     * indicates which moves are generally better than others by giving
     * them a higher rank.
     * 
     * @return the rank-map
     */
    private int[][] createRankMap() {
		int[][] tempRankMap = new int[X_MAX][Y_MAX];
        for (int x = 0; x < X_MAX; x++) {
        	for (int y = 0; y < Y_MAX; y++) {
        		int distXRank = getDistRank(x, y);
        		int xyRank = getXYRank(x, y);
        		tempRankMap[x][y] = distXRank * xyRank;
        	}
        }
        tempRankMap[1][0] = 0;
        tempRankMap[0][1] = 0;
        tempRankMap[X_MAX - 2][0] = 0;
        tempRankMap[X_MAX - 1][1] = 0;
        tempRankMap[X_MAX - 1][Y_MAX - 2] = 0;
        tempRankMap[X_MAX - 2][Y_MAX - 1] = 0;
        tempRankMap[1][Y_MAX - 1] = 0;
        tempRankMap[0][Y_MAX - 2] = 0;
		return tempRankMap;
	}
	
	
	private int getXYRank(int col, int row) {
		int rankX = 0;
		int rankY = 0;
		if (col <= leftUpper.x) {
			rankX = Math.abs(1 - col % 2);
		} else {
			rankX = Math.abs(col % 2);
		}
		if (row <= leftUpper.y) {
			rankY = Math.abs(1 - row % 2);
		} else {
			rankY = Math.abs(row % 2);
		}
		return rankX + rankY;
	}
	
	private int getDistRank(int col, int row) {
		int rank = 0;
		int distX = 0;
		int distY = 0;
		if (col <= leftUpper.x) {
			distX = leftUpper.x - col;
		} else {
			distX = col - rightUpper.x;
		}
		if (row <= leftUpper.y) {
			distY = leftUpper.y - row;
		} else {
			distY = row - leftLower.y;
		}
		if (distX == 0 || distY == 0) {
			rank = distX + distY;	
		} else {
			rank = distX + distY;
		}
		return rank;
	}
	
    /**
     * Ranks a field according to the rank map which indicates which moves
     * are generally better than others by giving them a higher rank.
     * 
     * @param row the field's row
     * @param col the field's column
     * @return the rank-map
     */
	/*@
	  requires isField(col,row);
	  ensures \result >= 0;
	 */
	public int getRank(int col, int row) {
		return rankMap[col][row];
	}
	
    /**
     * Ranks a field according to the rank map which indicates which moves
     * are generally better than others by giving them a higher rank.
     * 
     * @param p the point of the field
     * @return the rank-map
     */
	/*@
	  requires isField(p);
	  ensures \result >= 0;
	 */
	public int getRank(Point p) {
		return rankMap[p.x][p.y];
	}
	
	/**
	 * Returns full rank-map.
	 */
	/*@
	  ensures \result != null;
	 */
	public int[][] getRankMap() {
		return rankMap;
	}

    /**
     * Creates a deep copy of this board.
     */
	/*@
	  ensures \result == this;
	 */
    public Board deepCopy() {
    	Ball[][] newFields = new Ball[X_MAX][Y_MAX];
        for (int x = 0; x < X_MAX; x++) {
        	for (int y = 0; y < Y_MAX; y++) {
        		newFields[x][y] = fields[x][y];
        	}
        }
        return new Board(newFields);
    }
    
    /**
     * Should be called by Game just after initialization of the Board to 
     * set the initial state of the Board according to the number of 
     * players and the ball belonging to each player. 
     * @param balls the balls used by the players
     */
    /*@
      ensures getField((X_MAX -1)/2, (Y_MAX -1)/2) == Ball.RED;
      ensures getField(X_MAX / 2, (Y_MAX - 1) / 2) == Ball.YELLOW; 
      ensures getField(X_MAX / 2, Y_MAX / 2) == Ball.GREEN; 
      ensures getField((X_MAX - 1) / 2, Y_MAX / 2) == Ball.BLUE;  
     */
    public void setInitial() {
    	fields[leftUpper.x][leftUpper.y] = Ball.RED;
    	fields[rightUpper.x][rightUpper.y] = Ball.YELLOW;
    	fields[rightLower.x][rightLower.y] = Ball.GREEN;
    	fields[leftLower.x][leftLower.y] = Ball.BLUE;
    	otherMoves = getOtherMoves();
    }
    
    /**
     * Returns the whole field array.
     */
    /*@
      ensures (\forall int x,y; x >= 0 && x < X_MAX && y >= 0 && y < Y_MAX; 
      			\result[x][y] == getField(x,y));
      pure
     */
    public Ball[][] getFields() {
		return fields;
	}

    /**
     * Returns the content of the field referred to by the (row,col) pair.
     * 
     * @param col the column of the field
     * @param row the row of the field
     * @return the Ball on the field
     */
    /*@
      requires isField(col,row);
      ensures Ball.BLUE <#= \result && \result <#= Ball.EMPTY;
      pure;
     */
    public Ball getField(int col, int row) {
        return fields[col][row];
    }
    
    /**
     * Sets the content of the field represented by the (row,col) pair to
     * the Ball <code>b</code>. <br> The move will immediately be applied, 
     * so the appropriate balls will turn into Ball <code>b</code>.
     * 
     * @param row the field's row
     * @param col the field's column
     * @param b the Ball to be placed
     */
    /*@
      requires isValidMove(b, new Point(col, row));
      ensures getField(col,row) == b;
     */
    public void setField(int col, int row, Ball b) {
    	applyMove(col, row, b);
    	fields[col][row] = b;
    	otherMoves = getOtherMoves();
    }
      
    
    private void setFields(ArrayList<Point> points, Ball b, boolean apply) {
    	for (Point point : points) {
	    	setField(point, b , apply);
    	}
    	otherMoves = getOtherMoves();
    }
    
    private void setField(Point p, Ball b, boolean apply) {
    	if (apply) {
    		applyMove(p.x, p.y, b);
    	}
    	fields[p.x][p.y] = b;
    }
    
    private void applyMove(int col, int row, Ball b) {
    	applyAMove(col, row, b, "h");
    	applyAMove(col, row, b, "v");	
		applyAMove(col, row, b, "d1");	
		applyAMove(col, row, b, "d2");
	}
    
    private void applyAMove(int col, int row, Ball b, String d /*h, v, d1 or d2*/) {
		ArrayList<Point> changes = new ArrayList<Point>();
		int diffX = 1;
		int diffY = 0;
		switch (d) {
		case "h":
			break;
		case "v":
			diffX = 0;
			diffY = 1;
			break;
		case "d1":
			diffX = 1;
			diffY = 1;
			break;
		case "d2":
			diffX = -1;
			diffY = 1;
			break;
		}	
		
		loop1:
		for (int x = col + diffX, y = row + diffY; isField(x, y); x += diffX, y += diffY) {
			if (getField(x, y).equals(b)) {
				setFields(changes, b, false);
				break loop1;
			} else if (!isPlayableBall(getField(x, y))) {
				break loop1;
			} else {
				changes.add(new Point(x, y));
			}
		}
		changes.clear();
		loop2:
		for (int x = col - diffX, y = row - diffY; isField(x, y); x -= diffX, y -= diffY) {
			if (getField(x, y).equals(b)) {
				setFields(changes, b, false);
				break loop2;
			} else if (!isPlayableBall(getField(x, y))) {
				break loop2;
			} else {
				changes.add(new Point(x, y));
			}
		}
		changes.clear();
	}
    
    /**
     * Sets the content of the field referred to by Point <code>p</code> to
     * the Ball <code>b</code>. <br> The move will immediately be applied, 
     * so the appropriate balls will turn into Ball <code>b</code>.
     * 
     * @param p the point to set the field
     * @param b the Ball to be placed
     */
    /*@
      requires isValidMove(b, p);
      ensures getField(p.x, p.y) == b;
     */
    public void setField(Point p, Ball b) {
    	setField(p.x, p.y, b);
    }

	/**
     * Returns true if the (row,col) pair refers to a valid field on the board.
     * 
     * @return true if <code>0 <= row < Y_MAX && 0 <= col < X_MAX</code>
     */
    /*@
      ensures \result == (0 <= row && row < Y_MAX && 0 <= col && col < X_MAX);
      pure;
     */
    public boolean isField(int col, int row) {
        return (0 <= row) && (row < Y_MAX) && (0 <= col) && (col < X_MAX);
    }
    
    /**
     * Returns true if the field referred to by Point <code>p</code> is empty
     * 
     * @return true if <code>0 <= p.y < Y_MAX && 0 <= p.x < X_MAX</code>
     */
    /*@
      ensures \result == (0 <= p.y && p.y < Y_MAX && 0 <= p.x && p.x < X_MAX);
      pure;
     */
    public boolean isField(Point p) {
        return (0 <= p.y) && (p.y < Y_MAX) && (0 <= p.x) && (p.x < X_MAX);
    }
    
    /**
     * Returns a list containing the empty fields or null.
     */
    /*@
      ensures (\forall int x,y; isEmptyField(x,y); \result.contains(new Point(x,y)));
      pure;
     */
    public ArrayList<Point> getEmptyFields() {
    	ArrayList<Point> emptyFields = new ArrayList<Point>();
        for (int x = 0; x < X_MAX; x++) {
        	for (int y = 0; y < Y_MAX; y++) {
        		if (isEmptyField(x, y)) {
        			emptyFields.add(new Point(x,y));
        		}
        	}
        }
        if (emptyFields.size() > 0) {
        	return emptyFields;
        } 
        return null;
    }
    
    /**
     * Returns true if the field referred to by the (row,col) pair is empty.
     * 
     * @param col the column of the field
     * @param row the row of the field
     * @return true if the field is empty
     */
    /*@
      requires isField(col, row);
      ensures \result == (getField(col,row) != Ball.EMPTY);
      pure;
     */
    public boolean isEmptyField(int col, int row) {
        return fields[col][row].equals(Ball.EMPTY);
    }
    
    /**
     * Returns true if the field referred to by Point <code>p</code> is empty.
     * 
     * @param p the point of the field
     * @return true if the field is empty
     */
    /*@
      requires isField(p);
      ensures \result == (getField(p.x,p.y) == Ball.EMPTY);
      pure;
     */
    public boolean isEmptyField(Point p) {
        return fields[p.x][p.y].equals(Ball.EMPTY);
    }

    /**
     * Determines if this field is located on an edge of the board.
     * 
     * @param col the column of the field
     * @param row the row of the field
     * @return true if the field is on the edge of the board
     */
	/*@
	  ensures \result == (col == 0 || row == 0 || col == X_MAX - 1 || row == Y_MAX - 1);
	 */
    public boolean isEdgeField(int col, int row) {
    	return col == 0 || row == 0 || col == X_MAX - 1 || row == Y_MAX - 1;
    }
	
    
    /**
     * Determines if this field is located on an edge of the board.
     * 
     * @param p the point of the field
     * @return true if the field is an edge-field
     */
	/*@
	  requires p != null;
	  ensures \result == (p.x == 0 || p.y == 0 || p.x == X_MAX - 1 || p.y == Y_MAX - 1);
	 */
    public boolean isEdgeField(Point p) {
    	return isEdgeField(p.x, p.y);
    }

    /**
     * Tests if the whole board is full.
     * 
     * @return true if all fields are occupied
     */
    /*@
      ensures \result == (getEmptyFields() == null);
     */
    public boolean isFull() {
        /*for (int x = 0; x < X_MAX; x++) {
        	for (int y = 0; y < Y_MAX; y++) {
        		if (isEmptyField(x,y)) {
        			return false;
        		}
            }
        }
        return true;*/
    	return (getEmptyFields() == null);
    }  

    /**
     * Returns true if the game is over. The game is over when the whole board is full.
     * 
     * @return true if the game is over
     */
    /*@
      ensures \result == (getEmptyFields() == null);
     */
    public boolean gameOver() {
        return isFull();
    }
    
    /**
     * Determines whether a Ball can perform a move or not.
     * 
     * @param ball the ball of interest
     * @return true if the Ball can perform at least one move.
     */
	/*@
	  requires ball != null && move != null;
	  ensures \result == (getMoves(ball).contains(move));
	  pure;
	 */
    public boolean isValidMove(Ball ball, Point move) {	
		return getMoves(ball).contains(move);
	}
	
    /**
	  * Returns a random point on the board that is a valid move for the ball of interest.
	  * 
	  * @param ball the ball of interest.
	  */
	/*@
	  requires ball != null;
	  ensures isValidMove(ball, \result);
	 */
    public Point getHint(Ball ball) {
		ArrayList<Point> moves = getMoves(ball);
		Point move = null;
		if (moves != null) {
			move = moves.get(rand.nextInt(moves.size()));
		}
		return move;
	}

    /**
     * Determines whether a Ball can perform a move or not.
     * 
     * @param ball the ball of interest
     * @return true if the Ball can perform at least one move.
     */
	/*@
	  requires b != null;
	  ensures \result == (getMoves(b) != null);
	 */
    public boolean hasMoves(Ball b) {		
		return getMoves(b) != null;
	}
	
	/**
	 * Returns an ArrayList of all points that are a valid move for the ball of interest.
	 * 
	 * @param b the ball of interest.
	 */
    /*@
	  requires b != null;
	  ensures (\forall Point p; isField(p) && \result.contains(p); 
	  				nextToOccupiedField(p) == true);
	  pure;
	 */
	public ArrayList<Point> getMoves(Ball b) {
		ArrayList<Point> moves = getNormalMoves(b);
		if (moves == null) {
			moves = otherMoves;
		}
		return moves;
	}

    private ArrayList<Point> getOtherMoves() {
    	ArrayList<Point> moves = new ArrayList<Point>();
    	ArrayList<Point> emptyFields = getEmptyFields();
    	if (emptyFields != null) {
	    	for (Point p : emptyFields) {
	    		if (nextToOccupiedField(p)) {
	    			moves.add(p);
	    		}
	    	}
    	}
    	return moves;
    }
    
    /**
     * Determines whether the specified field is adjacent to an occupied field.
     * An occupied field being a field of which the ball has neither the value EMPTY nor
     * the value HINT.
     * 
     * @param col the column of the field
     * @param row the row of the field
     * @return true if the field is next to an occupied one
     */
    /*@
      requires isField(col, row);
	  ensures (\exists int x,y; isField(x,y) && !isEmptyField(x,y) && 
	    					(Math.abs(col - x) < 2) && (Math.abs(row - y) < 2);
	    					\result == true);
	  pure;
     */
    public boolean nextToOccupiedField(int col, int row) {
        for (int x = 0; x < X_MAX; x++) {
        	for (int y = 0; y < Y_MAX; y++) {
        		if (!isEmptyField(x, y)) {
            		if (Math.abs(col - x) < 2 && Math.abs(row - y) < 2) {
            			return true;
            		}
        		}
        	}
        }
        return false;
    }
    
    /**
	 * Determines the moves the Ball can perform.
	 * 
	 * @param b the ball of interest
	 * @return the moves the Ball can perform or null if the ball can't 
	 * perform any moves
     */
    /*@
      requires isField(p);
	  ensures (\exists Point q; isField(q) && !isEmptyField(q) && 
	    					(Math.abs(p.x - q.x) < 2) && (Math.abs(p.y - q.y) < 2);
	    					\result == true);
	  pure;
     */
    public boolean nextToOccupiedField(Point p) {
    	return nextToOccupiedField(p.x, p.y);
    }
    
	private ArrayList<Point> getNormalMoves(Ball b) {
		ArrayList<Point> m = new ArrayList<Point>();
		addAllMoves(b, m, "h");
		addAllMoves(b, m, "v");
		addAllMoves(b, m, "d1");
		addAllMoves(b, m, "d2");
		if (m.size() > 0) {
			return m;
		}
		return null;
	}

	/**
	 * Determines all moves <code>b</code> can perform adds them to 
	 * <code>m</code>.
	 * 
	 * @param b the ball of interest
	 * @param m the list to add the moves into
	 * @param d the direction to look for moves: <br>
	 * Horizontal: "h", vertical: "v", 
	 * diagonal(left upper to right lower: "d1"
	 * diagonal(left upper to right lower: "d2"
	 */
	private void addAllMoves(Ball b, ArrayList<Point> m, String d /*h, v, d1 or d2*/) {
		switch (d) {
		case "h":
			for (int y = 0; y < Y_MAX; y++) {
				addAllIfNotInThere(m, checkALine(b, 0, y, "h"));
			}
			break;
		case "v":
			for (int x = 0; x < X_MAX; x++) {
				addAllIfNotInThere(m, checkALine(b, x, 0, "v"));
			}
			break;
		case "d1":
			for (int y = 0; y < Y_MAX - 3; y++) {
				addAllIfNotInThere(m, checkALine(b, 0, y, "d1"));
			}
			for (int x = 1; x < X_MAX - 3; x++) {
				addAllIfNotInThere(m, checkALine(b, x, 0, "d1"));
			}
			break;
		case "d2":
			for (int y = 0; y < Y_MAX - 3; y++) {
				addAllIfNotInThere(m, checkALine(b, X_MAX - 1, y, "d2"));
			}
			for (int x = X_MAX - 2; x > 1; x--) {
				addAllIfNotInThere(m, checkALine(b, x, 0, "d2"));
			}
			break;
		}
	}

	/**
	 * Determines all moves <code>b</code> can perform on a certain line 
	 * in direction <code>d</code> starting at 
	 * (<code>startX</code>,<code>startY</code>).
	 * 
	 * @param b the ball of interest
	 * @param startX the x-coordinate of the start point
	 * @param startY the y-coordinate of the start point
	 * @param d the direction to look for moves: <br>
	 * Horizontal: "h", vertical: "v", diagonal: "d"
	 * @return the list of moves
	 */
	private ArrayList<Point> checkALine(Ball b, int startX, int startY, String d /*h, v, d1 or d2*/) {
		ArrayList<Point> m = new ArrayList<Point>();
		boolean foundThis = false;
		boolean foundOther = false;
		int diffX = 1;
		int diffY = 0;
		switch (d) {
		case "h":
			break;
		case "v":
			diffX = 0;
			diffY = 1;
			break;
		case "d1":
			diffX = 1;
			diffY = 1;
			break;
		case "d2":
			diffX = -1;
			diffY = 1;
			break;
		}			
		for (int x = startX, y = startY; isField(x, y); x += diffX, y += diffY) {
			if (fields[x][y].equals(b)) {
				foundThis = true;
				foundOther = false;
			} else if (isPlayableBall(fields[x][y])) {
				foundOther = true;
			} else if (foundThis && foundOther && fields[x][y].equals(Ball.EMPTY)) {
				addIfNotInThere(m,new Point(x,y));
				foundThis = false;
				foundOther = false;
			} else {
				foundThis = false;
				foundOther = false;
			}
			startX = x;
			startY = y;
		}
		foundThis = false;
		foundOther = false;
		for (int x = startX, y = startY; isField(x, y); x -= diffX, y -= diffY) {
			if (fields[x][y].equals(b)) {
				foundThis = true;
				foundOther = false;
			} else if (isPlayableBall(fields[x][y])) {
				foundOther = true;
			} else if (foundThis && foundOther && fields[x][y].equals(Ball.EMPTY)) {
				addIfNotInThere(m,new Point(x,y));
				foundThis = false;
				foundOther = false;
			} else {
				foundThis = false;
				foundOther = false;
			}
		}
		return m;
	}
	
	private void addAllIfNotInThere(ArrayList<Point> d, ArrayList<Point> s) {
		for (Point value : s) {
			addIfNotInThere(d, value);
		}
	}
	
	private void addIfNotInThere(ArrayList<Point> d, Point p) {
		if (!d.contains(p)) {
			d.add(p);
		}
	}
	
	/**
     * Checks if the Ball <code>b</code> has won. A Ball wins if it has
     * the most instances on the Board. 
     * 
     * @param b the Ball of interest
     * @return true if the Ball has won
     */
	/*@
	  requires b != null;
	  ensures (\exists Ball ball; isPlayableBall(ball) && 
	  				countInstancesOf(b) <= countInstancesOf(ball); \result == false);
	  pure;
	 */
    public boolean isWinner(Ball b) {
    	boolean isWinner = true;
    	int best = countInstancesOf(b);
    	for (Ball ball : Ball.values()) {
    		if (!ball.equals(b) && isPlayableBall(ball)) {
    			if (countInstancesOf(ball) >= best) {
    				isWinner = false;
    			}
    		}
    	}
        return isWinner;
    }
    
    /**
     * Counts the number of times Ball <code>b</code> occurs on the Board.
     * 
     * @param b the ball of interest
     * @return number of <code>b</code> on the Board
     */
    /*@
      requires b != null;
      ensures (\exists int counter; 
      				(\forall int x,y; isField(x,y) && getField(x,y) == b; 
      				counter == \old(counter) + 1); \result == counter);
      pure;
     */
    public int countInstancesOf(Ball b) {
    	int counter = 0;
        for (int x = 0; x < X_MAX; x++) {
        	for (int y = 0; y < Y_MAX; y++) {
        		if (fields[x][y].equals(b)) {
        			counter++;
        		}
        	}
        }
        return counter;
    }

    /**
     * Returns true if the game has a winner. This is the case when one of
     * the balls (and only one) has more instances on the Board than the 
     * others.
     * 
     * @return true if the board has a winner
     */
    /*@
      ensures (\exists Ball b; isPlayableBall(b) && isWinner(b); \result == true);
      pure;
     */
    public boolean hasWinner() {
    	boolean hasWinner = false;
    	int best = 0;
    	for (Ball ball : Ball.values()) {
    		if (isPlayableBall(ball)) {
    			int temp = countInstancesOf(ball);
    			if (temp > best) {
    				hasWinner = true;
    				best = temp;
    			} else if (temp == best) {
    				hasWinner = false;
    			}
    		}
    	}
        return hasWinner;
    }
    
    /**
     * Returns the winner or null if the game doesn't have a winner.<br>
     * This (a winner) is the case when one of the Balls (and only one)
     * has more instances on the Board than the others.
     * 
     * @return the winning Ball or null
     */
    /*@
      requires hasWinner();
      ensures (\exists Ball b; isPlayableBall(b) && 
      			!(\exists Ball other; isPlayableBall(other) && 
      				countInstancesOf(b) <= countInstancesOf(other)); \result == b);
     */
    public Ball getWinner() {
    	Ball winner = null;
    	int best = 0;
    	for (Ball ball : Ball.values()) {
    		if (isPlayableBall(ball)) {
    			int temp = countInstancesOf(ball);
    			if (temp > best) {
    				winner = ball;
    				best = temp;
    			} else if (temp == best) {
    				winner = null;
    			}
    		}
    	}
        return winner;
    }
    
    /**
     * Returns whether a Ball is a playable ball.
     * 
     * @param b the ball of interest
     * @return true if the Ball is not Ball.EMPTY or Ball.HINT
     */
    /*@
      requires b != null;
      ensures \result == !(b == Ball.EMPTY || b == Ball.HINT);
      pure;
     */
    public boolean isPlayableBall(Ball b) {
    	return !(b.equals(Ball.EMPTY) || b.equals(Ball.HINT));
    }
    
    /**
     * Returns all playable balls except for Ball <code>b</code>.
     * 
     * @param b the ball of interest
     * @return the list of balls
     */
    /*@
      requires b != null;
      ensures (\forall Ball ball; isPlayableBall(ball) && ball != b; \result.contains(ball));
     */
    public ArrayList<Ball> getPlayableBallsExcept(Ball b) {
    	ArrayList<Ball> balls = new ArrayList<Ball>();
    	for (Ball ball : Ball.values()) {
    		if (!b.equals(ball) && isPlayableBall(ball)) {
    			balls.add(ball);
    		}
    	}
    	return balls;
    }
    
    /**
     * Empties all fields of this board.
     */   
    /*@
      ensures (\forall Point p; isField(p); getField(p.x, p.y) == Ball.EMPTY); 
     */
    public void reset() {
        for (int x = 0; x < X_MAX; x++) {
        	for (int y = 0; y < Y_MAX; y++) {
        		fields[x][y] = Ball.EMPTY;
        	}
        }
    }

    /**
     * Returns a String representation of this board.
     * 
     * @return the game situation as String
     */
    /*@
      ensures \result != null;
     */
    public String toString() {  
    	String boardPrint = getXes() + "\n";
    	for (int y = 0; y < Y_MAX; y++) {
    		boardPrint += "   " + getLines() + "\n";
    		String s = "";
    		if (y < 10) {
    			s = " " + y + " |";
    		} else {
    			s = y + " |";
    		}
        	for (int x = 0; x < X_MAX; x++) {
        		s += " " + fields[x][y].veryShortName() + " |";
        	}
        	boardPrint += s + "\n";   	
    	}
    	boardPrint += "   " + getLines() + "\n";
    	return boardPrint;
    }
      
    private String getLines() {
    	String lines = "+";
    	for (int x = 0; x < X_MAX; x++) {
    		lines += "---+";
    	}
		return lines;  	
    }
    
    private String getXes() {
    	String lines = "   ";
    	for (int x = 0; x < X_MAX; x++) {
    		if (x < 10) {
    			lines += "  " + x + " ";
    		} else {
    			lines += " " + x + " ";
    		}
    	}
		return lines;  	
    }

    public static void main(String[] args) {
		Board board = new Board();
		System.out.println(board.toString());
		board.setInitial();
		System.out.println(board.toString());
		System.out.println(board.getRankMap());
	}
}
