package org.boblight4j.client.grabber;

import java.awt.image.BufferedImage;

public interface PassiveGrabber {

	void frameToBoblight(final BufferedImage img);

}
