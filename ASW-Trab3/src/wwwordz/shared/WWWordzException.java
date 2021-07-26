package wwwordz.shared;

/**
 * "A exception in WWWordz. All constructors delegate in the super class."
 * 
 * @see https://www.dcc.fc.up.pt/~zp/aulas/1920/asw/api/wwwordz/shared/WWWordzException.html
 */
public class WWWordzException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Empty constructor.
	 * 
	 */
	public WWWordzException() {
		super();
	}

	/**
	 * Creates an instance with a message and a Throwable cause
	 * 
	 * @param message - the message this exception shows
	 * @param cause - the cause that raised this exception
	 */
	public WWWordzException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates an instance with a message
	 * 
	 * @param message - the message this exception shows
	 */
	public WWWordzException(String message) {
		super(message);
	}

	/**
	 * Creates an instance with a Throwable cause
	 * 
	 * @param cause - the cause that raised this exception
	 */
	public WWWordzException(Throwable cause) {
		super(cause);
	}
	
}
