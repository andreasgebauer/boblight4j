package org.boblight4j.client.grabber;

import org.boblight4j.client.FlagManager;
import org.boblight4j.exception.BoblightException;

public interface Grabber {

	/**
	 * Setup the grabber. All initialisation should be done in this method.
	 * 
	 * @param flagManager
	 *            the flag manager
	 * @throws BoblightException
	 *             if setup fails
	 */
	void setup(FlagManager flagManager) throws BoblightException;

	/**
	 * Setup the debug output.
	 */
	void setupDebug();

	/**
	 * Cleans up this grabber.
	 */
	void cleanup();

}
