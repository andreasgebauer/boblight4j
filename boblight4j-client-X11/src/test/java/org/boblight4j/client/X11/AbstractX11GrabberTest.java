package org.boblight4j.client.X11;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gnu.x11.Display;
import gnu.x11.Screen;

import java.io.IOException;

import org.boblight4j.client.Client;
import org.boblight4j.exception.BoblightException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

public class AbstractX11GrabberTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

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
		int scrWidth = 1280;
		int scrHeight = 720;
		int size = 16;

		Display display = mock(Display.class);
		display.default_screen = mock(Screen.class);
		display.default_screen.height = scrHeight;
		display.default_screen.width = scrWidth;

		Whitebox.setInternalState(testable, "size", size);
		when(testable.getDisplay()).thenReturn(display);

		// stop after first cycle
		doAnswer(new Answer() {

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
				verify(testable).grabPixelAt(
						(int) (cellWidth / 2 + cellWidth * width),
						(int) (scrHeight / size / 2 + cellHeight * height));
				verify(client).addPixel(width, height, null);
			}
		}
	}
}
