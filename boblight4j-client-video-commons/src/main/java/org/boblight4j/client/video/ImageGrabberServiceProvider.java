package org.boblight4j.client.video;

import org.boblight4j.client.Client;
import org.boblight4j.client.grabber.Grabber;

/**
 * Service Provider Interface for image grabber services.
 * 
 * @author agebauer
 * 
 */
public interface ImageGrabberServiceProvider {

	/**
	 * Should create a grabber for the given client, sync mode and width and
	 * height.
	 * 
	 * @param client
	 *            the client to use
	 * @param sync
	 *            true whether the server should be set to sync mode
	 * @param width
	 *            the grab width
	 * @param height
	 *            the grab height
	 * @return a grabber
	 */
	Grabber create(Client client, boolean sync, int width, int height);

}
