package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;

import clientAndServer.GlobalData;

import exceptions.NotSameStateException;
import exceptions.ProtocolNotFollowedException;

public class Login extends Observable {
	
	private ServerCommunicator c;
	LoginGUI loginGUI;
	
	private String name;
	
	private String infoTitle = "Login";
	private String infoLoginOffline = " , you have succesfully logged in offline!";
	private String infoLoginSucces = " , you have succesfully logged in!";
	private String infoAdressError = " , you did not type a valid ip-adress!";
	private String infoPortError = " , you did not type a valid port!";
	private String infoLoginError1 = " , you typed a wrong password!\n" +
			"If you are trying to create a new account, the username has already been taken.";

	public Login() {
		loginGUI = new LoginGUI(this);
	}

	public boolean tryLogin(String theAdress, String thePort, String theName, String thePass) {
		this.name = theName;
		InetAddress adress = null;
		int port = 0;
		try {
			adress = InetAddress.getByName(theAdress);
		} catch (UnknownHostException e) {
			loginGUI.addPopup("Adress invalid", "Adress: " 
					+ adress + infoAdressError, true);
			return false;
		}
		try {
			port = Integer.parseInt(thePort);
			if (port < 0 || port > 65535) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			loginGUI.addPopup("Port invalid", "Port: " 
					+ thePort + infoPortError, true);
			e.printStackTrace();
			return false;
		}
		try {
			c = new ServerCommunicator(name, adress, port);
		} catch (IOException e) {
			loginGUI.addPopup(infoTitle, name + GlobalData.ERR_CLIENT_CONNECTION, true);
			e.printStackTrace();
			return false;
		}
		boolean succes;
		try {
			succes = c.login(name, thePass);
			if (succes) {
				loginGUI.addPopup(infoTitle, name + infoLoginSucces, false);
				notifyObservers(c);
				return true;
			} else {
				loginGUI.addPopup(infoTitle, name + infoLoginError1, true);
				return false;
			}
		} catch (ProtocolNotFollowedException e) {
			loginGUI.addPopup(infoTitle, name + GlobalData.ERR_PROTECOL, true);
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			loginGUI.addPopup(infoTitle, name + GlobalData.ERR_CLIENT_CONNECTION, true);
			e.printStackTrace();
			return false;
		} catch (NotSameStateException e) {
			loginGUI.addPopup(infoTitle, name + GlobalData.ERR_STATE, true);
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

	public String getLocalHost() {
		String localhost = null;
		try {
			localhost = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			loginGUI.addPopup(infoTitle, name + "this shoud never happen", true);
			e1.printStackTrace();
		}
		return localhost;
	}
		
}
