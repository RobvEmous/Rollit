package exceptions;

/**
 * Signals that the server and client are not in the same state. This will 
 * result in a kick of the client from the server, because this means it 
 * is a bad(ly programmed) client. <b>This should never happen</b>.
 * 
 * @author Rob van Emous
 *
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