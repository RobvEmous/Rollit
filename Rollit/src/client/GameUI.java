package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * The GUI for the Rolit game.
 * @author René Nijhuis
 * @version 0.2
 */
public class GameUI extends JFrame implements Observer {
	private static final long serialVersionUID = 5844574958336659575L;

	/**
	 * The amount of buttons that the board is wide.
	 */
	public static final int FIELD_WIDTH = Board.X_MAX;
	/**
	 * The amount of buttons that the board is high.
	 */
	public static final int FIELD_HEIGHT = Board.Y_MAX;
	
	private ResizeListener resizeListener;
	private FieldListener fieldListener;
	
	private JPanel optionsPanel;	
	private JPanel fieldPanel;
	
	private JButton[][] field;
	private int buttonSize = 16;
	
	private ImageIcon ballNoneIcon = new ImageIcon("Pictures/BallNone64.png");
	private ImageIcon ballHintIcon = new ImageIcon("Pictures/BallHint64.png");
	private ImageIcon ballRedIcon = new ImageIcon("Pictures/BallRed64.png");
	private ImageIcon ballBlueIcon = new ImageIcon("Pictures/BallBlue64.png");
	private ImageIcon ballYellowIcon = new ImageIcon("Pictures/BallYellow64.png");
	private ImageIcon ballGreenIcon = new ImageIcon("Pictures/BallGreen64.png");
	private Color backgroundColor = Color.BLACK;
	
	public GameUI() {
		super("Rolit");
		initialize();
	}
	
	/**
	 * This method initializes the entire GUI it creates all panels and buttons and than adds
	 * them to the frame that is this GUI
	 */
	private void initialize() {
		initListeners();
		
		optionsPanel = new JPanel();
		optionsPanel.setSize((int) (getSize().getWidth() / 3), (FIELD_HEIGHT * buttonSize));
		
		GridLayout fieldLayout = new GridLayout(8, 8);
		fieldPanel = new JPanel(fieldLayout);
		fieldPanel.setSize((FIELD_WIDTH * buttonSize), (FIELD_HEIGHT * buttonSize));
		
		createField();
		
		add(fieldPanel);
		add(optionsPanel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addComponentListener(resizeListener);
		setVisible(true);
	}

	/**
	 * This method initializes and fills the array used for storing the buttons that together
	 * form the board.
	 * It also sets these buttons to some default properties.
	 */
	private void createField() {
		field = new JButton[FIELD_HEIGHT][FIELD_WIDTH];
		for (int y = 0; y < FIELD_HEIGHT; y++) {
			for (int x = 0; x <FIELD_WIDTH; x++) {
				field[y][x] = new JButton();
				field[y][x].setBackground(backgroundColor);
				field[y][x].setVisible(true);
				field[y][x].setIcon(ballNoneIcon);
				//field[y][x].setText(y + ", " + x);
				field[y][x].setSize(buttonSize, buttonSize);
				field[y][x].addActionListener(fieldListener);
				fieldPanel.add(field[y][x]);
			}
		}
	}

	/**
	 * This method initializes all listeners this GUI uses. Nothing more, nothing less.
	 */
	private void initListeners() {
		resizeListener = new ResizeListener();
		fieldListener = new FieldListener(this);
	}	

	@Override
	public void update(Observable o, Object arg) {
		Ball[][] boardFields = (Ball[][]) arg;
		for (int y = 0; y < Board.Y_MAX; y++) {
			for (int x = 0; x < Board.X_MAX; x++) {
				Ball ball = boardFields[x][y];
				switch (ball) {
				case RED:
					field[y][x].setDisabledIcon(ballRedIcon);
					field[y][x].setEnabled(false);
					break;
				case BLUE:
					field[y][x].setDisabledIcon(ballBlueIcon);
					field[y][x].setEnabled(false);
					break;
				case YELLOW:
					field[y][x].setDisabledIcon(ballYellowIcon);
					field[y][x].setEnabled(false);
					break;
				case GREEN:
					field[y][x].setDisabledIcon(ballGreenIcon);
					field[y][x].setEnabled(false);
					break;
				case HINT:
					field[y][x].setIcon(ballHintIcon);
				case EMPTY:
					field[y][x].setIcon(ballNoneIcon);
				default:
					break;
				}
				
			}
		}
	}
	
	/**
	 * When this method is called it gets the size of the frame and then sets the size of
	 * the panels and buttons to match the right proportions. After this is done it also 
	 * validates the frame to make sure the GUI is updated to its new size.
	 */	
	protected void rescale() {
		Dimension dim = getSize();
		double width = ((dim.getWidth()) / 3) * 2;
		double height = dim.getHeight() - 36;
		if (width >= height) {
			buttonSize = (int) (height / FIELD_HEIGHT);
		} else {
			buttonSize = (int) (width / FIELD_WIDTH);
		}
		optionsPanel.setSize((int) (dim.getWidth() / 3), (int) (FIELD_HEIGHT * buttonSize));
		fieldPanel.setSize((int) (FIELD_WIDTH * buttonSize), (int) (FIELD_HEIGHT * buttonSize));
		for (int y = 0; y < FIELD_HEIGHT; y++) {
			for (int x = 0; x < FIELD_WIDTH; x++) {
				field[y][x].setSize(buttonSize, buttonSize);
			}
		}
		validate();
	}
	
	/**
	 * When this method is called is goes through the array field to see which button matches 
	 * the argument. If it finds one that matches the argument it then disables this button.
	 * @param pressedButton The button for which to find a match within the array field.
	 */
	public void fieldUsed(JButton pressedButton) {
		for(int y = 0; y < FIELD_HEIGHT; y++) {
			for (int x = 0; x < FIELD_WIDTH; x++) {
				if (field[y][x].equals(pressedButton)) {
					field[y][x].setEnabled(false);
				}
			}
		}
	}
	
	public String toString() {
		String result = null;
		result = "A GUI for the Rolit Game." + "\n" + "GUI size: " + "\n\t" + "Width = " +
				getWidth() + " pixels." + "\n\t" + "Height = " + getHeight() + " pixels."
				+ "\n" + "Board size:" + "\n\t" + "Width = " + FIELD_WIDTH + " ballen," +
				"\n\t" + "Height = " + FIELD_HEIGHT + " ballen.";
		return result;
	}
	
	
	public static void main(String[] args) {
		new GameUI();
	}
	
}
