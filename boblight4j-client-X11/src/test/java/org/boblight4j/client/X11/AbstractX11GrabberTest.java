package org.boblight4j.client.X11;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gnu.x11.Display;
import gnu.x11.Screen;

import java.io.IOException;
import java.util.Random;

import org.boblight4j.client.Client;
import org.boblight4j.exception.BoblightException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

public class AbstractX11GrabberTest {

	private static final int SCREEN_WIDTH_MIN = 320;
	private static final int SCREEN_WIDTH_MAX = 1920;

	private static final int SCREEN_HEIGHT_MIN = 240;
	private static final int SCREEN_HEIGHT_MAX = 1080;

	private AbstractX11Grabber testable;

	private Client client;

	@Before
	public void setUp() throws Exception {
		client = mock(Client.class);
		testable = spy(new AbstractX11Grabber(client, false) {

			@Override
			protected int[] grabPixelAt(int xpos, int ypos) {
				return null;
			}
		});
	}

	@Test
	public void testGrabPixelAt() throws IOException, BoblightException {
		final Random random = new Random();;
		int scrWidth = SCREEN_WIDTH_MIN
				+ random.nextInt(SCREEN_WIDTH_MAX - SCREEN_WIDTH_MIN);
		int scrHeight = SCREEN_HEIGHT_MIN
				+ random.nextInt(SCREEN_HEIGHT_MAX - SCREEN_HEIGHT_MIN);
		int size = 2 + random.nextInt(64 - 2);

		Display display = mock(Display.class);
		display.default_screen = mock(Screen.class);
		display.default_screen.height = scrHeight;
		display.default_screen.width = scrWidth;

		Whitebox.setInternalState(testable, "size", size);
		when(testable.getDisplay()).thenReturn(display);

		// stop after first cycle
		doAnswer(new Answer<Object>() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Whitebox.setInternalState(testable, "stop", true);
				return null;
			}
		}).when(client).sendRgb(false, null);

		testable.run();

		double cellWidth = (double) scrWidth / (double) size;
		double cellHeight = (double) scrHeight / size;

		for (int width = 0; width < size; width++)
		{
			for (int height = 0; height < size; height++)
			{
				final int xpos = (int) (cellWidth / 2 + cellWidth * width);
				final int ypos = (int) (cellHeight / 2 + cellHeight * height);

				verify(testable).grabPixelAt(xpos, ypos);
				verify(client).addPixel(width, height, null);
			}
		}
	}

}
