package org.boblight4j.client.grabber;

import org.boblight4j.client.FlagManager;
import org.boblight4j.exception.BoblightException;

public interface Grabber {

	/**
	 * Setup.
	 * 
	 * @param flagManager
	 * @throws BoblightException
	 */
	void setup(FlagManager flagManager) throws BoblightException;

	/**
	 * Cleanup.
	 */
	void cleanup();

	public abstract void setupDebug();

}
