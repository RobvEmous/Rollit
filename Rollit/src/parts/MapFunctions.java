package parts;

public class MapFunctions {
	private int mapSize;
	private int color;
	private int[][] possMap;
	private int[][] rankMap;
	private int[][] currRankMap;
	private int[][] gameMap;	
	private int x = 0;
	private int y = 0;
	private int xMax;
	private int yMax;
	
	public MapFunctions(int theMapsize) {
		mapSize = theMapsize;
		possMap = new int[mapSize][mapSize];
		rankMap = new int[mapSize][mapSize];
		currRankMap  = new int[mapSize][mapSize];
		gameMap = new int[mapSize][mapSize];	
		xMax = mapSize - 1;
		yMax = xMax;
		initRankMap();
	}

	private void initRankMap() {
		rankMap[0] = new int[] {5,0,4,3,3,4,0,5};
		rankMap[1] = new int[] {0,0,3,1,1,3,0,0};
		rankMap[2] = new int[] {4,3,4,3,3,4,3,4};
		rankMap[3] = new int[] {4,1,3,0,0,3,1,3};
		rankMap[4] = new int[] {4,1,3,0,0,3,1,3};
		rankMap[5] = new int[] {4,3,4,3,3,4,3,4};
		rankMap[6] = new int[] {0,0,3,1,1,3,0,0};
		rankMap[7] = new int[] {5,0,4,4,4,4,0,5};	
	}

	public int[][] createPoss(int[][] newGameMap, int theColor) {
		gameMap = newGameMap;
		color = theColor;
		while (nextGoodPoint()) {
			if (x > 1) {
				match("l");
				if (y > 1) {
					match("lu");
				}
			} 
			if (y > 1) {
				match("u");
				if (x < xMax - 1) {
					match("ru");
				}
			}
			if (x < xMax - 1) {
				match("r");
				if (y < yMax - 1) {	
					match("rd");
				}
			}
			if (y < yMax - 1) {	
				match("d");
				if (x > 1) {
					match("ld");
				}
			}
		}
		return possMap;	
	}
	
	public int[][] createRank(int[][] newGameMap, int theColor) {
		createPoss(newGameMap, theColor);
		for (int i = 0; i < newGameMap.length; i++) {
			for (int j = 0; j < newGameMap[i].length; j++) {
				if (possMap[j][i] != 0) {
					currRankMap[j][i] = rankMap[j][i];
				}
			}
		}
		return currRankMap;
		
	}
		
	private int match(String direction) {
		boolean foundAnother = false;
		boolean foundSame = false;
		boolean foundZero = false;
		int state = 0;
		switch (direction) {
			case "l":
				for (int i = x; i >= 0; i--) {
					state = getPointState(i,y);
					if (state == 0 && foundAnother && !foundSame && !foundZero) {
						possMap[i][y] = color;
						foundAnother = false;
					} else if (state == 1) {
						foundAnother = true;
						foundSame = false;
					} else if (state == 2) {
						foundSame = true;
					} else {
						foundZero = true;
					}
				}
				break;
			case "r":
				for (int i = x; i < gameMap.length; i++) {
					state = getPointState(i,y);
					if (state == 0 && foundAnother && !foundSame && !foundZero) {
						possMap[i][y] = color;
						foundAnother = false;
					} else if (state == 1) {
						foundAnother = true;
						foundSame = false;
					} else if (state == 2) {
						foundSame = true;
					} else {
						foundZero = true;
					}
				}
				break;
			case "u":
				for (int i = y; i >= 0; i--) {
					state = getPointState(x,i);
					if (state == 0 && foundAnother && !foundSame && !foundZero) {
						possMap[x][i] = color;
						foundAnother = false;
					} else if (state == 1) {
						foundAnother = true;
						foundSame = false;
					} else if (state == 2) {
						foundSame = true;
					} else {
						foundZero = true;
					}
				}
				break;
			case "d":
				for (int i = y; i < gameMap.length; i++) {
					state = getPointState(x,i);
					if (state == 0 && foundAnother && !foundSame && !foundZero) {
						possMap[x][i] = color;
						foundAnother = false;
					} else if (state == 1) {
						foundAnother = true;
						foundSame = false;
					} else if (state == 2) {
						foundSame = true;
					} else {
						foundZero = true;
					}
				}
				break;
			case "lu":
				for (int i = x, j = y; i >= 0 && j >= 0; i--, j--) {
					state = getPointState(i,j);
					if (state == 0 && foundAnother && !foundSame && !foundZero) {
						possMap[i][j] = color;
						foundAnother = false;
					} else if (state == 1) {
						foundAnother = true;
						foundSame = false;
					} else if (state == 2) {
						foundSame = true;
					} else {
						foundZero = true;
					}
				}
				break;
			case "ld":
				for (int i = x, j = y; i >= 0 && j <= yMax; i--, j++) {
					state = getPointState(i,j);
					if (state == 0 && foundAnother && !foundSame && !foundZero) {
						possMap[i][j] = color;
						foundAnother = false;
					} else if (state == 1) {
						foundAnother = true;
						foundSame = false;
					} else if (state == 2) {
						foundSame = true;
					} else {
						foundZero = true;
					}
				}
				break;
			case "ru":
				for (int i = x, j = y; i <= xMax && j >= 0; i++, j--) {
					state = getPointState(i,j);
					if (state == 0 && foundAnother && !foundSame && !foundZero) {
						possMap[i][j] = color;
						foundAnother = false;
					} else if (state == 1) {
						foundAnother = true;
						foundSame = false;
					} else if (state == 2) {
						foundSame = true;
					} else {
						foundZero = true;
					}
				}
				break;
			case "rd":
				for (int i = x, j = y; i <= xMax && j <= yMax; i++, j++) {
					state = getPointState(i,j);
					if (state == 0 && foundAnother && !foundSame && !foundZero) {
						possMap[i][j] = color;
						foundAnother = false;
					} else if (state == 1) {
						foundAnother = true;
						foundSame = false;
					} else if (state == 2) {
						foundSame = true;
					} else {
						foundZero = true;
					}
				}
				break;
		}
		return color;	
	}
	
	private int getPointState(int myX, int myY) {
		int state = 0;
		if (gameMap[myX][myY] != 0 && gameMap[myX][myY] != color) {
			state = 1;
		} else if (gameMap[myX][myY] != 0) {
			state = 2;
		} else {
			state = 0;
		}
		return state;	
	}
	
	private boolean nextGoodPoint() {
		boolean found = false;
		if (x == 0 && y == 0 && gameMap[x][y] == color ) {
			found = true;
		} else {
			while (nextPoint() && gameMap[x][y] != color) {
				// keep on searching				
			}
			if (gameMap[x][y] == color) {
				found = true;
			}
		}
		return found;
	}
	
	private boolean nextPoint() {
		boolean exists = true;
		if (x == xMax && y == yMax) {
			exists = false;
		} else if (x == yMax) {
			y += 1;
			x = 0;
		} else {
			x += 1;
		}
		return exists;	
	}
	
	public int[][] getPossMap() {
		return possMap;
	}
}
