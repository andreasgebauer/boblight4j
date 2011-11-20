package org.boblight4j.exception;

import java.io.IOException;

public final class BoblightDeviceException extends BoblightException {

	private static final long serialVersionUID = 1L;

	public BoblightDeviceException(final String message, final IOException cause) {
		super(message, cause);
	}

}
