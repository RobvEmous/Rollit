package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.MenuBar;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Login gui. A GUI for the login screen presented to the user at at startup.
 * @author  Rob van Emous
 * @version v0.5
 */
public class LoginGUI extends JFrame implements ActionListener, KeyListener, PopupUI {
	private static final long serialVersionUID = -482499099902918937L;
	
	private static final Insets PADDING = new Insets(5, 5, 5, 5);
	
	private int screenWidth = 410;
	private int screenHeight = 165;
	private Dimension windowSize = new Dimension(screenWidth, screenHeight);
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	private JButton bLogin;
	private JTextField name;
	private String nameToolTip = "Fill in your username or a new username";
	
	private JPasswordField password;
	private String passToolTip = "Fill in your password or a new password";
	
	private JButton bOffline;
	
	private String messageInit = "Must be connected first!";	
	
	private Login login;
	
	private boolean nameTyped = false;
	private boolean passwordTyped = false;
	
	
	/** Constructs a ServerGUI object. */
	public LoginGUI() {
		super("Rollit Login");
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
		setSize(screenWidth, screenHeight);
		setWindowLocation();
		
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
		JPanel panelLabels = new JPanel(new GridLayout(2, 0));
		JPanel panelFields = new JPanel(new GridLayout(2, 0));
		
		GridBagConstraints panelLabelsC = new GridBagConstraints(
				0, 0, 1, 2, 1D, 1D, GridBagConstraints.CENTER, 0, PADDING, 0, 0);
		GridBagConstraints panelFieldsC = new GridBagConstraints(
				1, 0, 2, 2, 1D, 1D, GridBagConstraints.CENTER, 0, PADDING, 0, 0);
		GridBagConstraints panelLoginC = new GridBagConstraints(
				3, 0, 1, 2, 1D, 1D, GridBagConstraints.CENTER, 0, PADDING, 0, 0);
		GridBagConstraints panelOfflineC = new GridBagConstraints(
				2, 2, 1, 1, 1D, 1D, GridBagConstraints.CENTER, 0, PADDING, 0, 0);

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
		
		// create login panel
		bLogin = new JButton("Login");
		bLogin.addActionListener(this);
		bLogin.setEnabled(false);
		
		panels.add(bLogin, panelLoginC);
		
		// create offline panel
		bOffline = new JButton("Play offline");
		bOffline.addActionListener(this);
		bOffline.setEnabled(false);
		
		panels.add(bOffline, panelOfflineC);
		fullPanel.add(panels);
		add(fullPanel);
	}

	/**
	 * listener for the "Connect" button
	 */
	public void actionPerformed(ActionEvent ev) {
		Object src = ev.getSource();
	}

	/** pops up a message to the user  */
	public void addPopupMessage(String title, String msg, boolean warning) {
		if (!warning) {
			JOptionPane.showMessageDialog(this, msg, title, JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, msg, title, JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void reset() {
		name.setText("");
		password.setText("");
		bLogin.setEnabled(false);
		bOffline.setEnabled(false);
	}

	@Override
	public void keyTyped(KeyEvent e) {	
		Object trigger = e.getSource();
		updateFieldBooleans(e, (JTextField) trigger);
		updateLoginButton(e, (JTextField) trigger);
		updateOfflineButton(e, (JTextField) trigger);
	}
	
	private void updateFieldBooleans(KeyEvent e, JTextField item) {
		String s = item.getText() + e.getKeyChar();
		boolean validInput = Tools.containsLetterOrNumber(s);
		if (item.equals(name)) {
			nameTyped = validInput;
		} else if (item.equals(password)) {
			passwordTyped = validInput;
		} 	
	}
	
	private void updateLoginButton(KeyEvent e, JTextField item) {
		if (nameTyped && passwordTyped && !bLogin.isEnabled()) {
			bLogin.setEnabled(true);
		} else if ((!nameTyped || !passwordTyped) && bLogin.isEnabled()) {
			bLogin.setEnabled(false);
		}
	}
	
	private void updateOfflineButton(KeyEvent e, JTextField item) {	
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
	
	private void setWindowLocation() {
		setLocation(
		(int)(screenSize.getWidth() / 2 - windowSize.width / 2),
		(int)(screenSize.getHeight() / 2 - windowSize.height / 2)
		);
	}
	
	@Override
	public void addPopup(String title, String message, boolean warning) {
		if (!warning) {
			JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	/** Start a LoginGUI application */
	public static void main(String[] args) {
		LoginGUI gui = new LoginGUI();
	}

}
