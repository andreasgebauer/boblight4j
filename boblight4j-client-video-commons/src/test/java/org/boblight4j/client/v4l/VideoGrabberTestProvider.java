package org.boblight4j.client.v4l;

import org.boblight4j.client.video.ImageGrabber;
import org.boblight4j.client.video.ImageGrabberServiceProvider;
import org.mockito.Mockito;

public class VideoGrabberTestProvider implements ImageGrabberServiceProvider {

	@Override
	public ImageGrabber create() {
		return Mockito.mock(ImageGrabber.class);
	}

}
