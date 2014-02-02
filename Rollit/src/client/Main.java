package client;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.jws.Oneway;

import exceptions.NotSameStateException;
import exceptions.ProtocolNotFollowedException;

/**
 * This is the heart of the client-side of the Rollit program. It is 
 * directly linked to the MainUI and the ServerCommunicator and is 
 * therefore responsible for communicating the User's actions to the 
 * server (e.a. join a game or get certain highscores).
 * 
 * @author Rob van Emous
 * @version 0.2
 */
public class Main implements Observer {
	
	private String clientName;
	private boolean isLoggedInOnline = false;
	
	private ServerCommunicator c = null;
	
	private MainUI mainUI;
	
	private Login login;
	
	public Main() {
		start();
		login();
	}	
	
	private void start() {
		mainUI = new MainUI(this);	
	}
	
	private void restart(boolean online) {
		mainUI.setLoggedIn(online);
		mainUI.setVisible(true);
		mainUI.requestFocus();
	}
	
	public void login() {	
		mainUI.setVisible(false);
		login = new Login();
		login.addObserver(this);
	}
	
	public void returnFromAction() {
		mainUI.setVisible(true);
	}

	public void logout(boolean login) {
		String subject = "Logout";
		try {
			if (isLoggedInOnline) {
				c.logout();
			}
			if (login) {
				mainUI.addPopup(subject, "The logout was succesfull", false);
				login();
			}
		} catch (ProtocolNotFollowedException e) {
			printPopupError(subject, e);
			e.printStackTrace();
		} catch (IOException e) {
			printPopupError(subject, e);
			if (login) {
				login();
			}
			e.printStackTrace();
		} catch (NotSameStateException e) {
			printPopupError(subject, e);
			e.printStackTrace();
		}

	}
	
	public void join(int nrOfPlayers) {
		mainUI.setVisible(false);
		JoinGameSetup setup = new JoinGameSetup(this, c);	
	}

	public void challenge() {
		mainUI.setVisible(false);
		ChallengeGameSetup setup = new ChallengeGameSetup(this, c);
	}

	public void offlineGame() {
		mainUI.setVisible(false);
		OfflineGameSetup setup = new OfflineGameSetup(this);
	}

	public void highScores() {
		// TODO Auto-generated method stub	
	}

	public void exit() {
		if (isLoggedInOnline) {
			logout(false);
			c.shutdown();
		}
		//mainUI.toRightCorner();
		//mainUI.addCenteredPopup("Goodbye? :'(", "*Sobbing in a corner quietly*", false);
		System.exit(0);	
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o.equals(login)) {
			clientName = login.getName();
			if (arg != null) {
				c = (ServerCommunicator) arg;
				isLoggedInOnline = true;
			}  else {
				isLoggedInOnline = false;
			}
			restart(isLoggedInOnline);
		}
		
	}
	
	public String getClientName() {
		return clientName;
	}
	
	private void printPopupError(String subject, Exception e) {
		if (e instanceof ProtocolNotFollowedException) {
			mainUI.addPopup(subject, subject + " unsuccessfull: server doesn't follow the protocol!", true);
		} else if (e instanceof IOException) {
			mainUI.addPopup(subject, "Logout successfull, but the server is offline!", true);
		}
	}
	
	private void printPopupSucces(String subject) {
		mainUI.addPopup(subject, subject + " succesfull!", false);
	}

	public static void main(String[] args) {
		Main main = new Main();
	}

}
