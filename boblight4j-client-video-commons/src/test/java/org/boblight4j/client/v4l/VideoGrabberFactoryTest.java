package org.boblight4j.client.v4l;

import org.boblight4j.client.grabber.Grabber;
import org.boblight4j.client.video.ImageGrabberFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class VideoGrabberFactoryTest {

	private ImageGrabberFactory testable;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		testable = new ImageGrabberFactory();
	}

	@Test
	public void testGetImageGrabber() {
		thrown.expect(RuntimeException.class);

		Grabber imageGrabber = testable.getImageGrabber(null, false, 0, 0);
	}

}
