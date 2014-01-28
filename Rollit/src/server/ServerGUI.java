package server;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import clientAndServer.Tools;

/**
 * ServerGui. A GUI for the Server.
 * @author  Theo Ruys
 * @version 2005.02.21
 */
public class ServerGUI extends JFrame implements ActionListener, MessageUI {
	private static final long serialVersionUID = 8295677598601801613L;
	
	private int screenWidth = 600;
	private int screenHeight = 400;
	private Dimension windowSize = new Dimension(screenWidth, screenHeight);
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	private JButton bConnect;
	private JTextField tfPort;
	private JTextArea taMessages;
	private Server server;

	/** Constructs a ServerGUI object. */
	public ServerGUI() {
		super("ServerGUI");

		buildGUI();
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
			}
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	/** builds the GUI. */
	public void buildGUI() {
		setSize(screenWidth, screenHeight);
		setWindowLocation();
		
		setSize(windowSize);
		setLocation(Tools.getCenterLocation(screenSize, windowSize));
		
		// declare and create menu
		JMenuBar menuBar = new JMenuBar();
		JMenu optionMenu = new JMenu("Options");
		JMenuItem helpItem = new JMenuItem("Help");
		for (int i = 0; i < 10; i++) {
			helpItem.add(new JMenuItem("#" + i));
		}
		optionMenu.add(helpItem);
		menuBar.add(optionMenu);
		setJMenuBar(menuBar);

		
		// Panel p1 - Listen

		JPanel p1 = new JPanel(new FlowLayout());
		JPanel pp = new JPanel(new GridLayout(2, 2));

		JLabel lbAddress = new JLabel("Address: ");
		JTextField tfAddress = new JTextField(getHostAddress(), 12);
		tfAddress.setEditable(false);

		JLabel lbPort = new JLabel("Port:");
		tfPort = new JTextField("8080", 5);

		pp.add(lbAddress);
		pp.add(tfAddress);
		pp.add(lbPort);
		pp.add(tfPort);

		bConnect = new JButton("Start Listening");
		bConnect.addActionListener(this);

		p1.add(pp, BorderLayout.WEST);
		p1.add(bConnect, BorderLayout.EAST);

		// Panel p2 - Messages

		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());

		JLabel lbMessages = new JLabel("Messages:");
		taMessages = new JTextArea("", 15, 50);
		taMessages.setEditable(false);
		p2.add(lbMessages);
		p2.add(taMessages, BorderLayout.SOUTH);

		Container cc = getContentPane();
		cc.setLayout(new FlowLayout());
		cc.add(p1);
		cc.add(p2);
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
	 * listener for the "Start Listening" button
	 */
	public void actionPerformed(ActionEvent ev) {
		Object src = ev.getSource();
		if (src == bConnect) {
			startListening();
		}
	}

	/**
	 * Construct a Server-object, which is waiting for clients. The port field and button should be disabled
	 */
	private void startListening() {
		int port = 0;
		try {
			port = Integer.parseInt(tfPort.getText());
			if (port < 0 || port > 65535) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			addPopupMessage("Port invalid", "Port: " 
					+ port + " is not a valid portnumber!", true);
			return;
		}
		tfPort.setText(port + "");
		tfPort.setEditable(false);
		bConnect.setEnabled(false);

		server = new Server(port, this);
		server.start();

		addPopupMessage("Server started", "Started listening on port: " 
				+ port + ".", false);
	}

	/** add a message to the textarea  */
	public void addMessage(String msg) {
		taMessages.append(msg + "\n");
	}
	
	/** pops up a message to the user  */
	public void addPopupMessage(String title, String msg, boolean warning) {
		if (!warning) {
			JOptionPane.showMessageDialog(this, msg, title, JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, msg, title, JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void setWindowLocation() {
		setLocation(
		(int)(screenSize.getWidth() / 2 - windowSize.width / 2),
		(int)(screenSize.getHeight() / 2 - windowSize.height / 2)
		);
	}

}
