package exceptions;

/**
 * Signals that the server and client do not follow the same protocol. 
 * Probably because a request timed out or the answer of the client/server 
 * was an unrecognized command (without an Client- or 
 * ServerNotFoundException being thrown).
 * 
 * @author Rob van Emous
 *
 */
public class ProtocolNotFollowedException extends Exception {
	private static final long serialVersionUID = 2778268293915773778L;


	public ProtocolNotFollowedException() {
        super();
    }

    public ProtocolNotFollowedException(String message) {
        super(message);
    }


    public ProtocolNotFollowedException(String message, Throwable cause) {
        super(message, cause);
    }


    public ProtocolNotFollowedException(Throwable cause) {
        super(cause);
    }

}