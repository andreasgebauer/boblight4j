package de.gebauer.boblight4j.xbmc;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.boblight4j.client.Client;
import org.boblight4j.exception.BoblightException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class InputStreamRGBReaderTest {

	private final class RGBHandlerImplementation implements RGBHandler {
		private boolean stop;
		private List<RGBValue> handled = new ArrayList<RGBValue>();

		@Override
		public void setScanRange(String substring) {
		}

		@Override
		public void handle(RGBValue parse) {
			this.handled.add(parse);
		}

		@Override
		public void stop() {
			this.stop = true;
		}

		@Override
		public void sendRgb(boolean b, Object object) throws IOException,
				BoblightException {
		}

		@Override
		public Client getClient() {
			return null;
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws InterruptedException {
		final InputStreamRGBReader inputStreamRGBReader = new InputStreamRGBReader();
		final byte[] bytes = "scan20,20\n10,10:255,255,255\n\n\n\r\n"
				.getBytes();
		final ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		final RGBHandlerImplementation handler = new RGBHandlerImplementation();
		inputStreamRGBReader.setup(is, handler);
		final Thread thread = new Thread(inputStreamRGBReader);
		thread.start();

		thread.join();

		assertTrue(handler.stop);
		assertEquals(1, handler.handled.size());
		assertEquals(10, handler.handled.get(0).xPos);
		assertEquals(10, handler.handled.get(0).yPos);
		assertEquals(255, handler.handled.get(0).rgb[0]);
		assertEquals(255, handler.handled.get(0).rgb[1]);
		assertEquals(255, handler.handled.get(0).rgb[2]);

	}
}
