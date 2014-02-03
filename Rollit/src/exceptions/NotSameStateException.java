package exceptions;

/**
 * Signals that the server and client are not in the same state. This will 
 * result in a kick of the client from the server, because this means it 
 * is a bad(ly programmed) client. <b>This should never happen</b>.<br>
 * This exception will also be thrown when the client is trying to log in 
 * and another client has already logged in on this account. Thén, it is
 * not because of bad programming of the client.
 * 
 * @author Rob van Emous
 * @version 1.0
 */
public class NotSameStateException extends Exception {
	private static final long serialVersionUID = 6432101392173853327L;


	public NotSameStateException() {
        super();
    }

    public NotSameStateException(String message) {
        super(message);
    }


    public NotSameStateException(String message, Throwable cause) {
        super(message, cause);
    }


    public NotSameStateException(Throwable cause) {
        super(cause);
    }

}