package org.boblight4j.client.video;

import org.boblight4j.client.ClientImpl;
import org.boblight4j.client.AbstractFlagManager;
import org.boblight4j.exception.BoblightException;

/**
 * 
 * @author agebauer
 */
public interface ImageGrabber {

	/**
	 * 
	 * @param flagManager
	 *            the flag manager
	 * @param client
	 * @throws BoblightException
	 */
	void setup(AbstractFlagManager flagManager, ClientImpl client)
			throws BoblightException;

	void run() throws BoblightException;

	void cleanup();

}
