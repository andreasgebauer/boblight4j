package org.boblight4j.client.jmf;

import org.boblight4j.client.ClientImpl;
import org.boblight4j.client.grabber.Grabber;
import org.boblight4j.client.video.ImageGrabberServiceProvider;

public class VideoGrabberJMFProvider implements ImageGrabberServiceProvider {

	@Override
	public Grabber create(ClientImpl client, boolean sync, int width, int height) {
		return new JMFVideoGrabber(client, sync, width, height);
	}

}
