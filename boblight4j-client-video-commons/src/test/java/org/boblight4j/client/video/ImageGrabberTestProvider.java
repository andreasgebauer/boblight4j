package org.boblight4j.client.video;

import org.boblight4j.client.Client;
import org.boblight4j.client.grabber.Grabber;
import org.boblight4j.client.video.ImageGrabberServiceProvider;
import org.mockito.Mockito;

public class ImageGrabberTestProvider implements ImageGrabberServiceProvider {

	@Override
	public Grabber create(Client client, boolean sync, int width, int height) {
		return Mockito.mock(Grabber.class);
	}

}
