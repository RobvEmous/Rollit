package exceptions;

import java.io.IOException;

/**
 * Signals that the server and client do not follow the same protocol. 
 * Probably because a request timed out or the answer of the client/server 
 * was an unrecognized command (without an Client- or 
 * ServerNotFoundException being thrown).
 * 
 * @author Rob van Emous
 *
 */
public class ProtecolNotFollowedException extends Exception {
	private static final long serialVersionUID = 2778268293915773778L;


	public ProtecolNotFollowedException() {
        super();
    }

    public ProtecolNotFollowedException(String message) {
        super(message);
    }


    public ProtecolNotFollowedException(String message, Throwable cause) {
        super(message, cause);
    }


    public ProtecolNotFollowedException(Throwable cause) {
        super(cause);
    }

}