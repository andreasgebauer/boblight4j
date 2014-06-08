package org.boblight4j.client.grabber;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;

import org.boblight4j.client.Client;
import org.boblight4j.client.LightsHolder;
import org.boblight4j.client.RemoteClient;
import org.boblight4j.client.FlagManager;
import org.boblight4j.exception.BoblightException;
import org.junit.Before;
import org.junit.Test;

public class AbstractPassiveGrabberTest {

	private AbstractPassiveGrabber testable;

	@Before
	public void setUp() throws Exception {
		final Client client = mock(Client.class);
		final LightsHolder lightsHldr = mock(LightsHolder.class);
		when(client.getLightsHolder()).thenReturn(lightsHldr);
		testable = new AbstractPassiveGrabber(client, false, 100, 80) {

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
