package org.boblight4j.exception;

public final class BoblightParseException extends BoblightException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5532251516636945388L;

	public BoblightParseException(final String message, final Exception cause) {
		super(message, cause);
	}

	public BoblightParseException(final Exception cause) {
		this(null, cause);
	}

	public BoblightParseException(final String message) {
		this(message, null);
	}

}
