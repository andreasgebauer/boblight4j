package org.boblight4j.exception;

public class BoblightRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8590071228229375898L;

	public BoblightRuntimeException(String message) {
		super(message);
	}

	public BoblightRuntimeException(String message, Exception cause) {
		super(message, cause);
	}

}
