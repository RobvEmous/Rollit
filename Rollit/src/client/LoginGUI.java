package client;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import clientAndServer.GlobalSettings;
import clientAndServer.Tools;

/**
 * Login gui. A GUI for the login screen presented to the user at at startup.
 * 
 * @author  Rob van Emous
 * @version v0.5
 */
public class LoginGUI extends JFrame implements ActionListener, KeyListener, PopupUI {
	private static final long serialVersionUID = -482499099902918937L;
	
	private static final Insets PADDING = new Insets(5, 5, 5, 5);
	
	private Dimension windowSize = new Dimension(450, 175);
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	private JTextField adress;
	private String adressToolTip = "Fill in an ip-adress to connect to";
	
	private JTextField port;
	private String portToolTip = "Fill in a port to connect to";
	
	private JTextField name;
	private String nameToolTip = "Fill in your username or a new username";
	
	private JPasswordField password;
	private String passToolTip = "Fill in your password or a new password";
	
	
	private JButton bLogin;
	private JButton bOffline;
	
	private Login login;
	
	private boolean adressTyped = true;
	private boolean portTyped = true;
	private boolean nameTyped = false;
	private boolean passwordTyped = false;
	
	/** Constructs a LoginGUI object. */
	public LoginGUI(Login login) {
		super("Rolit Login");
		this.login = login;
		buildGUI();
		setVisible(true);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
			}
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	/** builds the GUI. */
	public void buildGUI() {
		setSize(windowSize);
		setLocation(Tools.getCenterLocation(screenSize, windowSize));
		
		// declare and create menu
		JMenuBar menuBar = new JMenuBar();
		JMenu optionMenu = new JMenu("Options");
		JMenuItem helpItem = new JMenuItem("Help");
		helpItem.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				addPopup("Help", 
						"Fill in a username and password to login.\n" +
						"If the username is unknown a new account will be created.\n" +
						"If the server is not on your pc, you must supply another " +
						"address.\n" +
						"You can also play offline, this requires only a username.", false
						);
			}
		});
		optionMenu.add(helpItem);
		menuBar.add(optionMenu);
		setJMenuBar(menuBar);

		// declare all panels
		JPanel fullPanel = new JPanel(new FlowLayout());
		JPanel panels = new JPanel(new GridBagLayout());
		JPanel panelLabels = new JPanel(new GridLayout(4, 0));
		JPanel panelFields = new JPanel(new GridLayout(4, 0));
		JPanel panelButtons = new JPanel(new GridLayout(2, 0));
		
		GridBagConstraints panelLabelsC = new GridBagConstraints(
				0, 0, 1, 2, 1D, 1D, GridBagConstraints.CENTER, 0, PADDING, 0, 0);
		GridBagConstraints panelFieldsC = new GridBagConstraints(
				1, 0, 2, 2, 1D, 1D, GridBagConstraints.CENTER, 0, PADDING, 0, 0);
		GridBagConstraints panelButtonsC = new GridBagConstraints(
				3, 0, 1, 2, 1D, 1D, GridBagConstraints.CENTER, 0, PADDING, 0, 0);

		// create ip adress panel
		JLabel lbAdress = new JLabel("Adress: ");
		adress = new JTextField(login.getLocalHost(), 20);
		adress.setToolTipText(adressToolTip);
		adress.addKeyListener(this);
		
		panelLabels.add(lbAdress);
		panelFields.add(adress);	
		
		// create port panel
		JLabel lbPort = new JLabel("Port: ");
		port = new JTextField(GlobalSettings.PORT_NR + "", 20);
		port.setToolTipText(portToolTip);
		port.addKeyListener(this);
		
		panelLabels.add(lbPort);
		panelFields.add(port);	
		
		// create name panel
		JLabel lbName = new JLabel("Name: ");
		name = new JTextField("", 20);
		name.setToolTipText(nameToolTip);
		name.addKeyListener(this);
		
		panelLabels.add(lbName);
		panelFields.add(name);	
		
		// create pass panel
		JLabel lbPass = new JLabel("Password: ");
		password = new JPasswordField("", 20);
		password.setToolTipText(passToolTip);
		password.addKeyListener(this);
		
		panelLabels.add(lbPass);
		panelFields.add(password);	
		
		panels.add(panelLabels, panelLabelsC);
		panels.add(panelFields, panelFieldsC);
		
		// create button panel
		bLogin = new JButton("Login");
		bLogin.addActionListener(this);
		bLogin.setEnabled(false);
		
		bOffline = new JButton("Play offline");
		bOffline.addActionListener(this);
		bOffline.setEnabled(false);
		
		panelButtons.add(bLogin);
		panelButtons.add(bOffline);
		
		panels.add(panelButtons, panelButtonsC);
		fullPanel.add(panels);
		add(fullPanel);
	}

	/**
	 * listener for the "Login" and "Play offline" button
	 */
	public void actionPerformed(ActionEvent ev) {
		Object src = ev.getSource();
		if (src.equals(bLogin)) {
			String theAdress = adress.getText();
			String thePort = port.getText();
			String theName = name.getText();
			String thePass = new String(password.getPassword());
			if (login.tryLogin(theAdress, thePort, theName, thePass)) {
				setVisible(false);
				reset();
			}
		} else if (src.equals(bOffline)) {
			String theName = name.getText();
			login.goOffline(theName);
			setVisible(false);
			reset();
		}			
	}

	public void reset() {
		adress.setText("");
		port.setText("");
		name.setText("");
		password.setText("");
		adressTyped = false;
		portTyped = false;
		nameTyped = false;
		passwordTyped = false;
		updateLoginButton();
		updateOfflineButton();
	}

	@Override
	public void keyTyped(KeyEvent e) {	
		Object trigger = e.getSource();
		updateFieldBooleans(e, (JTextField) trigger);
		updateLoginButton();
		updateOfflineButton();
	}
	
	private void updateFieldBooleans(KeyEvent e, JTextField item) {
		String s = item.getText() + e.getKeyChar();
		boolean validInput = Tools.containsLetterOrNumber(s);
		if (item.equals(adress)) {
			adressTyped = validInput;
		} else if (item.equals(port)) {
			portTyped = validInput;
		} else if (item.equals(name)) {
			nameTyped = validInput;
		} else if (item.equals(password)) {
			passwordTyped = validInput;
		} 	
	}
	
	private void updateLoginButton() {
		if (adressTyped && portTyped && nameTyped && passwordTyped && !bLogin.isEnabled()) {
			bLogin.setEnabled(true);
		} else if ((!adressTyped || !portTyped || !nameTyped || !passwordTyped) && bLogin.isEnabled()) {
			bLogin.setEnabled(false);
		}
	}
	
	private void updateOfflineButton() {	
		if (nameTyped && !bOffline.isEnabled()) {
			bOffline.setEnabled(true);
		} else if (!nameTyped && bOffline.isEnabled()) {
			bOffline.setEnabled(false);
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}
	
	@Override
	public void addPopup(String title, String message, boolean warning) {
		if (!warning) {
			JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
		}
		
	}

}
