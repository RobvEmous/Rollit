package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 * This Listener makes a call to a GUI's fieldUsed method as soon as it gets triggered.
 * @author René Nijhuis
 * @version 0.2
 */

public class FieldListener implements ActionListener {
	
	private GameUI targetGUI;
	
	/**
	 * The constructor of this listener assigns the specified GUI as the GUI on which
	 * this listener acts
	 * @param g The GUI to act on
	 */
	public FieldListener(GameUI g) {
		targetGUI = g;
	}
	
	/**
	 * This is the method that actually makes the call to the fieldUsed method of the GUI
	 * it gets the source of the event that triggered this method and then uses that to call
	 * the fieldUsed method
	 * @param e The event that triggers this method
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton pressedButton = (JButton) e.getSource();
		targetGUI.fieldUsed(pressedButton);
	}

}
