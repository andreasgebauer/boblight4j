package org.boblight4j.exception;

public class BoblightException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BoblightException(final Exception e) {
		super(e);
	}

	public BoblightException(final String message) {
		super(message);
	}

	public BoblightException(final String message, final Exception cause) {
		super(message, cause);
	}

}
