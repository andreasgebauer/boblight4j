package org.boblight4j.client.v4l;

import org.boblight4j.client.ClientImpl;
import org.boblight4j.client.grabber.Grabber;
import org.boblight4j.client.video.ImageGrabberServiceProvider;

public class ImageGrabberServiceProviderLinux implements
		ImageGrabberServiceProvider {

	@Override
	public final Grabber create(final ClientImpl client, boolean sync, int width,
			int height) {
		return new V4LImageGrabberImpl(client, sync, width, height);
	}

}
