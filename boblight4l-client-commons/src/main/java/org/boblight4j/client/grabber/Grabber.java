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
	 * Run loop.
	 * 
	 * @throws BoblightException
	 */
	void run() throws BoblightException;

	/**
	 * Cleanup.
	 */
	void cleanup();

}