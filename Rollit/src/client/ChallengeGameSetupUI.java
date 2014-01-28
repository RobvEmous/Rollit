package client;

import game.GamePlayer;
import game.HumanPlayer;

import java.awt.Choice;
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

import javax.jws.Oneway;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import clientAndServer.Tools;

public class ChallengeGameSetupUI extends JFrame implements ActionListener, ItemListener {
	private static final long serialVersionUID = -1704679785722871219L;
	
	private static final Insets PADDING = new Insets(5, 5, 5, 5);
	
	private Dimension windowSize = new Dimension(230, 300);
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	private static final String TITLE = "Setup";
	
	private ChallengeGameSetup gameSetup;
	
	private String[] choice = new String[4];
	
	private int numberOfPlayers = 2; 
	
	private Choice selectNrOfPlayers;
	private Choice[] selectPlayers = new Choice[4];
	
	private JButton startGame;
	private JButton back;

	public ChallengeGameSetupUI(ChallengeGameSetup gameSetup) {
		super(TITLE);
		this.gameSetup = gameSetup;	
		buildGUI();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((ChallengeGameSetupUI) e.getWindow()).close();
			}
		});
		setUp(2);	
	}
	
	public void close() {
		gameSetup.goBack();
	}

	private void buildGUI() {
		setSize(windowSize);
		setLocation(Tools.getCenterLocation(screenSize, windowSize));
		
		// declare all panels
		JPanel fullPanel = new JPanel(new FlowLayout());
		JPanel panels = new JPanel(new GridBagLayout());
		JPanel panelNrOfPlayers = new JPanel(new GridLayout(2, 0));
		JPanel panelPlayers = new JPanel(new GridLayout(5, 0));
		JPanel panelButton = new JPanel(new GridLayout(2, 0));
		
		GridBagConstraints panelNrOfPlayersC = new GridBagConstraints(
				0, 0, 2, 2, 1D, 1D, GridBagConstraints.CENTER, 0, PADDING, 0, 0);
		GridBagConstraints panelPlayersC = new GridBagConstraints(
				0, 2, 2, 5, 1D, 1D, GridBagConstraints.CENTER, 0, PADDING, 0, 0);
		GridBagConstraints panelButtonC = new GridBagConstraints(
				0, 7, 2, 2, 1D, 1D, GridBagConstraints.CENTER, 0, PADDING, 0, 0);

		// create nrOfPlayers panel
		JLabel lbNrOfPlayers = new JLabel("Number of players: ");
		selectNrOfPlayers = new Choice();
		selectNrOfPlayers.add("2");
		selectNrOfPlayers.add("3");
		selectNrOfPlayers.add("4");
		selectNrOfPlayers.addItemListener(this);
		
		panelNrOfPlayers.add(lbNrOfPlayers);
		panelNrOfPlayers.add(selectNrOfPlayers);
		
		// create Players panel
		JLabel lbPlayers = new JLabel("Select the players: ");
		
		selectPlayers[0] = new Choice();
		selectPlayers[1] = new Choice();
		selectPlayers[2] = new Choice();
		selectPlayers[3] = new Choice();
		
		selectPlayers[0].addItemListener(this);
		selectPlayers[1].addItemListener(this);
		selectPlayers[2].addItemListener(this);
		selectPlayers[3].addItemListener(this);
		
		addAllPlayers(selectPlayers);
	
		panelPlayers.add(lbPlayers);
		panelPlayers.add(selectPlayers[0]);
		panelPlayers.add(selectPlayers[1]);
		panelPlayers.add(selectPlayers[2]);
		panelPlayers.add(selectPlayers[3]);
		
		// create button panel
		startGame = new JButton("Start game");
		back = new JButton("Back to main");
		startGame.addActionListener(this);
		back.addActionListener(this);
		
		panelButton.add(startGame);
		panelButton.add(back);
		
		panels.add(panelNrOfPlayers, panelNrOfPlayersC);
		panels.add(panelPlayers, panelPlayersC);
		panels.add(panelButton, panelButtonC);
		fullPanel.add(panels);
		add(fullPanel);	
	}

	private void setUp(int nrOfPlayers) {
		for (int i = 0; i < nrOfPlayers; i++) {
			selectPlayers[i].setEnabled(true);
		}
		for (int i = nrOfPlayers; i < 4; i++) {
			selectPlayers[i].setEnabled(false);
		}
		numberOfPlayers = nrOfPlayers;
	}
	
	private void addAllPlayers(Choice[] cs) {
		for (Choice c : cs) {
			addPlayers(c);
		}
	}
	
	private void addPlayers(Choice c) {
		for (String s : gameSetup.playersKinds) {
			c.add(s);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(back)) {
			close();
		} else if (e.getSource().equals(startGame)) {
			String[] finalChoice = new String[numberOfPlayers];
			for (int i = 0; i < numberOfPlayers; i++) {
				finalChoice[i] = selectPlayers[i].getSelectedItem();
			}
			gameSetup.startGame(finalChoice);
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource().equals(selectNrOfPlayers)) {
			String item = selectNrOfPlayers.getSelectedItem();
			setUp(Integer.parseInt(item));
		} else {
			for (int i = 0; i < numberOfPlayers; i++) {
				if (e.equals(selectPlayers[i]))	{
					choice[1] = selectPlayers[i].getSelectedItem();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		ChallengeGameSetupUI ui = new ChallengeGameSetupUI(new ChallengeGameSetup(new Main()));
		ui.setVisible(true);
	}
	
	
}
