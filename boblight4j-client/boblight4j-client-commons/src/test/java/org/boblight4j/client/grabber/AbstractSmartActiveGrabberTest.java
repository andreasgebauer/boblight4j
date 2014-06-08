package org.boblight4j.client.grabber;

import org.boblight4j.client.Client;
import org.boblight4j.client.FlagManager;
import org.boblight4j.exception.BoblightException;
import org.junit.Before;
import org.junit.Test;

public class AbstractSmartActiveGrabberTest {

	private final class AbstractSmartActiveGrabberImpl extends
			AbstractSmartActiveGrabber {
		private AbstractSmartActiveGrabberImpl(Client client, boolean sync,
				int width, int height) {
			super(client, sync, width, height);
		}

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
	}

	private AbstractSmartActiveGrabber grabber;

	@Before
	public void setUp() throws Exception {
		this.grabber = new AbstractSmartActiveGrabberImpl(null, false, 100, 80);
	}

	@Test
	public void test() {
		grabber.grabPixelAt(0, 0);
	}

}
