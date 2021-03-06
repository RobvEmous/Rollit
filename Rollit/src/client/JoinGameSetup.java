package client;

import java.io.IOException;
import java.util.ArrayList;
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
		this.c.addObserver(this);
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
			ui.addPopup(infoTitle, main.getClientName() + ", disjoined succesful!", false);
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
			Ball[] allBalls = Ball.values();
			GamePlayer playerTemp = null;
			ArrayList<String> otherPlayersList = new ArrayList<String>();
			for (int i = 0; i < allPlayers.length; i++) {
				if (allPlayers[i].equals(clientname)) {
					playerTemp = nameToPlayer(choice, allBalls[i]);
				} else {
					otherPlayersList.add(allPlayers[i]);
				}
			}
			final GamePlayer player = playerTemp;
			final String[] otherPlayers = 
					(String[])otherPlayersList.toArray(
							new String[otherPlayersList.size()]);
			if (allPlayers.length != nrOfPlayers || player == null) {
				throw new ProtocolNotFollowedException();
			} 		
			ui.dispose();
			c.deleteObserver(this);
			// from here the gameSetting is final and an online game will be started.	
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
	
	private GamePlayer nameToPlayer(String name, Ball ball) {
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
			if (comm.getId().equals(Commands.COM_NEWGAME)) {
				ui.setVisible(false);
				try {				
					newGame = comm;
					startGame();	
					c.sendAck(comm.getId(), new String(""));
				} catch (IOException e) {
					ui.addPopup("Send Ack", main.getClientName() + GlobalData.ERR_CLIENT_CONNECTION, true);
					goBack(false);
					e.printStackTrace();
				}
			}
		}			
	}
}
