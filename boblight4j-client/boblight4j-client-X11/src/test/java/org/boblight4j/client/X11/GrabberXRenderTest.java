package org.boblight4j.client.X11;

import static org.mockito.Mockito.mock;

import org.boblight4j.client.Client;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

//will fail if X11 does not listen on tcp
@Ignore
public class GrabberXRenderTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	private GrabberXRender testable;

	@Before
	public void setUp() throws Exception {
		testable = new GrabberXRender(mock(Client.class), true, 64, 64, .1f);
		final FlagManagerX11 flagManager = new FlagManagerX11();
		flagManager.parseFlags("-x -u 32 -s openelec".split("\\s"));
		testable.setup(flagManager);
	}

	@Test
	public void testGrabPixelAt() {
		testable.grabPixelAt(0, 0);
	}

}
