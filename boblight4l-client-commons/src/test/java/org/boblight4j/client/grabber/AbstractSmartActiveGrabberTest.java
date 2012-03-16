package org.boblight4j.client.grabber;

import org.boblight4j.client.AbstractFlagManager;
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
			public void setup(AbstractFlagManager flagManager)
					throws BoblightException {
				// TODO Auto-generated method stub

			}

			@Override
			public void cleanup() {
				// TODO Auto-generated method stub

			}

			@Override
			public int[] grabPixelAt(int xpos, int ypos) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			protected void updateDimensions() {
				// TODO Auto-generated method stub

			}

			@Override
			protected int getScreenWidth() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			protected int getScreenHeight() {
				// TODO Auto-generated method stub
				return 0;
			}
		};
	}

	@Test
	public void test() {

	}

}
