package exceptions;

import java.net.BindException;

/**
 * Signals that the port the server wants to listen to is already in use
 * probably another instance of the server is also running and has already
 * this port
 * 
 * @author Rob van Emous
 * @version 1.0
 */
public class PortInUseException extends BindException {
	private static final long serialVersionUID = 5087010459210844576L;


	public PortInUseException() {
        super();
    }

    public PortInUseException(String message) {
        super(message);
    }


    public PortInUseException(String message, Throwable cause) {
        super(message);
    }


    public PortInUseException(Throwable cause) {
        super();
    }

}