package org.boblight4j.client.v4l;

import org.boblight4j.client.Client;
import org.boblight4j.client.grabber.Grabber;
import org.boblight4j.client.video.ImageGrabberServiceProvider;

/**
 * Service Provider implementation for V4L grabber.
 * 
 * @author agebauer
 * 
 */
public class ImageGrabberServiceProviderLinux implements
		ImageGrabberServiceProvider {

	@Override
	public final Grabber create(final Client client, boolean sync,
			int width, int height) {
		return new V4LImageGrabberImpl(client, sync, width, height);
	}

}
