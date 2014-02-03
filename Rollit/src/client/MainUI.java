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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import clientAndServer.Tools;

/**
 * The main gui of the Rolit game.
 * 
 * @author  Rob van Emous
 * @version v1.0
 */
public class MainUI extends JFrame implements ActionListener, PopupUI {
	private static final long serialVersionUID = 5334287799998677921L;

	private static final Insets PADDING = new Insets(5, 5, 5, 5);
	
	private Dimension windowSize = new Dimension(410, 330);
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	private JMenu optionMenu;
	private JMenuItem logoutItem;
	private JMenuItem helpItem;
	
	private JButton bJoin;
	private JButton bChallenge;
	private JButton bHighscores;
	private JButton bOfflineGame;
	private JButton bExit;
	
	private Main main;
			
	/** Constructs a MainGUI object. */
	public MainUI(Main main) {
		super("Rollit main");
		buildGUI();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((MainUI) e.getWindow()).main.exit();
			}
		});
		this.main = main;
	}

	/** builds the GUI. */
	public void buildGUI() {
		setSize(windowSize);
		setLocation(Tools.getCenterLocation(screenSize, windowSize));
		
		// declare and create menu
		JMenuBar menuBar = new JMenuBar();
		optionMenu = new JMenu("Options");
		logoutItem= new JMenuItem("Logout");
		logoutItem.addActionListener(this);
		helpItem= new JMenuItem("Help");
		helpItem.setPreferredSize(new Dimension(5,5));
		helpItem.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				addPopup("Help", 
						"This is the main menu of Rolit\n" +
						"You must first login to be able to play the game.\n" +
						"Either offline (only requires a username) or online.\n" +
						"We hope you will enjoy the game!", false
						);		
			}
		});
		optionMenu.add(logoutItem);
		menuBar.add(optionMenu);
		menuBar.add(helpItem);
		
		setJMenuBar(menuBar);

		// declare all panels
		JPanel fullPanel = new JPanel(new FlowLayout());
		JPanel panels = new JPanel(new GridBagLayout());
		JPanel pOnline = new JPanel(new GridLayout(3, 0));
		JPanel pOffline = new JPanel(new GridLayout(1, 0));
		JPanel pExit = new JPanel(new GridLayout(1, 0));
		
		GridBagConstraints onlineLabelC = new GridBagConstraints(
				0, 0, 3, 1, 1D, 1D, GridBagConstraints.CENTER, 0, PADDING, 0, 0);
		GridBagConstraints onlineC = new GridBagConstraints(
				1, 1, 1, 3, 1D, 1D, GridBagConstraints.CENTER, 0, PADDING, 0, 0);
		GridBagConstraints sep1 = new GridBagConstraints(
				0, 5, 3, 1, 1D, 1D, GridBagConstraints.CENTER, 0, PADDING, 0, 0);
		sep1.fill = GridBagConstraints.BOTH;
		GridBagConstraints offlineLabelC = new GridBagConstraints(
				0, 6, 3, 1, 1D, 1D, GridBagConstraints.CENTER, 0, PADDING, 0, 0);
		GridBagConstraints offlineC = new GridBagConstraints(
				1, 7, 1, 1, 1D, 1D, GridBagConstraints.CENTER, 0, PADDING, 0, 0);
		GridBagConstraints sep2 = new GridBagConstraints(
				0, 9, 3, 1, 1D, 1D, GridBagConstraints.CENTER, 0, PADDING, 0, 0);
		sep2.fill = GridBagConstraints.HORIZONTAL;
		GridBagConstraints exitC = new GridBagConstraints(
				1, 10, 2, 1, 1D, 1D, GridBagConstraints.CENTER, 0, PADDING, 0, 0);

		// create online panel
		JLabel lbOnline = new JLabel("Online", JLabel.CENTER);
		bJoin = new JButton("Play a game");	
		bJoin.addActionListener(this);		
		bChallenge = new JButton("Challenge friends");	
		bChallenge.setToolTipText("Not implemented");
		bChallenge.addActionListener(this);	
		bHighscores = new JButton("Highscores");	
		bHighscores.setToolTipText("Not implemented");
		bHighscores.addActionListener(this);
		
		pOnline.add(bJoin);
		pOnline.add(bChallenge);
		pOnline.add(bHighscores);
		
		// create offline panel		
		JLabel lbOffline = new JLabel("Offline", JLabel.CENTER);
		bOfflineGame = new JButton("Play offline");
		bOfflineGame.addActionListener(this);
		
		pOffline.add(bOfflineGame);
		
		// create exit panel
		bExit = new JButton("Exit");
		bExit.addActionListener(this);
		
		pExit.add(bExit);
		
		//reset button states
		reset();
		
		// join all panels		
		panels.add(lbOnline, onlineLabelC);
		panels.add(pOnline, onlineC);
		JSeparator seperator1 = new JSeparator(JSeparator.HORIZONTAL);
		seperator1.setPreferredSize(new Dimension(getPreferredSize().width, 5));
		panels.add(seperator1, sep1);
		panels.add(lbOffline, offlineLabelC);
		panels.add(pOffline, offlineC);
		JSeparator seperator2 = new JSeparator(JSeparator.HORIZONTAL);
		seperator2.setPreferredSize(new Dimension(this.getWidth(), 5));
		panels.add(seperator2, sep2);
		panels.add(pExit, exitC);
		fullPanel.add(panels);
		add(fullPanel);
	}

	/**
	 * listener for the "Login" and "Play offline" button
	 */
	public void actionPerformed(ActionEvent ev) {
		Object src = ev.getSource();		
		if (src.equals(logoutItem)) {
			main.logout(true);
		} else if (src.equals(bJoin)) {
			main.join(0);
		} else if (src.equals(bChallenge)) {
			//main.challenge(); NOT IMPLEMENTED
		} else if (src.equals(bHighscores)) {	
			//main.highScores(); NOT IMPLEMENTED
		} else if (src.equals(bOfflineGame)) {
			main.offlineGame();
		} else if (src.equals(bExit)) {
			main.exit();
		}
	}

	public void reset() {
		bJoin.setEnabled(false);
		bChallenge.setEnabled(false);
		bHighscores.setEnabled(false);
		bOfflineGame.setEnabled(false);
	}
	

	public void setLoggedIn(boolean online) {
		bJoin.setEnabled(online);
		bChallenge.setEnabled(false); //TODO challenge not working yet
		bHighscores.setEnabled(false);  //TODO highscores not working yet
		bOfflineGame.setEnabled(true);
	}
	
	public void toRightCorner() {
		setLocation(Tools.getRightBottomLocation(screenSize, windowSize));
	}
	
	@Override
	public synchronized void addPopup(String title, String message, boolean warning) {
		if (!warning) {
			JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
		}	
	}
	
	public synchronized void addCenteredPopup(String title, String message, boolean warning) {
		if (!warning) {
			JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		}	
	}

}
