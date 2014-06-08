package org.boblight4j.client.v4l;

import static junit.framework.Assert.assertNotNull;

import org.boblight4j.client.video.ImageGrabberFactory;
import org.junit.Test;

public class ImageGrabberServiceProviderTest {

	@Test
	public void test() {
		assertNotNull(new ImageGrabberFactory().getImageGrabber(null, false,
				100, 80));
	}

}
