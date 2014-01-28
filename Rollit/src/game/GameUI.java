package game;

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
import java.util.Observable;
import java.util.Observer;

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

import client.PopupUI;
import clientAndServer.Tools;

/**
 * <h1>The GUI for the Rolit game.</h1>
 * @author René Nijhuis
 * @version 0.6
 */
public class GameUI extends JFrame implements Observer, PopupUI {
	private static final long serialVersionUID = 5844574958336659575L;
	
	private Dimension windowSize = new Dimension(800,600);
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private Dimension oldSize = windowSize;
	
	private OfflineGame game;
	private boolean onlineGame;

	private FieldListener fieldListener;
	
	private static final Insets inset = new Insets(0,0,0,0);

	private JPanel fullPanel;
	private JPanel optionsPanel;
	private JPanel ballCountPanel;
	private JPanel fieldPanel;
	
	private FieldButton[][] field;
	
	private JTextField stateField;
	private JTextArea chatBox;
	private JTextField chat;
	private int[] ballCounts = new int[] {0, 0, 0, 0};
	private JLabel ballCountRedLabel;
	private JLabel ballCountBlueLabel;
	private JLabel ballCountYellowLabel;
	private JLabel ballCountGreenLabel;
	
	private FieldButton oldHint = null;
	private boolean isFirstScreenUpdate = true;

	private Color backgroundColor = Color.BLACK;
	private Color foregroundColor = Color.WHITE;
	private Color textBoxColor = Color.GRAY;
	
	private Cursor redCursor;
	private Cursor blueCursor;
	private Cursor yellowCursor;
	private Cursor greenCursor;
	private Cursor idleCursor;
	
	public GameUI() {
		this(false,null);
	}
	
	public GameUI(boolean online) {
		this(online,null);
	}
	
	public GameUI(OfflineGame g) {
		this(false, g);
	}
	
	public GameUI(boolean online, OfflineGame g){
		super("Rolit");
		onlineGame = online;
		game = g;
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
		
		fullPanel = new JPanel(new GridBagLayout());
		fullPanel.setSize((int) (getWidth() - 50), (int) (getHeight() -50));
		fullPanel.setBackground(backgroundColor);
		
		add(fullPanel);
		
		createMenu();
		createOptionsPanel();
		createField();
		createCursors();
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		
	}

	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");
		JMenuItem hintItem = new JMenuItem("Hint");
		hintItem.setToolTipText("A random field you can use will be highlighted");
		hintItem.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						showHint();
					}					
				});
		gameMenu.add(hintItem);
		JMenuItem quitItem = new JMenuItem("Quit Game");
		quitItem.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						game.goBack();
					}					
				});
		gameMenu.add(quitItem);
		menuBar.add(gameMenu);
		JMenu optionsMenu = new JMenu("Options");
		JMenuItem fullScreenItem = new JMenuItem("Full Screen");
		fullScreenItem.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (getWidth() >= screenSize.getWidth() || 
								getHeight() >= screenSize.getHeight()) {
							setSize(oldSize);
							setLocation(Tools.getCenterLocation(screenSize, getSize()));
						} else {
							oldSize = getSize();
							setBounds(-10, -30, (int) screenSize.getWidth() + 20,
									(int) screenSize.getHeight() + 50);
		
						}
					}
					
				});
		optionsMenu.add(fullScreenItem);
		menuBar.add(optionsMenu);
		setJMenuBar(menuBar);
	}

	private void createCursors() {
		Toolkit tool = Toolkit.getDefaultToolkit();
		Image redCursorImage = tool.getImage("Pictures/BallRed32.png");
		Image blueCursorImage = tool.getImage("Pictures/BallBlue32.png");
		Image yellowCursorImage = tool.getImage("Pictures/BallYellow32.png");
		Image greenCursorImage = tool.getImage("Pictures/BallGreen32.png");
		Image idleCursorImage = tool.getImage("Pictures/BallNone32.png");
		redCursor = tool.createCustomCursor(redCursorImage, new Point(16,16), "ballCursor");
		blueCursor = tool.createCustomCursor(blueCursorImage, new Point(16,16), "ballCursor");
		yellowCursor = tool.createCustomCursor(yellowCursorImage, new Point(16,16), "ballCursor");
		greenCursor = tool.createCustomCursor(greenCursorImage, new Point(16,16), "ballCursor");
		idleCursor = tool.createCustomCursor(idleCursorImage, new Point(16,16), "ballCursor");
		
	}

	private void createOptionsPanel() {
		optionsPanel = new JPanel(new GridBagLayout());
		optionsPanel.setBackground(backgroundColor);
		GridBagConstraints optionsPanelC = new GridBagConstraints(4, 0, 1, 1, 1D, 1D,
				GridBagConstraints.LINE_END, GridBagConstraints.BOTH, inset, 0, 0);
		
		fullPanel.add(optionsPanel, optionsPanelC);		
		
		stateField = new JTextField();
		GridBagConstraints stateFieldC = new GridBagConstraints(0, 0, 1, 1, 1D, 1D,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, inset, 0, 0);
		stateField.setEditable(false);
		stateField.setBackground(backgroundColor);
		stateField.setForeground(foregroundColor);;
		stateField.setPreferredSize(new Dimension(optionsPanel.getWidth(), 50));
		stateField.setHorizontalAlignment(JTextField.CENTER);
		stateField.setToolTipText("This displays who has the turn or who has won the game");
		optionsPanel.add(stateField, stateFieldC);
		
		JSeparator seperator1 = new JSeparator(JSeparator.HORIZONTAL);
		GridBagConstraints sep1C = new GridBagConstraints(0, 1, 1, 1, 1D, 1D, 
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, inset, 0, 0);
		seperator1.setPreferredSize(new Dimension(getPreferredSize().width, 2));
		optionsPanel.add(seperator1, sep1C);
		
		createBallCountPanel();
		
		if (onlineGame) {
			JSeparator seperator2 = new JSeparator(JSeparator.HORIZONTAL);
			GridBagConstraints sep2C = new GridBagConstraints(0, 6, 1, 1, 1D, 1D, 
					GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, inset, 0, 0);
			seperator1.setPreferredSize(new Dimension(getPreferredSize().width, 2));
			optionsPanel.add(seperator2, sep2C);
			
			JSeparator seperator3 = new JSeparator(JSeparator.HORIZONTAL);
			GridBagConstraints sep3C = new GridBagConstraints(0, 7, 1, 1, 1D, 1D, 
					GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, inset, 0, 0);
			seperator1.setPreferredSize(new Dimension(getPreferredSize().width, 2));
			optionsPanel.add(seperator3, sep3C);
			
			createChatPanel();
		}
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
		chat.addKeyListener(new KeyListener() {
			boolean messageTyped;
			@Override
			public void keyTyped(KeyEvent e) {
				messageTyped = false;
				char c = e.getKeyChar();
				if (Tools.isLetterOrNumber(c)) {
					messageTyped = true;
				} else {
					String s = chat.getText();
					if (Tools.containsLetterOrNumber(s)) {
						messageTyped = true;
					} 	
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER && messageTyped) {
					chatBox.append("Player1: " + chat.getText() + "\n");
					chat.setText("");
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}			
		});
		GridBagConstraints chatConstraints = new GridBagConstraints(0, 4, 1, 1, 1D, 1D,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, inset, 0, 0);
		
		chatPanel.add(chatBoxLabel, chatBoxLabelC);
		chatPanel.add(chatBoxScrollPane, chatBoxScrollPaneC);
		chatPanel.add(chat, chatConstraints);
		
	}

	private void createBallCountPanel() {
		ballCountPanel = new JPanel(new GridBagLayout());
		ballCountPanel.setBackground(backgroundColor);
		GridBagConstraints ballCountPanelTextC = new GridBagConstraints(0, 0, 4, 1, 1D, 1D,
				GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, inset, 0, 0);
		JLabel ballCountPanelText = new JLabel("Ball Count");
		ballCountPanelText.setForeground(foregroundColor);
		ballCountPanelText.setPreferredSize(new Dimension(optionsPanel.getWidth(), 50));
		ballCountPanelText.setHorizontalAlignment(JLabel.CENTER);
		ballCountPanelText.setToolTipText("The amount of balls each player has on the field.");
		GridBagConstraints ballCountRedIconC = new GridBagConstraints(0, 1, 
				1, 1, 1D, 1D, GridBagConstraints.PAGE_START, GridBagConstraints.NONE, 
				inset, 0, 0);
		JLabel ballCountRedIconLabel = new JLabel(new ImageIcon("Pictures/BallRed16.png"));
		GridBagConstraints ballCountRedC = new GridBagConstraints(1, 1, 
				3, 1, 1D, 1D, GridBagConstraints.PAGE_START, GridBagConstraints.BOTH, 
				inset, 0, 0);
		ballCountRedLabel = new JLabel(String.valueOf(ballCounts[0]));
		ballCountRedLabel.setForeground(foregroundColor);
		GridBagConstraints ballCountBlueIconC = new GridBagConstraints(0, 2, 
				1, 1, 1D, 1D, GridBagConstraints.PAGE_START, GridBagConstraints.NONE, 
				inset, 0, 0);
		JLabel ballCountBlueIconLabel = new JLabel(new ImageIcon("Pictures/BallBlue16.png"));
		GridBagConstraints ballCountBlueC = new GridBagConstraints(1, 2, 
				3, 1, 1D, 1D, GridBagConstraints.PAGE_START, GridBagConstraints.BOTH, 
				inset, 0, 0);
		ballCountBlueLabel = new JLabel(String.valueOf(ballCounts[1]));
		ballCountBlueLabel.setForeground(foregroundColor);
		GridBagConstraints ballCountYellowIconC = new GridBagConstraints(0, 3, 
				1, 1, 1D, 1D, GridBagConstraints.PAGE_START, GridBagConstraints.NONE, 
				inset, 0, 0);
		JLabel ballCountYellowIconLabel = new JLabel(new ImageIcon("Pictures/BallYellow16.png"));
		GridBagConstraints ballCountYellowC = new GridBagConstraints(1, 3, 
				3, 1, 1D, 1D, GridBagConstraints.PAGE_START, GridBagConstraints.BOTH, 
				inset, 0, 0);
		ballCountYellowLabel = new JLabel(String.valueOf(ballCounts[2]));
		ballCountYellowLabel.setForeground(foregroundColor);
		GridBagConstraints ballCountGreenIconC = new GridBagConstraints(0, 4, 
				1, 1, 1D, 1D, GridBagConstraints.PAGE_START, GridBagConstraints.NONE, 
				inset, 0, 0);
		JLabel ballCountGreenIconLabel = new JLabel(new ImageIcon("Pictures/BallGreen16.png"));
		GridBagConstraints ballCountGreenC = new GridBagConstraints(1, 4, 
				3, 1, 1D, 1D, GridBagConstraints.PAGE_START, GridBagConstraints.BOTH, 
				inset, 0, 0);
		ballCountGreenLabel = new JLabel(String.valueOf(ballCounts[3]));
		ballCountGreenLabel.setForeground(foregroundColor);
		ballCountPanel.add(ballCountPanelText, ballCountPanelTextC);
		ballCountPanel.add(ballCountRedIconLabel, ballCountRedIconC);
		ballCountPanel.add(ballCountRedLabel, ballCountRedC);
		ballCountPanel.add(ballCountBlueIconLabel, ballCountBlueIconC);
		ballCountPanel.add(ballCountBlueLabel, ballCountBlueC);
		ballCountPanel.add(ballCountYellowIconLabel, ballCountYellowIconC);
		ballCountPanel.add(ballCountYellowLabel, ballCountYellowC);
		ballCountPanel.add(ballCountGreenIconLabel, ballCountGreenIconC);
		ballCountPanel.add(ballCountGreenLabel, ballCountGreenC);
		ballCountPanel.setSize(optionsPanel.getWidth(), 300);
		GridBagConstraints ballCountPanelC = new GridBagConstraints(0, 2, 1, 4, 1D, 1D,
				GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, inset, 0, 0);
		optionsPanel.add(ballCountPanel, ballCountPanelC);
	}

	/**
	 * This method initializes and fills the array used for storing the buttons that together
	 * form the board.</br>
	 * It also sets some default properties of these buttons like background color, icon and
	 * size.
	 */
	private void createField() {
		GridLayout fieldLayout = new GridLayout(Board.X_MAX, Board.Y_MAX);
		fieldPanel = new JPanel(fieldLayout);
		GridBagConstraints fieldPanelC = new GridBagConstraints(0, 0, 4, 1, 1D, 1D,
				GridBagConstraints.LINE_START, GridBagConstraints.BOTH, inset, 0, 0);

		fullPanel.add(fieldPanel, fieldPanelC);
		
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
	 * When this method is called is goes through the array field to see which button matches 
	 * the argument. If it finds one that matches the argument it then disables this button.
	 * @param pressedButton The button for which to find a match within the array field.
	 */
	public void fieldUsed(JButton pressedButton) {
		GamePlayer currentPlayer = game.getCurrentPlayer();
		for(int y = 0; y < Board.Y_MAX; y++) {
			for (int x = 0; x < Board.X_MAX; x++) {
				if (field[y][x].equals(pressedButton) && 
						currentPlayer instanceof HumanPlayer) {
						((HumanPlayer) currentPlayer).choice = new Point(x,y);
				}
			}
		}
	}
	
	@Override
	public void update(Observable o, Object arg) {
		oldHint = null;
		Board board = ((Board) arg).deepCopy();
		OfflineGame g = (OfflineGame) o;
		for (int y = 0; y < Board.Y_MAX; y++) {
			for (int x = 0; x < Board.X_MAX; x++) {
				Ball ball = board.getField(x, y);
				field[y][x].setColor(ball.getColor());
				field[y][x].repaint();
			}
		}
		if (board.gameOver()) {
			if (board.hasWinner()) {
				stateField.setText(g.getWinner().getName() + " has won the game!");
			} else {
				stateField.setText("It's a draw.");
			}
		} else {
			if (isFirstScreenUpdate) {
				stateField.setText("It is " + g.getCurrentPlayer().getName() + "'s turn");
			} else {
				stateField.setText("It is " + g.getNextPlayer().getName()+ "'s turn.");
			}
		}
		GamePlayer player = null;
		if (isFirstScreenUpdate) {
			player = g.getCurrentPlayer();
		} else {
			player = g.getNextPlayer();
		}
		if (player instanceof HumanPlayer) {
			switch (player.getBall()) {
				case RED:
					fieldPanel.setCursor(redCursor);
					break;
				case BLUE:
					fieldPanel.setCursor(blueCursor);
					break;
				case YELLOW:
					fieldPanel.setCursor(yellowCursor);
					break;
				case GREEN:
					fieldPanel.setCursor(greenCursor);
					break;
				default:
					break;
			}
		} else {
			fieldPanel.setCursor(idleCursor);
		}
		ballCountRedLabel.setText("" + board.countInstancesOf(Ball.RED));
		ballCountBlueLabel.setText("" + board.countInstancesOf(Ball.BLUE));
		ballCountYellowLabel.setText("" + board.countInstancesOf(Ball.YELLOW));
		ballCountGreenLabel.setText("" + board.countInstancesOf(Ball.GREEN));
		validate();
		isFirstScreenUpdate = false;
	}	
	
	public String toString() {
		String result = null;
		result = "A GUI for the Rolit Game." + "\n" + "GUI size: " + "\n\t" + "Width = " +
				getWidth() + " pixels." + "\n\t" + "Height = " + getHeight() + " pixels."
				+ "\n" + "Board size:" + "\n\t" + "Width = " + Board.X_MAX + " ballen," +
				"\n\t" + "Height = " + Board.Y_MAX + " ballen.";
		return result;
	}

	@Override
	public void addPopup(String title, String message, boolean warning) {
		if (!warning) {
			JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public static void newPopup(String title, String message, boolean warning) {
		if (!warning) {
			JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
}
