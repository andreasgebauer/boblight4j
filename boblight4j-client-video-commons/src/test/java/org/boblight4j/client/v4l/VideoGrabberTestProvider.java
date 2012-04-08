package org.boblight4j.client.v4l;

import org.boblight4j.client.ClientImpl;
import org.boblight4j.client.grabber.Grabber;
import org.boblight4j.client.video.ImageGrabberServiceProvider;
import org.mockito.Mockito;

public class VideoGrabberTestProvider implements ImageGrabberServiceProvider {

	@Override
	public Grabber create(ClientImpl client, boolean sync, int width, int height) {
		return Mockito.mock(Grabber.class);
	}

}
