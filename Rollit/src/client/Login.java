package client;

import java.io.IOException;
import java.util.Observable;

import exceptions.ProtecolNotFollowedException;

public class Login extends Observable {
	
	private ServerCommunicator c;
	private PopupUI pop;
	LoginGUI loginGUI;
	
	private String name;
	
	private String infoTitle = "Login";
	private String infoLoginOffline = " , you have succesfully logged in offline!";
	private String infoLoginSucces = " , you have succesfully logged in!";
	private String infoLoginError1 = " , you typed a wrong password!\n" +
			"If you are trying to create a new account, the username has already been taken.";
	private String infoLoginError2 = " , the server does not respond (appropriately).\n" +
			"Either the server is offline, or it uses a different communication protocol.";
	
	public Login() {
		loginGUI = new LoginGUI(this);
	}

	public boolean tryLogin(String name, String password) {
		this.name = name;
		try {
			c = new ServerCommunicator(name);
		} catch (IOException e) {
			loginGUI.addPopup(infoTitle, name + infoLoginError2, true);
			e.printStackTrace();
			return false;
		}
		boolean succes;
		try {
			succes = c.login(name, password);
			if (succes) {
				loginGUI.addPopup(infoTitle, name + infoLoginSucces, false);
				notifyObservers(c);
				return true;
			} else {
				loginGUI.addPopup(infoTitle, name + infoLoginError1, true);
				return false;
			}
		} catch (ProtecolNotFollowedException | IOException e) {
			loginGUI.addPopup(infoTitle, name + infoLoginError2, true);
			e.printStackTrace();
			return false;
		}
	}
	
	public void goOffline(String name) {
		this.name = name;
		loginGUI.addPopup(infoTitle, name + infoLoginOffline, false);
		notifyObservers(null);
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public void notifyObservers(Object arg) {
		setChanged();
		super.notifyObservers(arg);
	}
		
}
