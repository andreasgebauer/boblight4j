package de.gebauer.boblight4j.xbmc;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

import org.boblight4j.client.LightsHolder;
import org.boblight4j.client.RemoteClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class InputStreamGrabberTest {

	private InputStreamGrabber testable;

	@Mock
	private RemoteClient mock;

	@Mock
	private LightsHolder lightsHolder;

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);
		when(mock.getLightsHolder()).thenReturn(lightsHolder);

		testable = new InputStreamGrabber(mock, true, 20, 20);
	}

	@Test
	public void testSetScanRange() {
		testable.setScanRange("20,20");

		verify(lightsHolder).setScanRange(20, 20);
	}

}
