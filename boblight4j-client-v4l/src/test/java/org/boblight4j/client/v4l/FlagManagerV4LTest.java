package org.boblight4j.client.v4l;

import static junit.framework.Assert.assertTrue;
import junit.framework.Assert;

import org.boblight4j.client.v4l.FlagManagerV4l.V4lArgs;
import org.junit.Before;
import org.junit.Test;

public class FlagManagerV4LTest {

	private FlagManagerV4l testable;

	@Before
	public void testCFlagManagerV4L() {
		this.testable = new FlagManagerV4l();
	}

	@Test
	public void testParseFlagsExtended() throws Exception {
		// final String argLine =
		// "-c /dev/video1 -i 1 -w 128x128 -o speed=40.0 -o interpolation=true -o use=true -o threshold=0 -o value=2 -y off -p 0 -d";

		final String argLine = "-c /dev/video1 -w 128x128 -o speed=40.0 -o interpolation=true -o use=true -o threshold=0 -o value=2 -y off -p 0 -d";
		final String[] args = argLine.split("\\s");
		V4lArgs parseFlags = this.testable.parseFlags(args);

		assertTrue(parseFlags.isDebug());

		Assert.assertTrue(this.testable.debug);
	}

	@Test
	public void testPostGetopt() {
	}

}
