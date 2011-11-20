package org.boblight4j.client.jmf;

import org.boblight4j.client.video.ImageGrabber;
import org.boblight4j.client.video.ImageGrabberServiceProvider;

public class VideoGrabberJMFProvider implements ImageGrabberServiceProvider {

	@Override
	public final ImageGrabber create() {
		return new JMFVideoGrabber();
	}

}
