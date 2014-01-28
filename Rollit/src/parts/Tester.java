package parts;

public class Tester {
	
	private GameEngine engine;
	
	public Tester() {
		engine = new GameEngine(2);
		printMap(engine.getGameMap());
		printMap(engine.getPossMap());
		printMap(engine.getRankMap());
	}
	
	private void printMap(int[][] map) {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				print(map[j][i] + " ");
			}
			println("");
		}
		println("");
	}
	
	private void println(String t) {
		System.out.println(t);
	}
	
	@SuppressWarnings("unused")
	private void println(int v) {
		System.out.println(v);
	}
	
	private void print(String t) {
		System.out.print(t);
	}
	
	@SuppressWarnings("unused")
	private void print(int v) {
		System.out.print(v);
	}
	
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Tester tester = new Tester();
	}
}
