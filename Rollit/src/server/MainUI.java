package server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import clientAndServer.GlobalSettings;
import clientAndServer.Tools;

/**
 * ServerGui. A GUI for the Server.
 * @author  R&R
 * @version v0.1
 */
public class MainUI extends JFrame implements ActionListener, KeyListener {
	private static final long serialVersionUID = 5756603861153289750L;
	
	private static final Insets PADDING = new Insets(5, 5, 5, 5);
	
	private Dimension windowSize = new Dimension(440, 510);
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	private Main main;
	
	private String portToolTip = "Fill in a port to connect to";

	private JButton bConnect;
	private JButton bDisconnect;
	private JTextField address;
	private JTextField port;
	
	private boolean portTyped = false;
	
	private JTextArea taMainMessages;
	private JTextArea taClientManagerMessages;
	private JTextArea taGameManagerMessages;

	/** Constructs a MainUI object. */
	public MainUI(Main main) {
		super("Rolit server");

		buildGUI();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((MainUI) e.getWindow()).main.stop();
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
		JMenu optionMenu = new JMenu("Options");
		JMenuItem helpItem = new JMenuItem("Help");
		helpItem.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				addPopupMessage("Help", 
						"Fill in the port to listen for clients.\n" +
						"While the server is running, clients can connect" +
						", play games and ask for high-scores.\n" +
						"The three message-fields contain the messages " +
						"from those parts of the server side.", false
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
		JPanel panelButtons = new JPanel(new GridLayout(2, 0));
		JPanel panelMessages = new JPanel(new GridLayout(3, 0));
		
		GridBagConstraints panelLabelsC = new GridBagConstraints(
				0, 0, 1, 2, 1D, 1D, GridBagConstraints.CENTER, 0, PADDING, 0, 0);
		GridBagConstraints panelFieldsC = new GridBagConstraints(
				1, 0, 2, 2, 1D, 1D, GridBagConstraints.CENTER, 0, PADDING, 0, 0);
		GridBagConstraints panelButtonsC = new GridBagConstraints(
				3, 0, 1, 2, 1D, 1D, GridBagConstraints.CENTER, 0, PADDING, 0, 0);	
		GridBagConstraints panelMessagesC = new GridBagConstraints(
				0, 3, 4, 9, 1D, 1D, GridBagConstraints.CENTER, 0, PADDING, 0, 0);

		// create ip adress panel
		JLabel lbAdress = new JLabel("Address: ");
		address = new JTextField(getHostAddress(), 20);
		address.addKeyListener(this);
		
		panelLabels.add(lbAdress);
		panelFields.add(address);	
		
		// create port panel
		JLabel lbPort = new JLabel("Port: ");
		port = new JTextField(GlobalSettings.PORT_NR + "", 20);
		port.setToolTipText(portToolTip);
		port.addKeyListener(this);
		
		panelLabels.add(lbPort);
		panelFields.add(port);	
			
		panels.add(panelLabels, panelLabelsC);
		panels.add(panelFields, panelFieldsC);
		
		// create button panel
		bConnect = new JButton("Start server");
		bConnect.addActionListener(this);
		bConnect.setEnabled(true);
		
		bDisconnect = new JButton("Stop server");
		bDisconnect.addActionListener(this);
		bDisconnect.setEnabled(false);
		
		panelButtons.add(bConnect);
		panelButtons.add(bDisconnect);
		
		// Create message panel
		JPanel panelMessages1 = new JPanel(new BorderLayout());
		JPanel panelMessages2 = new JPanel(new BorderLayout());
		JPanel panelMessages3 = new JPanel(new BorderLayout());

		JLabel lbMessages1 = new JLabel("Messages of main:");
		JLabel lbMessages2 = new JLabel("Messages of client manager:");
		JLabel lbMessages3 = new JLabel("Messages of game manager:");

		taMainMessages = new JTextArea("", 6, 35);
		taMainMessages.setLineWrap(true);
		taMainMessages.setWrapStyleWord(true);
		taMainMessages.setEditable(false);
		taMainMessages.setAutoscrolls(true);
		JScrollPane chatBoxScrollPane1 = new JScrollPane(taMainMessages);

		panelMessages1.add(lbMessages1, BorderLayout.NORTH);
		panelMessages1.add(chatBoxScrollPane1, BorderLayout.CENTER);
		
		taClientManagerMessages = new JTextArea("", 6, 35);
		taClientManagerMessages.setLineWrap(true);
		taClientManagerMessages.setWrapStyleWord(true);
		taClientManagerMessages.setEditable(false);
		taClientManagerMessages.setAutoscrolls(true);
		JScrollPane chatBoxScrollPane2 = new JScrollPane(taClientManagerMessages);

		panelMessages2.add(lbMessages2, BorderLayout.NORTH);
		panelMessages2.add(chatBoxScrollPane2, BorderLayout.CENTER);
		
		taGameManagerMessages = new JTextArea("", 6, 35);
		taGameManagerMessages.setLineWrap(true);
		taGameManagerMessages.setWrapStyleWord(true);
		taGameManagerMessages.setEditable(false);
		taGameManagerMessages.setAutoscrolls(true);
		JScrollPane chatBoxScrollPane3 = new JScrollPane(taGameManagerMessages);

		panelMessages3.add(lbMessages3, BorderLayout.NORTH);
		panelMessages3.add(chatBoxScrollPane3, BorderLayout.CENTER);
		
		panelMessages.add(panelMessages1);
		panelMessages.add(panelMessages2);
		panelMessages.add(panelMessages3);
		
		panels.add(panelLabels, panelLabelsC);
		panels.add(panelFields, panelFieldsC);
		panels.add(panelButtons, panelButtonsC);
		panels.add(panelMessages, panelMessagesC);
		fullPanel.add(panels);
		add(fullPanel);
	}

	/** returns the Internetadress of this computer */
	private String getHostAddress() {
		try {
			InetAddress iaddr = InetAddress.getLocalHost();
			return iaddr.getHostAddress();
		} catch (UnknownHostException e) {
			return "?unknown?";
		}
	}

	/**
	 * listener for the buttons
	 */
	public void actionPerformed(ActionEvent ev) {
		Object src = ev.getSource();
		if (src == bConnect) {
			startListening();
		} else if (src.equals(bDisconnect)) {
			stopListening();
		}
	}

	/**
	 * Construct a Server-object, which is waiting for clients. The port field and button should be disabled
	 */
	private void startListening() {
		int portNr = 0;
		try {
			portNr = Integer.parseInt(port.getText());
			if (portNr < 0 || portNr > 65535) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			addPopupMessage("Port invalid", "Port: " 
					+ portNr + " is not a valid portnumber!", true);
			return;
		}
		port.setText(portNr + "");
		port.setEditable(false);
		bConnect.setEnabled(false);
		
		main.startListening(portNr);
		
		taMainMessages.setText("");
		taClientManagerMessages.setText("");
		taGameManagerMessages.setText("");
		
		addPopupMessage("Server started", "The server side of the Rolit " +
				" game has been started succesfully!\n Started listening on " +
				"port: " + portNr + ".", false);
		
		bDisconnect.setEnabled(true);	
	}
	
	private void stopListening() {
		bDisconnect.setEnabled(false);	
		main.stopListening();
		port.setEditable(true);
		
		addPopupMessage("Server stopped", "The server side of the Rolit " +
				" game has been stopped succesfully!", false);
		
		bConnect.setEnabled(true);
	}

	/** 
	 * add a message to the main-textarea.  
	 */
	public void addMainMessage(String msg) {
		taMainMessages.append(msg + "\n");
	}	

	/** 
	 * add a message to the clientManager-textarea.
	 */
	public void addClientManagerMessage(String msg) {
		taClientManagerMessages.append(msg + "\n");
	}
	
	/** 
	 * add a message to the gameManager-textarea.
	 */
	public void addGameManagerMessage(String msg) {
		taGameManagerMessages.append(msg + "\n");
	}
	
	/** pops up a message to the user  */
	public void addPopupMessage(String title, String msg, boolean warning) {
		if (!warning) {
			JOptionPane.showMessageDialog(this, msg, title, JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, msg, title, JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {	
		Object trigger = e.getSource();
		updateFieldBoolean(e, (JTextField) trigger);
		updateConnectButton();
	}
	
	private void updateFieldBoolean(KeyEvent e, JTextField item) {
		String s = item.getText() + e.getKeyChar();
		boolean validInput = Tools.containsLetterOrNumber(s);
		if (item.equals(address)) {
			portTyped = validInput;
		} 	
	}
	
	private void updateConnectButton() {
		if (portTyped && !bConnect.isEnabled()) {
			bConnect.setEnabled(true);
		} else if (!portTyped && bConnect.isEnabled()) {
			bConnect.setEnabled(false);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}
	
}
