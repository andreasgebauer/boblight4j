package org.boblight4j.client.grabber;

import org.boblight4j.exception.BoblightException;

public interface ActiveGrabber extends Grabber {

	/**
	 * Run loop.
	 * 
	 * @throws BoblightException
	 */
	void run() throws BoblightException;

	int[] grabPixelAt(int xpos, int ypos);

}
