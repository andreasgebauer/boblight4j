package org.boblight4j.client.grabber;

import java.awt.image.BufferedImage;

/**
 * Interface which all passive grabbers implement.
 * 
 * @author agebauer
 * 
 */
public interface PassiveGrabber extends Grabber {

	/**
	 * Converts the 
	 * 
	 * @param img
	 */
	void frameToBoblight(final BufferedImage img);

}
