package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Observable;
import java.util.Observer;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
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
	private JPanel ballCountPanel;
	private JPanel fieldPanel;
	
	private JButton[][] field;
	private int buttonSize = 16;
	
	private int[] ballCounts = new int[] {0, 0, 0, 0};
	private JLabel ballCountRedLabel;
	private JLabel ballCountBlueLabel;
	private JLabel ballCountYellowLabel;
	private JLabel ballCountGreenLabel;
	
	
	private ImageIcon ballNoneIcon = new ImageIcon("Pictures/BallNoneBig.png");
	private ImageIcon ballHintIcon = new ImageIcon("Pictures/BallHintBig.png");
	private ImageIcon ballRedIcon = new ImageIcon("Pictures/BallRedBig.png");
	private ImageIcon ballBlueIcon = new ImageIcon("Pictures/BallBlueBig.png");
	private ImageIcon ballYellowIcon = new ImageIcon("Pictures/BallYellowBig.png");
	private ImageIcon ballGreenIcon = new ImageIcon("Pictures/BallGreenBig.png");
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
		
		ballCountPanel = new JPanel(new GridBagLayout());
		GridBagConstraints ballCountRedIconConstraints = new GridBagConstraints(0, 0, 
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
				new Insets(0,0,0,0), 0, 0);
		JLabel ballCountRedIconLabel = new JLabel(new ImageIcon("Pictures/BallRedIcon16.png"));
		GridBagConstraints ballCountRedConstraints = new GridBagConstraints(1, 0, 
				3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
				new Insets(0,0,0,0), 0, 0);
		ballCountRedLabel = new JLabel(String.valueOf(ballCounts[0]));
		GridBagConstraints ballCountBlueIconConstraints = new GridBagConstraints(0, 1, 
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
				new Insets(0,0,0,0), 0, 0);
		JLabel ballCountBlueIconLabel = new JLabel(new ImageIcon("Pictures/BallBlueIcon16.png"));
		GridBagConstraints ballCountBlueConstraints = new GridBagConstraints(1, 1, 
				3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
				new Insets(0,0,0,0), 0, 0);
		ballCountBlueLabel = new JLabel(String.valueOf(ballCounts[1]));
		GridBagConstraints ballCountYellowIconConstraints = new GridBagConstraints(0, 2, 
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
				new Insets(0,0,0,0), 0, 0);
		JLabel ballCountYellowIconLabel = new JLabel(new ImageIcon("Pictures/BallYellowIcon16.png"));
		GridBagConstraints ballCountYellowConstraints = new GridBagConstraints(1, 2, 
				3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
				new Insets(0,0,0,0), 0, 0);
		ballCountYellowLabel = new JLabel(String.valueOf(ballCounts[2]));
		GridBagConstraints ballCountGreenIconConstraints = new GridBagConstraints(0, 0, 
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
				new Insets(0,0,0,0), 0, 0);
		JLabel ballCountGreenIconLabel = new JLabel(new ImageIcon("Pictures/BallGreenIcon16.png"));
		GridBagConstraints ballCountGreenConstraints = new GridBagConstraints(1, 3, 
				3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
				new Insets(0,0,0,0), 0, 0);
		ballCountGreenLabel = new JLabel(String.valueOf(ballCounts[3]));
		ballCountPanel.add(ballCountRedIconLabel, ballCountRedIconConstraints);
		ballCountPanel.add(ballCountRedLabel, ballCountRedConstraints);
		ballCountPanel.add(ballCountBlueIconLabel, ballCountBlueIconConstraints);
		ballCountPanel.add(ballCountBlueLabel, ballCountBlueConstraints);
		ballCountPanel.add(ballCountYellowIconLabel, ballCountYellowIconConstraints);
		ballCountPanel.add(ballCountYellowLabel, ballCountYellowConstraints);
		ballCountPanel.add(ballCountGreenIconLabel, ballCountGreenIconConstraints);
		ballCountPanel.add(ballCountGreenLabel, ballCountGreenConstraints);
		
		optionsPanel.add(ballCountPanel);
		
		GridLayout fieldLayout = new GridLayout(Board.X_MAX, Board.Y_MAX);
		fieldPanel = new JPanel(fieldLayout);
		fieldPanel.setSize((Board.X_MAX * buttonSize), (Board.Y_MAX * buttonSize));
		
		createField();
		
		rescale();
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
		resizeIcons();		
		validate();
	}
	
	/**
	 * This method resizes the ball icons.</br>
	 * It does so by first making new icons of the original sized image and scaling them.
	 * It then checks for all buttons which icon it has and gives that button the new
	 * scaled version of that icon. At the end it also sets the old icons to the new 
	 * scaled ones so they get used from there on.
	 */
	private void resizeIcons() {
		//Creating the new scaled icons.
		ImageIcon newBallNoneIcon = new ImageIcon(
				(new ImageIcon("Pictures/BallNoneBig.png").getImage())
				.getScaledInstance(buttonSize, buttonSize, Image.SCALE_FAST));
		ImageIcon newBallHintIcon = new ImageIcon(
				(new ImageIcon("Pictures/BallHintBig.png").getImage())
				.getScaledInstance(buttonSize, buttonSize, Image.SCALE_FAST));
		ImageIcon newBallRedIcon = new ImageIcon(
				(new ImageIcon("Pictures/BallRedBig.png").getImage())
				.getScaledInstance(buttonSize, buttonSize, Image.SCALE_FAST));
		ImageIcon newBallBlueIcon = new ImageIcon(
				(new ImageIcon("Pictures/BallBlueBig.png").getImage())
				.getScaledInstance(buttonSize, buttonSize, Image.SCALE_FAST));
		ImageIcon newBallYellowIcon = new ImageIcon(
				(new ImageIcon("Pictures/BallYellowBig.png").getImage())
				.getScaledInstance(buttonSize, buttonSize, Image.SCALE_FAST));
		ImageIcon newBallGreenIcon = new ImageIcon(
				(new ImageIcon("Pictures/BallGreenBig.png").getImage())
				.getScaledInstance(buttonSize, buttonSize, Image.SCALE_FAST));

		//Changing the icons that are displayed by the buttons.
		for (int y = 0; y < Board.Y_MAX; y++) {
			for ( int x = 0; x < Board.X_MAX; x++) {
				ImageIcon currentIcon = (ImageIcon) field[y][x].getIcon();
				if (currentIcon.equals(ballNoneIcon)) {
					field[y][x].setIcon(newBallNoneIcon);
				} else if (currentIcon.equals(ballHintIcon)) {
					field[y][x].setIcon(newBallHintIcon);
				} else if (currentIcon.equals(ballRedIcon)) {
					field[y][x].setIcon(newBallRedIcon);
				} else if (currentIcon.equals(ballBlueIcon)) {
					field[y][x].setIcon(newBallBlueIcon);
				} else if (currentIcon.equals(ballYellowIcon)) {
					field[y][x].setIcon(newBallYellowIcon);
				} else if (currentIcon.equals(ballGreenIcon)) {
					field[y][x].setIcon(newBallGreenIcon);
				} else {
					System.out.println("Well, this doesn't quite work as well as thought");
				}
			}
		}
		
		//Overwriting the old icons with the new ones.
		ballNoneIcon = newBallNoneIcon;
		ballHintIcon = newBallHintIcon;
		ballRedIcon = newBallRedIcon;
		ballBlueIcon = newBallBlueIcon;
		ballYellowIcon = newBallYellowIcon;
		ballGreenIcon = newBallGreenIcon;
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
						human.choice = new Point(x,y);
				}
			}
		}
	}
	
	@Override
	public void update(Observable o, Object arg) {
		Ball[][] boardFields = (Ball[][]) arg;
		ballCounts[0] = 0;
		ballCounts[1] = 0;
		ballCounts[2] = 0;
		ballCounts[3] = 0;
		for (int y = 0; y < Board.Y_MAX; y++) {
			for (int x = 0; x < Board.X_MAX; x++) {
				Ball ball = boardFields[x][y];
				switch (ball) {
				case RED:
					field[y][x].setIcon(ballRedIcon);
					ballCounts[0]++;
					break;
				case BLUE:
					field[y][x].setIcon(ballBlueIcon);
					ballCounts[1]++;
					break;
				case YELLOW:
					field[y][x].setIcon(ballYellowIcon);
					ballCounts[2]++;
					break;
				case GREEN:
					field[y][x].setIcon(ballGreenIcon);
					ballCounts[3]++;
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
		ballCountRedLabel.setText(String.valueOf(ballCounts[0]));
		ballCountBlueLabel.setText(String.valueOf(ballCounts[1]));
		ballCountYellowLabel.setText(String.valueOf(ballCounts[2]));
		ballCountGreenLabel.setText(String.valueOf(ballCounts[3]));
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
