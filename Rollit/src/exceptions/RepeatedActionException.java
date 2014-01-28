package exceptions;

import java.io.IOException;

/**
 * Signals that the server received a certain command multiple times from one client. 
 * This indicates a bad/buggy client.
 * 
 * @author Rob van Emous
 *
 */
public class RepeatedActionException extends Exception {
	private static final long serialVersionUID = 6432101392173853327L;


	public RepeatedActionException() {
        super();
    }

    public RepeatedActionException(String message) {
        super(message);
    }


    public RepeatedActionException(String message, Throwable cause) {
        super(message, cause);
    }


    public RepeatedActionException(Throwable cause) {
        super(cause);
    }

}