package client;

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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import clientAndServer.GlobalData;
import clientAndServer.Tools;

public class JoinGameSetupUI extends JFrame implements PopupUI, ActionListener {
	private static final long serialVersionUID = -1704679785722871219L;
	
	private static final Insets PADDING = new Insets(5, 5, 5, 5);
	
	private Dimension windowSize = new Dimension(200, 230);
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	private static final String TITLE = "Setup";
	
	private JoinGameSetup gameSetup;
	
	private boolean waitingForPlayers = false;
	
	private Choice selectNrOfPlayers;
	private Choice selectPlayAs;
	
	private JButton startGame;
	private JButton back;

	public JoinGameSetupUI(JoinGameSetup gameSetup) {
		super(TITLE);
		this.gameSetup = gameSetup;	
		buildGUI();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((JoinGameSetupUI) e.getWindow()).close();
			}
		});
	}
	
	public void close() {
		if (waitingForPlayers) {
			gameSetup.disjoinGame();
		}
		gameSetup.goBack(false);
	}

	private void buildGUI() {
		setSize(windowSize);
		setLocation(Tools.getCenterLocation(screenSize, windowSize));
		
		// declare all panels
		JPanel fullPanel = new JPanel(new FlowLayout());
		JPanel panels = new JPanel(new GridBagLayout());
		JPanel panelNrOfPlayers = new JPanel(new GridLayout(2, 0));
		JPanel panelPlayAs = new JPanel(new GridLayout(2, 0));
		JPanel panelButton = new JPanel(new GridLayout(2, 0));
		
		GridBagConstraints panelNrOfPlayersC = new GridBagConstraints(
				0, 0, 2, 2, 1D, 1D, GridBagConstraints.CENTER, 0, PADDING, 0, 0);
		GridBagConstraints panelPlayAsC = new GridBagConstraints(
				0, 2, 2, 5, 1D, 1D, GridBagConstraints.CENTER, 0, PADDING, 0, 0);
		GridBagConstraints panelButtonC = new GridBagConstraints(
				0, 7, 2, 2, 1D, 1D, GridBagConstraints.CENTER, 0, PADDING, 0, 0);

		// create nrOfPlayers panel
		JLabel lbNrOfPlayers = new JLabel("Number of players: ");
		selectNrOfPlayers = new Choice();
		selectNrOfPlayers.add("2");
		selectNrOfPlayers.add("3");
		selectNrOfPlayers.add("4");
		
		panelNrOfPlayers.add(lbNrOfPlayers);
		panelNrOfPlayers.add(selectNrOfPlayers);
		
		// create play-as panel
		JLabel lbPlayAs = new JLabel("Play as: ");
		selectPlayAs = new Choice();
		addPlayers(selectPlayAs);
		
		panelPlayAs.add(lbPlayAs);
		panelPlayAs.add(selectPlayAs);
		
		// create button panel
		startGame = new JButton("Join game");
		back = new JButton("Back to main");
		startGame.addActionListener(this);
		back.addActionListener(this);
		
		panelButton.add(startGame);
		panelButton.add(back);
		
		panels.add(panelNrOfPlayers, panelNrOfPlayersC);
		panels.add(panelPlayAs, panelPlayAsC);
		panels.add(panelButton, panelButtonC);
		fullPanel.add(panels);
		add(fullPanel);
		
	}

	private void addPlayers(Choice c) {
		for (String s : GlobalData.PLAYERS) {
			c.add(s);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(back)) {
			if (!waitingForPlayers) {
				close();
			} else {
				back.setEnabled(false);	
				gameSetup.disjoinGame();
				startGame.setEnabled(true);	
				setJoined(false);
				startGame.setEnabled(true);
				waitingForPlayers = false;
				back.setEnabled(true);	
				
			}
		} else if (e.getSource().equals(startGame)) {
			int numberOfPlayers = Integer.parseInt(selectNrOfPlayers.getSelectedItem());
			String playerChoice = selectPlayAs.getSelectedItem();
			startGame.setEnabled(false);	
			back.setEnabled(false);	
			gameSetup.joinGame(playerChoice, numberOfPlayers);
			setJoined(true);
			back.setEnabled(true);	
			waitingForPlayers = true;
		}
	}

	public void setJoined(boolean join) {
		if (join) {
			back.setText("Disjoin");
			selectNrOfPlayers.setEnabled(false);
			selectPlayAs.setEnabled(false);
		} else {
			back.setText("Back to main");
			selectNrOfPlayers.setEnabled(true);
			selectPlayAs.setEnabled(true);
		}
	}

	@Override
	public synchronized void addPopup(String title, String message, boolean warning) {
		if (!warning) {
			JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
		}	
	}
}
