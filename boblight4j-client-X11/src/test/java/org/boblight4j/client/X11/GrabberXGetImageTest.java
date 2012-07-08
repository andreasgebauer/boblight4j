package org.boblight4j.client.X11;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gnu.x11.Display;
import gnu.x11.Window;
import gnu.x11.image.Image;
import gnu.x11.image.Image.Format;
import gnu.x11.image.ZPixmap;

import org.boblight4j.client.Client;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractX11Grabber.class })
public class GrabberXGetImageTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	private GrabberXGetImage testable;
	private Window window;

	@Before
	public void setUp() throws Exception {
		Display display = mock(Display.class);
		window = display.default_root = mock(Window.class);
		PowerMockito.whenNew(Display.class).withNoArguments()
				.thenReturn(display);

		testable = new GrabberXGetImage(mock(Client.class), true, 64, 64, .1f);

		final FlagManagerX11 flagManager = new FlagManagerX11();
		flagManager.parseFlags("-x -u 32".split("\\s"));
		testable.setup(flagManager);
	}

	@Test
	public void testGrabPixelAt() {
		ZPixmap imageMock = mock(ZPixmap.class);
		Mockito.when(
				window.image(0, 0, 1, 1, GrabberXGetImage.ALL_PLANES,
						Format.ZPIXMAP)).thenReturn(imageMock);

		when(imageMock.get_red(0, 0)).thenReturn(111);
		when(imageMock.get_green(0, 0)).thenReturn(222);
		when(imageMock.get_blue(0, 0)).thenReturn(33);

		int[] grabPixelAt = testable.grabPixelAt(0, 0);

		assertEquals(111, grabPixelAt[0]);
		assertEquals(222, grabPixelAt[1]);
		assertEquals(33, grabPixelAt[2]);
	}
}
