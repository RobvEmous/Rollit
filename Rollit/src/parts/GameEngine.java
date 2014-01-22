package parts;

import java.awt.Point;
import java.util.Random;

/**
 * Engine which will be the link between the UI and the user
 * @author Rob van Emous
 */
public class GameEngine {
	private final static int MAP_SIZE = 8;
	private int[][] gameMap = new int[MAP_SIZE][MAP_SIZE];
	private int[][] possMap = new int[MAP_SIZE][MAP_SIZE];	
	private int[][] rankMap = new int[MAP_SIZE][MAP_SIZE];
	private MapFunctions mapCreator = new MapFunctions(MAP_SIZE);
	int numberOfPlayers;
	Random random = new Random();
	int freeCounter = MAP_SIZE * MAP_SIZE;

	public GameEngine(int myNumberOfPlayers) {
		numberOfPlayers = myNumberOfPlayers;
		initializeMap(numberOfPlayers);
		rankMap = mapCreator.createRank(gameMap,2);
		possMap = mapCreator.createPoss(gameMap,2);
		
	}
	
	public void ProcessAction() {
		//TODO
	}
	
	public void ProcessButton() {
		//TODO
	}
	
	private void initializeMap(int nrOfP) {
		if (nrOfP == 4) {
			setColor(3,3,1);
			setColor(4,3,4);
			setColor(4,4,2);
			setColor(3,4,3);
		} else if (nrOfP == 2) {
			setColor(3,3,1);
			setColor(4,3,2);
			setColor(4,4,1);
			setColor(3,4,2);
		}
		for (int i = 1; i < numberOfPlayers + 1; i++) {
			setRandomColors(10,i);
		}	
	}

	public int[][] getGameMap() {
		return gameMap;
	}
	
	public int[][] getPossMap() {
		return possMap;
	}
	
	public int[][] getRankMap() {
		return rankMap;
	}
	
	/**
	 * @param x the y-coördinate of the square in map
	 * @param y the y-coördinate of the square in map
	 * @param color 0: blank, 1: red, 2: green, 3: blue, 4: yellow
	 */
	public boolean setColor(int x, int y, int color) {
		boolean isFull = false;
		if (freeCounter > 0) {
			gameMap[x][y] = color;
			freeCounter--;
		} else {
			isFull = true;
		}
		return isFull;
	}
	
	/**
	 * @param point the x- and y-coördinate of the square in map
	 * @param color 0: red, 1: green, 2: blue, 3: yellow
	 */
	public void setColor(Point point, int color) {
		gameMap[point.x][point.y] = color;
		freeCounter--;
	}
	
	public int getColor(int x, int y) {
		return gameMap[x][y];
	}
	
	public int getColor(Point point) {
		return gameMap[point.x][point.y];
	}
	
	private boolean setRandomColors(int amount, int color) {
		boolean isFull = false;
		int counter = 0;
		while (counter < amount && !isFull) {
				isFull = setRandomColor(color);
				counter++;
		}
		return isFull;
	}
	
	private boolean setRandomColor(int color) {
		int myX = 0;
		int myY = 0;
		boolean found = false;
		boolean isFull = false;
		while (!found && !isFull) {
			myX = getRandom(0,MAP_SIZE - 1);
			myY = getRandom(0,MAP_SIZE - 1);
			if (getColor(myX, myY) == 0) {
				if (!setColor(myX, myY, color)) {
					found = true;
				} else {
					isFull = true;
				}
			}
		}
		return isFull;
	}
	
	private int getRandom(int min, int max) {
		return min + random.nextInt(max - min);
		
	}
}
