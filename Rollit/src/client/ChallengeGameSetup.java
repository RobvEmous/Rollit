package client;

import clientAndServer.Ball;
import game.ComputerPlayer;
import game.OfflineGame;
import game.GamePlayer;
import game.HumanPlayer;
import game.NaiveStrategy;
import game.OnlineGame;
import game.SmartStrategy;

public class ChallengeGameSetup {
	
	private String humanPlayer = "Human";
	private String naiveAIPlayer = "Stupid PC";
	private String smartAIPlayer = "Smart PC";
	protected String[] playersKinds = {humanPlayer, naiveAIPlayer, smartAIPlayer};
	
	private ChallengeGameSetupUI ui;
	private Main main;
	
	public ChallengeGameSetup(Main main, ServerCommunicator c) {
		this.main = main;
		ui = new ChallengeGameSetupUI(this);	
		ui.setVisible(true);
	}
	
	public void startGame(String[] players) {
		ui.dispose();
		OnlineGame game = new OnlineGame(main, namesToPlayers(players));
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
