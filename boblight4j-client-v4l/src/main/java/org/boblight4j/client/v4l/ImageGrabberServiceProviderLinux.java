package org.boblight4j.client.v4l;

import org.boblight4j.client.video.ImageGrabber;
import org.boblight4j.client.video.ImageGrabberServiceProvider;

public class ImageGrabberServiceProviderLinux implements
		ImageGrabberServiceProvider {

	@Override
	public final ImageGrabber create() {
		return new V4LImageGrabberImpl();
	}

}
