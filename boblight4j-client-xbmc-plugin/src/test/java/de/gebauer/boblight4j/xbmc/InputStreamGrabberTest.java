package de.gebauer.boblight4j.xbmc;

import static org.mockito.Mockito.verify;

import org.boblight4j.client.Client;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class InputStreamGrabberTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	private InputStreamGrabber testable;
	@Mock
	private Client mock;

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);

		testable = new InputStreamGrabber(mock, true, 20, 20);
	}

	@Test
	public void testSetScanRange() {
		testable.setScanRange("20,20");

		verify(mock).setScanRange(20, 20);
	}

}
