package org.boblight4j.exception;

public final class BoblightCommunicationException extends BoblightException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5387542278821627805L;

	public BoblightCommunicationException(final Exception e) {
		super(e);
	}

	public BoblightCommunicationException(final String message) {
		super(message);
	}

}
