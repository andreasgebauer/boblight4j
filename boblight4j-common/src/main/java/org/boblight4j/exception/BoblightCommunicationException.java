package org.boblight4j.exception;

/**
 * Exception class for communication errors between client and server.
 * 
 * @author agebauer
 * 
 */
public final class BoblightCommunicationException extends BoblightException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5387542278821627805L;

	/**
	 * Constructs a new communication exception.
	 * 
	 * @param cause
	 *            the underlying exception
	 */
	public BoblightCommunicationException(final Exception cause) {
		super(cause);
	}

	/**
	 * Constructs a new communication exception.
	 * 
	 * @param message
	 *            the error message
	 */
	public BoblightCommunicationException(final String message) {
		super(message);
	}

}
