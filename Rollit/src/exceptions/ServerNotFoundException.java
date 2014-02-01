package exceptions;

import java.io.IOException;

/**
 * Signals that the server on the specified address and port does not 
 * respond to an attempt to connect.
 * 
 * @author Rob van Emous
 *
 */
public class ServerNotFoundException extends IOException {
	private static final long serialVersionUID = 6603449370302934165L;


	public ServerNotFoundException() {
        super();
    }

    public ServerNotFoundException(String message) {
        super(message);
    }


    public ServerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }


    public ServerNotFoundException(Throwable cause) {
        super(cause);
    }

}
