package client;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import clientAndServer.Ball;
import clientAndServer.Command;
import clientAndServer.Commands;
import clientAndServer.GlobalData;
import clientAndServer.Tools;
import exceptions.NotSameStateException;
import exceptions.ProtocolNotFollowedException;
import game.ComputerPlayer;
import game.GamePlayer;
import game.NaiveStrategy;
import game.OnlineGame;
import game.OnlineHumanPlayer;
import game.SmartStrategy;
import game.SmarterStrategy;

public class JoinGameSetup implements Observer {

	private Main main;
	private ServerCommunicator c;
	private JoinGameSetupUI ui;
	
	private Command newGame = null;
	
	private String choice = null;
	private int nrOfPlayers = 0;
	
	public JoinGameSetup(Main main, ServerCommunicator c) {
		this.main = main;
		this.c = c;
		ui = new JoinGameSetupUI(this);	
		ui.setVisible(true);
		c.addObserver(this);
	}
	
	public void joinGame(String choice, int nrOfPlayers) {
		String infoTitle = "Join game";
		this.nrOfPlayers = nrOfPlayers;
		this.choice = choice;
		try {
			c.join(nrOfPlayers);
			ui.addPopup(infoTitle, main.getClientName() + GlobalData.MSG_WAITING_J, false);
		} catch (ProtocolNotFollowedException e) {
			ui.addPopup(infoTitle, main.getClientName() + GlobalData.ERR_PROTECOL, true);
			goBack(false);
			e.printStackTrace();
		} catch (IOException e) {
			ui.addPopup(infoTitle, main.getClientName() + GlobalData.ERR_CLIENT_CONNECTION, true);
			goBack(false);
			e.printStackTrace();
		} catch (NotSameStateException e) {
			ui.addPopup(infoTitle, main.getClientName() + GlobalData.ERR_STATE, true);
			goBack(true);
			e.printStackTrace();
		}
	}
	
	public void disjoinGame() {
		String infoTitle = "Disjoin game";
		try {
			c.disjoin();
			ui.addPopup(infoTitle, main.getClientName() + GlobalData.MSG_WAITING_J, false);
		} catch (ProtocolNotFollowedException e) {
			ui.addPopup(infoTitle, main.getClientName() + GlobalData.ERR_PROTECOL, true);
			goBack(false);
			e.printStackTrace();
		} catch (IOException e) {
			ui.addPopup(infoTitle, main.getClientName() + GlobalData.ERR_CLIENT_CONNECTION, true);
			goBack(false);
			e.printStackTrace();
		} catch (NotSameStateException e) {
			ui.addPopup(infoTitle, main.getClientName() + GlobalData.ERR_STATE, true);
			goBack(true);
			e.printStackTrace();
		}
		
	}
	
	private void startGame() {
		String infoTitle = "Start game";
		String clientname = main.getClientName();
		try {
			String[] allPlayers = newGame.getArgs();
			final String[] otherPlayers = Tools.removeOne(allPlayers, clientname);
			if (allPlayers.length != nrOfPlayers || allPlayers.length - otherPlayers.length != 1) {
				throw new ProtocolNotFollowedException();
			} 
			c.deleteObserver(this);
			ui.dispose();
			// from here the gameSetting is final and an online game will be started.
			final GamePlayer player = nameToPlayer(choice);
			Thread newGame = new Thread(new Runnable() {	
				@Override
				public void run() {
					OnlineGame game = new OnlineGame(main, c, player, otherPlayers);
					game.start();	
				}
			});
			newGame.start();
		} catch (ProtocolNotFollowedException e) {
			ui.addPopup(infoTitle, clientname + GlobalData.ERR_PROTECOL, true);
			goBack(false);
			e.printStackTrace();
		}
		

		//OnlineGame game = new OnlineGame(main, nameToPlayer(player));
		//game.start();
		
	}

	public void goBack(boolean kicked) {
		ui.dispose();
		main.returnFromAction();
	}
	
	private GamePlayer nameToPlayer(String name) {
		Ball ball = Ball.RED;
		GamePlayer player = null;
		if (name.equals(GlobalData.PLAYERS[0])) {
			player = new OnlineHumanPlayer(main.getClientName(), ball);
		} else if (name.equals(GlobalData.PLAYERS[1])) {
			player = new ComputerPlayer(ball, new NaiveStrategy());
		} else if (name.equals(GlobalData.PLAYERS[2])) {
			player = new ComputerPlayer(ball, new SmartStrategy());
		} else if (name.equals(GlobalData.PLAYERS[3])) {
			player = new ComputerPlayer(ball, new SmarterStrategy());		
		}
		return player;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o.equals(c)) {
			Command comm = (Command) arg;
			if (comm.equals(Commands.COM_NEWGAME)) {
				ui.setVisible(false);
				newGame = comm;	
				try {
					c.sendAck(newGame.getId(), new String(""));
					startGame();
				} catch (IOException e) {
					ui.addPopup("Send Ack", main.getClientName() + GlobalData.ERR_CLIENT_CONNECTION, true);
					goBack(false);
					e.printStackTrace();
				}
			}
		}			
	}
}
