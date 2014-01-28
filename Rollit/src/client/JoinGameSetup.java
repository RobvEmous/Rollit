package client;

import game.Ball;
import game.ComputerPlayer;
import game.OfflineGame;
import game.GamePlayer;
import game.HumanPlayer;
import game.NaiveStrategy;
import game.SmartStrategy;

public class JoinGameSetup {
	
	private String humanPlayer = "Human";
	private String naiveAIPlayer = "Stupid PC";
	private String smartAIPlayer = "Smart PC";
	protected String[] playersKinds = {humanPlayer, naiveAIPlayer, smartAIPlayer};
	
	private JoinGameSetupUI ui;
	private Main main;
	
	public JoinGameSetup(Main main) {
		this.main = main;
		ui = new JoinGameSetupUI(this);	
		ui.setVisible(true);
	}
	
	public void startGame(String[] players) {
		ui.dispose();
		OfflineGame game = new OfflineGame(namesToPlayers(players));
		game.start();
	}
	
	public void goBack() {
		ui.dispose();
		main.returnFromAction();
	}
	
	private GamePlayer[] namesToPlayers(String[] names) {
		Ball ball = Ball.RED;
		GamePlayer[] players = new GamePlayer[names.length];
		for (int i = 0; i < names.length; i++) {	
			if (names[i].equals(players[0])) {
				players[i] = new HumanPlayer(main.getClientName(), ball);
			} else if (names[i].equals(players[1])) {
				players[i] = new ComputerPlayer(ball, new NaiveStrategy());
			} else if (names[i].equals(players[2])) {
				players[i] = new ComputerPlayer(ball, new SmartStrategy());
			}
			ball = ball.next();
		}
		return players;
	}
}
