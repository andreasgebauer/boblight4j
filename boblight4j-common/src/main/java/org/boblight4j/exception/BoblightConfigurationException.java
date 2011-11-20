package org.boblight4j.exception;

public final class BoblightConfigurationException extends BoblightException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7543937871817962155L;

	public BoblightConfigurationException(final String message) {
		super(message);
	}

	public BoblightConfigurationException(final String message,
			final Exception cause) {
		super(message, cause);
	}

}
