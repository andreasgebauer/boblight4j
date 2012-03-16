package org.boblight4j.client.grabber;

import org.boblight4j.client.AbstractFlagManager;
import org.boblight4j.exception.BoblightException;

public interface Grabber {

	/**
	 * Setup.
	 * 
	 * @param flagManager
	 * @throws BoblightException
	 */
	void setup(final AbstractFlagManager flagManager) throws BoblightException;

	/**
	 * Cleanup.
	 */
	void cleanup();

	public abstract void setupDebug();

}
