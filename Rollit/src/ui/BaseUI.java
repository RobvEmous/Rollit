package ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class BaseUI extends JFrame {
	private static final long serialVersionUID = 0L;
	private static final long subVersionId = 2L;

	private static final String FRAME_NAME = "Rollit game v";
	
	private static final int EDGE_PADDING = 5;
	private static final int PADDING = 5;
	
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private Dimension windowSize = new Dimension(800, 800);
	
	// init playing field
	
	
	public BaseUI() {
		setSimpleUI(FRAME_NAME + serialVersionUID + "." + subVersionId, true);
	}
	
	public void setUp() {
		setUpRows();
		//addItemsToLayout(true);
	}
	/**
	 * Creates window and initalises basic settings.
	 * @param title title of the window used in the top bar
	 * @param isVisible whether the window is made visible after the method or not
	 */
	private void setSimpleUI(String title, boolean isVisible) {
		setTitle(title);
		setWindowSize(windowSize);
		setSize(windowSize);
		setWindowLocation(SwingConstants.CENTER);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(true);
		// make window visible (or not)
		setVisible(isVisible);
	}	
	
	
	/**
	 * Initializes the elements used in every row or menu bar.	
	 */
	private void setUpRows() {
		// set up playing field
		JPanel playfield = new JPanel();
		JTextArea notifications = new JTextArea("Welcome to the prime calculator benchmark", 1, 30);
		JLabel benchmarkVersion = new JLabel ("Chose benchmark version:");
		JButton singleBenchmark = new JButton("Normal");
		// set up row 1 & 2 items 

		
		// set up row 3 & 4 items

		// set up menu items
	}
		
	/**
	 * Adds created items in desired row or menu bar.
	 * @param setVisible whether the window is made visible after the method or not
	 */
	private void addItemsToLayout(boolean setVisible) {
		//GridLayout majorLayout = new GridLayout(2, 1, itemGap, itemGap);
		//setLayout(majorLayout);
		
		// add row 1 & 2 items
		
		//add(row1);
		
		// add row 3 & 4 items

		
		// add menu items
		
		// make window visible (or not)
		setVisible(setVisible);
	}
	
	

	
	/**
	 * Can be used with an Gridbaglayout to easily set the constrains of an item.	
	 * @param constraints the GridBagConstraints object to apply the settings to
	 * @param theGridx the column the top-left part of the item is placed
	 * @param theGridy the row the top-left part of the item is placed
	 * @param theGridWidth number of columns this component uses
	 * @param theGridHeight number of rows this component uses
	 * @param theGridWeight gives the object additional space in all directions if <code> theGridWeight </code> > 1
	 */
	private void setGridBagConstraints(GridBagConstraints constraints,int theGridx, int theGridy, int theGridWidth, int theGridHeight, int theGridWeight) {
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(PADDING, PADDING, PADDING, PADDING);
		constraints.ipadx = EDGE_PADDING;
		constraints.ipady = EDGE_PADDING;
		constraints.gridx = theGridx;
		constraints.gridy = theGridy;
		constraints.weightx = theGridWeight;
		constraints.gridwidth = theGridWidth;
		constraints.gridheight = theGridHeight;
	}
		
	/**
	 * Creates listeners for all menuItems so the can respond to clicks.
	 */
	private void createMenuItemListeners() {
		/*singleCoreBench.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (singleCoreBench.isSelected()) {
					multiCoreBenchmark = false;
				}
			}
		});*/
	}
		
	/**
	 * Creates listeners for all Buttons so the can respond to clicks.
	 */
	private void createActionButtonListeners() {
		/*singleBenchmark.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//TODO start benchmark
				threads = new BenchmarkThreads();
				if (!multiCoreBenchmark || processorCores == 1) {
					threads.CreateWorkingThreads(1);
				} else {
					threads.CreateWorkingThreads(processorCores);
				}
				singleBenchmark.setEnabled(false);
				continuousBenchmark.setEnabled(false);
				stopBenchmark.setEnabled(true);
			}
		});*/
	}

	/**
	 * Sets the size of the window
	 * @param windowSize the dimensions of the window
	 */
	private void setWindowSize(Dimension windowSize) {
		setSize(windowSize);
	}

	/**
	 * Places the window in the secified position on the screen
	 */
	private void setWindowLocation(int location) {
		if (location == SwingConstants.CENTER) {
			setLocation(
					(int)screenSize.getWidth() / 2 - windowSize.width / 2,
					(int)screenSize.getHeight() / 2 - windowSize.height / 2
					);
		}
	}
	
	@SuppressWarnings("unused")
	private void setLookAndFeel() {
		try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception exc) {
			System.out.println("Cant set the look and feel to Nimbus!");
		}
	}
	
	public static void main(String[] args) {
		BaseUI ui = new BaseUI();
		ui.setUp();
	}
}

