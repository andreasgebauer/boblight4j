package org.boblight4j.client.grabber;

import java.awt.image.BufferedImage;

/**
 * Interface which all passive grabbers implement.
 * 
 * @author agebauer
 * 
 */
public interface PassiveGrabber {

	void frameToBoblight(final BufferedImage img);

}
