package client;

import clientAndServer.Ball;
import game.ComputerPlayer;
import game.OfflineGame;
import game.GamePlayer;
import game.HumanPlayer;
import game.NaiveStrategy;
import game.SmartStrategy;
import game.SmarterStrategy;

public class OfflineGameSetup {
	
	private String humanPlayer = "Human";
	private String naiveAIPlayer = "Stupid PC";
	private String smartAIPlayer = "Smart PC";
	private String smarterAIPlayer = "Smarter PC";
	protected String[] playersKinds = {humanPlayer, naiveAIPlayer, smartAIPlayer, smarterAIPlayer};
	
	private OfflineGameSetupUI ui;
	private Main main;
	
	public OfflineGameSetup(Main main) {
		this.main = main;
		ui = new OfflineGameSetupUI(this);	
		ui.setVisible(true);
	}
	
	public void startGame(String[] players) {
		ui.dispose();
		final OfflineGameSetup c = this;
		final String[] playerss = players;
		Thread newGame = new Thread(new Runnable() {	
			@Override
			public void run() {
				OfflineGame game = new OfflineGame(c, namesToPlayers(playerss));
				game.start();
				
			}
		});
		newGame.start();
	}
	
	public void goBack() {
		ui.dispose();
		main.returnFromAction();
	}
	
	
	public void returnFromAction() {
		ui.setVisible(true);
	}
	
	private GamePlayer[] namesToPlayers(String[] names) {
		Ball ball = Ball.RED;
		int humanCounter = 1;
		GamePlayer[] players = new GamePlayer[names.length];
		for (int i = 0; i < names.length; i++) {	
			if (names[i].equals(playersKinds[0])) {
				players[i] = new HumanPlayer(main.getClientName() + humanCounter, ball);
				humanCounter++;
			} else if (names[i].equals(playersKinds[1])) {
				players[i] = new ComputerPlayer(ball, new NaiveStrategy());
			} else if (names[i].equals(playersKinds[2])) {
				players[i] = new ComputerPlayer(ball, new SmartStrategy());
			} else if (names[i].equals(playersKinds[3])) {
				players[i] = new ComputerPlayer(ball, new SmarterStrategy());
			}
			ball = ball.next();
		}
		return players;
	}
}
