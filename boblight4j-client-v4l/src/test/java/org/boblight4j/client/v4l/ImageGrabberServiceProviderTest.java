package org.boblight4j.client.v4l;

import org.boblight4j.client.video.ImageGrabberFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ImageGrabberServiceProviderTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		new ImageGrabberFactory().getImageGrabber(null, false, 0, 0);
	}

}
