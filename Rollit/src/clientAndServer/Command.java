package clientAndServer;

/**
 * A command object.<br> This will be used to send commands between Client
 * and Server.
 * 
 * @see Commands
 * @author Rob van Emous
 * @version 1.0
 */
public class Command {
	
	private String id;
	private String[] args;

	/**
	 * Create a new Command.
	 * @param id the actual command
	 * @param args possible arguments
	 */
	public Command(String id, String[] args) {
		this.id = id;
		this.args = args;
	}
	
	/**
	 * Returns the actual command of this Command.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Sets the actual command of this Command.
	 * @param id the actual command
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Returns the arguments.
	 */
	public String[] getArgs() {
		return args;
	}	
	
	/**
	 * Returns a String representation of this Command.<br>
	 * "Id: " + getId() + ", args: " + Tools.ArrayToString(getArgs());
	 * @see Tools
	 * @return A String representation of this Command.<br>
	 */
	@Override
	public String toString() {
		return "Id: " + id + ", args: " + Tools.ArrayToString(getArgs());
	}

}
