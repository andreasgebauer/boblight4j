package org.boblight4j.client.grabber;

import org.boblight4j.client.FlagManager;
import org.boblight4j.exception.BoblightException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AbstractSmartActiveGrabberTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	private AbstractSmartActiveGrabber grabber;

	@Before
	public void setUp() throws Exception {
		this.grabber = new AbstractSmartActiveGrabber(null, false, 0, 0) {

			@Override
			public void setup(FlagManager flagManager) throws BoblightException {

			}

			@Override
			public void cleanup() {

			}

			@Override
			public int[] grabPixelAt(int xpos, int ypos) {
				return null;
			}

			@Override
			protected void updateDimensions() {

			}

			@Override
			protected int getScreenWidth() {
				return 0;
			}

			@Override
			protected int getScreenHeight() {
				return 0;
			}
		};
	}

	@Test
	public void test() {
		grabber.grabPixelAt(0, 0);
	}

}
