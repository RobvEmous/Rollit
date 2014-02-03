package game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import client.PopupUI;
import clientAndServer.Ball;
import clientAndServer.Board;
import clientAndServer.Tools;

/**
 * <h1>The GUI for the Rolit game.</h1>
 * @author René Nijhuis
 * @version 0.6
 */
public class OnlineGameUI extends JFrame implements ActionListener, KeyListener, PopupUI {
	private static final long serialVersionUID = 5844574958336659575L;
	
	private Dimension windowSize = new Dimension(800,600);
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private Dimension oldSize = windowSize;
	
	private OnlineGame game;
	
	private boolean hasHuman;
	
	private boolean messageTyped = false;

	private FieldListener fieldListener;
	
	private static final Insets inset = new Insets(0,0,0,0);

	private JPanel fullPanel;
	private JPanel optionsPanel;
	private JPanel ballCountPanel;
	private JPanel fieldPanel;
		
	private JMenuItem hintItem;
	private JMenuItem quitItem;
	private JMenuItem fullScreenItem;
		
	private FieldButton[][] field;
	
	private JTextPane stateField;
	private JTextArea chatBox;
	private JTextField chat;
	private int[] ballCounts = new int[] {0, 0, 0, 0};
	private JLabel ballCountRedLabel;
	private JLabel ballCountBlueLabel;
	private JLabel ballCountYellowLabel;
	private JLabel ballCountGreenLabel;
	
	private FieldButton oldHint = null;

	private Color backgroundColor = Color.BLACK;
	private Color foregroundColor = Color.WHITE;
	private Color textBoxColor = Color.GRAY;
	
	private Cursor redCursor;
	private Cursor blueCursor;
	private Cursor yellowCursor;
	private Cursor greenCursor;
	private Cursor idleCursor;
	
	public OnlineGameUI(OnlineGame g, boolean hasHuman){
		super("Rolit");
		game = g;
		this.hasHuman = hasHuman;
		initialize();	
	}
	
	/**
	 * This method initializes the entire GUI.</br>
	 * It makes a call to the methods <code>initListeners</code> and <code> createField</code>.
	 * Furthermore it creates two panels and adds them to the frame that is this UI, one for
	 * the field and its buttons and one for the menu on the side .
	 */
	private void initialize() {
		setSize(windowSize);
		setLocation(Tools.getCenterLocation(screenSize, windowSize));
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((OnlineGameUI) e.getWindow()).close();
			}
		});
		
		fullPanel = new JPanel(new BorderLayout());
		fullPanel.setBackground(backgroundColor);
		
		add(fullPanel);
		
		createMenu();
		createOptionsPanel();
		createField();
		createCursors();
		
		if (!hasHuman) {
			deactivateBoard();
			setCursorIfChanged(idleCursor);
		}
		setVisible(true);		
	}
	
	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		
		// create and set up game menu		
		JMenu gameMenu = new JMenu("Game");
		
		if (hasHuman) {
			hintItem = new JMenuItem("Hint");
			hintItem.setToolTipText("A random field you can use will be highlighted");
			hintItem.addActionListener(this);
			gameMenu.add(hintItem);
		}
		
		quitItem = new JMenuItem("Quit Game");
		quitItem.setToolTipText("This will stop the game instantly (same as pressing close)");
		quitItem.addActionListener(this);
		gameMenu.add(quitItem);
		
		menuBar.add(gameMenu);
		
		// create and set up options menu
		JMenu optionsMenu = new JMenu("Options");
		fullScreenItem = new JMenuItem("Full Screen");
		fullScreenItem.addActionListener(this);
		optionsMenu.add(fullScreenItem);
		
		menuBar.add(optionsMenu);
		
		setJMenuBar(menuBar);
	}

	private void createCursors() {
		Toolkit tool = Toolkit.getDefaultToolkit();
		Image idleCursorImage = tool.getImage("Pictures/BallNone32.png");
		idleCursor = tool.createCustomCursor(idleCursorImage, new Point(16,16), "ballCursor");
		if (hasHuman) {
			Image redCursorImage = tool.getImage("Pictures/BallRed32.png");
			Image blueCursorImage = tool.getImage("Pictures/BallBlue32.png");
			Image yellowCursorImage = tool.getImage("Pictures/BallYellow32.png");
			Image greenCursorImage = tool.getImage("Pictures/BallGreen32.png");
			redCursor = tool.createCustomCursor(redCursorImage, new Point(16,16), "ballCursor");
			blueCursor = tool.createCustomCursor(blueCursorImage, new Point(16,16), "ballCursor");
			yellowCursor = tool.createCustomCursor(yellowCursorImage, new Point(16,16), "ballCursor");
			greenCursor = tool.createCustomCursor(greenCursorImage, new Point(16,16), "ballCursor");
		}	
	}

	private void createOptionsPanel() {
		optionsPanel = new JPanel(new GridBagLayout());
		optionsPanel.setBackground(backgroundColor);
		optionsPanel.setPreferredSize(new Dimension(200, optionsPanel.getPreferredSize().height));
	
		fullPanel.add(optionsPanel, BorderLayout.EAST);
		
		stateField = new JTextPane();
		GridBagConstraints stateFieldC = new GridBagConstraints(0, 0, 1, 1, 1D, 1D,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, inset, 0, 0);
		stateField.setEditable(false);
		stateField.setBackground(backgroundColor);
		stateField.setForeground(foregroundColor);;
		StyledDocument doc = stateField.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		stateField.setToolTipText("This displays who has the turn or who has won the game");
		optionsPanel.add(stateField, stateFieldC);
		
		JSeparator seperator1 = new JSeparator(JSeparator.HORIZONTAL);
		GridBagConstraints sep1C = new GridBagConstraints(0, 1, 1, 1, 1D, 1D, 
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, inset, 0, 0);
		seperator1.setPreferredSize(new Dimension(getPreferredSize().width, 2));
		optionsPanel.add(seperator1, sep1C);
		
		createBallCountPanel();	

		JSeparator seperator2 = new JSeparator(JSeparator.HORIZONTAL);
		GridBagConstraints sep2C = new GridBagConstraints(0, 7, 1, 1, 1D, 1D, 
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, inset, 0, 0);
		seperator1.setPreferredSize(new Dimension(getPreferredSize().width, 2));
		optionsPanel.add(seperator2, sep2C);
		
		createChatPanel();

	}

	private void createChatPanel() {
		JPanel chatPanel = new JPanel(new GridBagLayout());
		chatPanel.setBackground(backgroundColor);
		GridBagConstraints chatPanelC = new GridBagConstraints(0, 8, 1, 4, 1D, 1D, 
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, inset, 0, 0);
		optionsPanel.add(chatPanel, chatPanelC);
		
		JLabel chatBoxLabel = new JLabel("Chat");
		chatBoxLabel.setForeground(foregroundColor);
		chatBoxLabel.setHorizontalAlignment(JLabel.CENTER);
		GridBagConstraints chatBoxLabelC = new GridBagConstraints(0, 0, 1, 1, 1D, 1D,
				GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, inset, 0, 0);
		optionsPanel.add(chatBoxLabel, chatBoxLabelC);
		
		chatBox = new JTextArea();
		chatBox.setBackground(textBoxColor);
		chatBox.setForeground(foregroundColor);
		chatBox.setLineWrap(true);
		chatBox.setWrapStyleWord(true);
		chatBox.setEditable(false);
		chatBox.setAutoscrolls(true);
		JScrollPane chatBoxScrollPane = new JScrollPane(chatBox);
		chatBoxScrollPane.setPreferredSize(new Dimension(getPreferredSize().width -5 , 100));
		GridBagConstraints chatBoxScrollPaneC = new GridBagConstraints(0, 1, 1, 3, 1D, 1D,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, inset, 0, 0);
		
		chat = new JTextField();
		chat.setBackground(textBoxColor);
		chat.setForeground(foregroundColor);
		chat.setPreferredSize(new Dimension(getPreferredSize().width -5, 30));
		chat.addKeyListener(this);
		GridBagConstraints chatConstraints = new GridBagConstraints(0, 4, 1, 1, 1D, 1D,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, inset, 0, 0);
		
		chatPanel.add(chatBoxLabel, chatBoxLabelC);
		chatPanel.add(chatBoxScrollPane, chatBoxScrollPaneC);
		chatPanel.add(chat, chatConstraints);
		
	}

	private void createBallCountPanel() {
		ballCountPanel = new JPanel(new GridBagLayout());
		ballCountPanel.setBackground(backgroundColor);
		
		// create panels
		JPanel ballCountNames = new JPanel(new GridLayout(4, 0));
		JPanel ballCountIcons = new JPanel(new GridLayout(4, 0));
		JPanel ballCountValues = new JPanel(new GridLayout(4, 0));
		
		ballCountNames.setBackground(backgroundColor);
		ballCountIcons.setBackground(backgroundColor);
		ballCountValues.setBackground(backgroundColor);
		
		GridBagConstraints ballCountPanelC = new GridBagConstraints(
				0, 2, 1, 5, 1D, 1D, GridBagConstraints.CENTER, GridBagConstraints.BOTH, inset, 0, 0);
		GridBagConstraints ballCountPanelTextC = new GridBagConstraints(
				0, 0, 4, 1, 1D, 1D, GridBagConstraints.CENTER, 0, inset, 0, 0);
		GridBagConstraints ballCountNamesC = new GridBagConstraints(
				0, 1, 1, 4, 1D, 1D, GridBagConstraints.CENTER, 0, inset, 0, 0);
		GridBagConstraints ballCountIconsC = new GridBagConstraints(
				1, 1, 1, 4, 1D, 1D, GridBagConstraints.CENTER, 0, inset, 0, 0);
		GridBagConstraints ballCountValuesC = new GridBagConstraints(
				2, 1, 1, 4, 1D, 1D, GridBagConstraints.CENTER, 0, inset, 0, 0);

		// create score label
		JLabel ballCountPanelText = new JLabel("Score");
		ballCountPanelText.setForeground(foregroundColor);
		ballCountPanelText.setHorizontalAlignment(JLabel.CENTER);
		ballCountPanelText.setToolTipText("The amount of balls each player has on the field.");
		ballCountPanel.add(ballCountPanelText, ballCountPanelTextC);

		// create scores per player and color
		GamePlayer[] players = (GamePlayer[]) Tools.addOneToStart(game.getServerPlayers(), game.getClient()).toArray(new GamePlayer[game.getNrOfPlayers()]);
		
		JLabel player1 = new JLabel(players[0].getName());
		ballCountRedLabel = new JLabel(ballCounts[0] + "");
		player1.setForeground(foregroundColor);
		ballCountRedLabel.setForeground(foregroundColor);
		JLabel ballCountRedIconLabel = new JLabel(new ImageIcon("Pictures/BallRed16.png"));
		
		JLabel player2 = new JLabel(players[1].getName());
		ballCountGreenLabel = new JLabel(ballCounts[1] + "");
		player2.setForeground(foregroundColor);
		ballCountGreenLabel.setForeground(foregroundColor);
		JLabel ballCountGreenIconLabel = new JLabel(new ImageIcon("Pictures/BallGreen16.png"));
		
		ballCountNames.add(player1);
		ballCountNames.add(player2);
		ballCountIcons.add(ballCountRedIconLabel);
		ballCountIcons.add(ballCountGreenIconLabel);
		ballCountValues.add(ballCountRedLabel);
		ballCountValues.add(ballCountGreenLabel);
		
		if (players.length > 2) {
			JLabel player3 = new JLabel(players[2].getName());
			ballCountYellowLabel = new JLabel(ballCounts[2] + "");
			player3.setForeground(foregroundColor);
			ballCountYellowLabel.setForeground(foregroundColor);
			JLabel ballCountYellowIconLabel = new JLabel(new ImageIcon("Pictures/BallYellow16.png"));
			ballCountNames.add(player3);
			ballCountIcons.add(ballCountYellowIconLabel);
			ballCountValues.add(ballCountYellowLabel);
		}
		if (players.length > 3) {
			JLabel player4 = new JLabel(players[3].getName());
			ballCountBlueLabel = new JLabel(ballCounts[3] + "");
			player4.setForeground(foregroundColor);
			ballCountBlueLabel.setForeground(foregroundColor);
			JLabel ballCountBlueIconLabel = new JLabel(new ImageIcon("Pictures/BallBlue16.png"));
			ballCountNames.add(player4);
			ballCountIcons.add(ballCountBlueIconLabel);
			ballCountValues.add(ballCountBlueLabel);
		}

		ballCountPanel.add(ballCountNames, ballCountNamesC);
		ballCountPanel.add(ballCountIcons, ballCountIconsC);
		ballCountPanel.add(ballCountValues, ballCountValuesC);

		optionsPanel.add(ballCountPanel, ballCountPanelC);
	}

	/**
	 * This method initializes and fills the array used for storing the buttons that together
	 * form the board.</br>
	 * It also sets some default properties of these buttons like background color, icon and
	 * size.
	 */
	private void createField() {
		fieldPanel = new JPanel(new GridLayout(Board.Y_MAX, Board.X_MAX));
		fullPanel.add(fieldPanel, BorderLayout.CENTER);
		
		if (game.hasHuman()) {
			fieldListener = new FieldListener(this);
		}
		field = new FieldButton[Board.Y_MAX][Board.X_MAX];
		for (int y = 0; y < Board.Y_MAX; y++) {
			for (int x = 0; x <Board.X_MAX; x++) {
				field[y][x] = new FieldButton();
				field[y][x].setBackgroundColor(backgroundColor);
				field[y][x].setVisible(true);
				field[y][x].addActionListener(fieldListener);
				fieldPanel.add(field[y][x]);
			}
		}
	}
	
	protected void showHint() {
		Point hint = game.getHint();
		int hintX = (int) hint.getX();
		int hintY = (int) hint.getY();
		if (oldHint != null) {
			oldHint.setColor(Color.GRAY);
			oldHint.repaint();
		}
		field[hintY][hintX].setColor(Color.WHITE);
		field[hintY][hintX].repaint();
		oldHint = field[hintY][hintX];		
	}

	/**
	 * When this method is called it goes through the array field to see which button matches 
	 * the argument. If it finds one that matches the argument it then disables this button.
	 * @param pressedButton The button for which to find a match within the array field.
	 */
	public void fieldUsed(JButton pressedButton) {
		if (game.ClientHasturn() && hasHuman) {
			for(int y = 0; y < Board.Y_MAX; y++) {
				for (int x = 0; x < Board.X_MAX; x++) {
					if (field[y][x].equals(pressedButton)) {
						((OnlineHumanPlayer) game.getClient()).setChoice(new Point(x, y));
					}
				}
			}
		}
	}
	
	public void update(Board board) {
		oldHint = null;		
		for (int y = 0; y < Board.Y_MAX; y++) {
			for (int x = 0; x < Board.X_MAX; x++) {
				Ball ball = board.getField(x, y);
				if (!field[y][x].getColor().equals(ball.getColor())) {
					field[y][x].setColor(ball.getColor());
					field[y][x].repaint();
				}
			}
		}
		GamePlayer clientPlayer = game.getClient();
		if (game.ClientHasturn()) {
			stateField.setText("\n" +clientPlayer.getName() + "(" + clientPlayer.getBall() + ") has the turn.");
			if (hasHuman) {	
				activateBoard();
				switch (clientPlayer.getBall()) {
					case RED:
						setCursorIfChanged(redCursor);
						break;
					case BLUE:
						setCursorIfChanged(blueCursor);
						break;
					case YELLOW:
						setCursorIfChanged(yellowCursor);
						break;
					case GREEN:
						setCursorIfChanged(greenCursor);
						break;
					default:
						break;
				}
			}
		} else {
			stateField.setText("\nAnother client has the turn.");
			if (hasHuman) {
				deactivateBoard();
				setCursorIfChanged(idleCursor);
			}
		}
		ballCountRedLabel.setText("" + board.countInstancesOf(Ball.RED));
		ballCountGreenLabel.setText("" + board.countInstancesOf(Ball.GREEN));
		if (game.getNrOfPlayers() > 2) {
			ballCountYellowLabel.setText("" + board.countInstancesOf(Ball.YELLOW));
		} 
		if (game.getNrOfPlayers() > 3) {
			ballCountBlueLabel.setText("" + board.countInstancesOf(Ball.BLUE));
		}	
	}
		
	private void setCursorIfChanged(Cursor cursor) {
		if (!fieldPanel.getCursor().equals(cursor)) {
			fieldPanel.setCursor(cursor);
		}
	}
	
	public void gameOver(String message) {
		stateField.setText("\n" + message);
		deactivateBoard();
	}
	
	private void activateBoard() {
		if (hasHuman) {
			hintItem.setEnabled(true);
		}
	}
	
	private void deactivateBoard() {
		if (hasHuman) {
			hintItem.setEnabled(false);
		}	
	}
	
	public String toString() {
		String result = null;
		result = "A GUI for the Rolit Game." + "\n" + "GUI size: " + "\n\t" + "Width = " +
				getWidth() + " pixels." + "\n\t" + "Height = " + getHeight() + " pixels."
				+ "\n" + "Board size:" + "\n\t" + "Width = " + Board.X_MAX + " ballen," +
				"\n\t" + "Height = " + Board.Y_MAX + " ballen.";
		return result;
	}
	
	public void close() {
		game.goBack(false);
	}

	@Override
	public void addPopup(String title, String message, boolean warning) {
		if (isVisible()) {
			if (!warning) {
				JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(hintItem)) {
			showHint();
		} else if (e.getSource().equals(quitItem)) {
			close();
		} else if (e.getSource().equals(fullScreenItem)) {
			if (getWidth() >= screenSize.getWidth() || 
					getHeight() >= screenSize.getHeight()) {
				setSize(oldSize);
				setLocation(Tools.getCenterLocation(screenSize, getSize()));
			} else {
				oldSize = getSize();
				setExtendedState(JFrame.MAXIMIZED_BOTH);
			}
		} else if (e.getSource() instanceof FieldButton) {
			JButton pressedButton = (JButton) e.getSource();
			fieldUsed(pressedButton);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		messageTyped = false;
		String s = chat.getText() + e.getKeyChar();
		if (Tools.containsLetterOrNumber(s)) {
			messageTyped = true;
		} 		
	}
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (messageTyped) {
				addChatMessage(game.getClient().getName(), chat.getText());
				chat.setText("");
				game.chat(chat.getText());
			}
		} 
	}
	@Override
	public void keyReleased(KeyEvent e) {}

	public void addChatMessage(String playerName, String message) {
		chatBox.append(playerName + ": " + message + "\n");	
	}

}
