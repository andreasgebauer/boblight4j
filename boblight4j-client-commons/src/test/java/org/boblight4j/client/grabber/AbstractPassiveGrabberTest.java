package org.boblight4j.client.grabber;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.awt.image.BufferedImage;

import org.boblight4j.client.Client;
import org.boblight4j.client.FlagManager;
import org.boblight4j.exception.BoblightException;
import org.junit.Before;
import org.junit.Test;

public class AbstractPassiveGrabberTest {

	private AbstractPassiveGrabber testable;

	@Before
	public void setUp() throws Exception {
		testable = new AbstractPassiveGrabber(mock(Client.class), false, 100,
				80) {

			@Override
			public void setup(FlagManager flagManager) throws BoblightException {
			}

			@Override
			public void cleanup() {
			}
		};
	}

	@Test
	public void testFrameToBoblight() {
		testable.frameToBoblight(mock(BufferedImage.class));
	}

}
