package exceptions;

import java.io.IOException;

/**
 * Signals that communication with this client is not possible anymore. 
 * The client most likely rage-quited ;).
 * 
 * @author Rob van Emous
 * @version 1.0
 */
public class ClientNotFoundException extends IOException {
	private static final long serialVersionUID = 4036764230921915113L;

    public ClientNotFoundException() {
        super();
    }

    public ClientNotFoundException(String message) {
        super(message);
    }


    public ClientNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }


    public ClientNotFoundException(Throwable cause) {
        super(cause);
    }

}