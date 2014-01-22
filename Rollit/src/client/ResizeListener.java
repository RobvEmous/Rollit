package client;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * This Listener only acts when a GUI is resized.
 * When the GUI is resized it makes a call to the rescale method of that GUI.
 * @author René Nijhuis
 * @version 0.2
 */

public class ResizeListener implements ComponentListener {
	
	@Override
	public void componentHidden(ComponentEvent ce) {/*Do nothing*/}
	@Override
	public void componentMoved(ComponentEvent ce) {/*Do nothing*/}
	@Override
	public void componentShown(ComponentEvent ce) {/*Do nothing*/}
	
	/**
	 * This method gets the GUI which is the source of the given componentEvent 
	 * and then makes a call to that GUI's rescale method.
	 * @param ComponentEvent ce this is the event that triggers the action
	 */
	@Override
	public void componentResized(ComponentEvent ce) {
		GameUI targetGUI = (GameUI) ce.getSource();
		targetGUI.rescale();
	}
}
