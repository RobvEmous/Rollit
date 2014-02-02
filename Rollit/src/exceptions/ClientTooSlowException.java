package exceptions;

/**
 * Signals that the client took too long to determine the move.
 * 
 * @author Rob van Emous
 * @version 1.0
 */
public class ClientTooSlowException extends Exception {
	private static final long serialVersionUID = 4036764230921915113L;

    public ClientTooSlowException() {
        super();
    }

    public ClientTooSlowException(String message) {
        super(message);
    }


    public ClientTooSlowException(String message, Throwable cause) {
        super(message, cause);
    }


    public ClientTooSlowException(Throwable cause) {
        super(cause);
    }

}