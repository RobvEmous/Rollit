package client;

public interface PopupUI {
	
	/**
	 * Pops up a message to the user
	 * 
	 * @param title the title of the messagebox
	 * @param message the message to display
	 * @param warning if true this message is a warning else a notification
	 */
	public void addPopup(String title, String message, boolean warning);
}
