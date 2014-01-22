package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * <h1>The GUI for the Rolit game.</h1>
 * @author René Nijhuis
 * @version 0.6
 */
public class GameUI extends JFrame implements Observer, PopupUI {
	private static final long serialVersionUID = 5844574958336659575L;
	
	private Dimension windowSize = new Dimension(800,600);
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	private HumanPlayer human;
	
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
	
	public GameUI(HumanPlayer h) {
		super("Rolit");
		initialize();
		human = h;
	}
	
	/**
	 * This method initializes the entire GUI.</br>
	 * It makes a call to the methods <code>initListeners</code> and <code> createField</code>.
	 * Furthermore it creates two panels and adds them to the frame that is this UI, one for
	 * the field and its buttons and one for the menu on the side .
	 */
	private void initialize() {
		initListeners();
		
		setSize(windowSize);
		setLocation(Tools.getCenterLocation(screenSize, windowSize));
		
		optionsPanel = new JPanel();
		optionsPanel.setSize((int) (getSize().getWidth() / 3), (Board.Y_MAX * buttonSize));
		
		GridLayout fieldLayout = new GridLayout(8, 8);
		fieldPanel = new JPanel(fieldLayout);
		fieldPanel.setSize((Board.X_MAX * buttonSize), (Board.Y_MAX * buttonSize));
		
		createField();
		
		add(fieldPanel);
		add(optionsPanel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addComponentListener(resizeListener);
		setVisible(true);
	}

	/**
	 * This method initializes and fills the array used for storing the buttons that together
	 * form the board.</br>
	 * It also sets some default properties of these buttons like background color, icon and
	 * size.
	 */
	private void createField() {
		field = new JButton[Board.Y_MAX][Board.X_MAX];
		for (int y = 0; y < Board.Y_MAX; y++) {
			for (int x = 0; x <Board.X_MAX; x++) {
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

	
	
	/**
	 * The method that makes sure the contents of the frame get resized when the frame is.
	 * When this method is called it gets the size of the frame and then sets the size of
	 * the panels and buttons to match the right proportions. After this is done it also 
	 * validates the frame to make sure the UI is updated to its new size.
	 */	
	protected void rescale() {
		Dimension dim = getSize();
		double width = ((dim.getWidth()) / 3) * 2;
		double height = dim.getHeight() - 36;
		if (width >= height) {
			buttonSize = (int) (height / Board.Y_MAX);
		} else {
			buttonSize = (int) (width / Board.X_MAX);
		}
		optionsPanel.setSize((int) (dim.getWidth() / 3), (int) (Board.Y_MAX * buttonSize));
		fieldPanel.setSize((int) (Board.X_MAX * buttonSize), (int) (Board.Y_MAX * buttonSize));
		for (int y = 0; y < Board.Y_MAX; y++) {
			for (int x = 0; x < Board.X_MAX; x++) {
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
		for(int y = 0; y < Board.Y_MAX; y++) {
			for (int x = 0; x < Board.X_MAX; x++) {
				if (field[y][x].equals(pressedButton)) {
					if (field[y][x].getIcon() == ballNoneIcon || 
							field[y][x].getIcon() == ballHintIcon) {
						human.choice = new Point(x,y);
					}
				}
			}
		}
	}
	
	@Override
	public void update(Observable o, Object arg) {
		Ball[][] boardFields = (Ball[][]) arg;
		for (int y = 0; y < Board.Y_MAX; y++) {
			for (int x = 0; x < Board.X_MAX; x++) {
				Ball ball = boardFields[x][y];
				switch (ball) {
				case RED:
					field[y][x].setIcon(ballRedIcon);
					break;
				case BLUE:
					field[y][x].setIcon(ballBlueIcon);
					break;
				case YELLOW:
					field[y][x].setIcon(ballYellowIcon);
					break;
				case GREEN:
					field[y][x].setIcon(ballGreenIcon);
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
