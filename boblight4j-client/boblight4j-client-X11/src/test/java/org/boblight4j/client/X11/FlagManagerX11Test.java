package org.boblight4j.client.X11;

import static org.junit.Assert.*;

import org.boblight4j.exception.BoblightConfigurationException;
import org.junit.Test;

public class FlagManagerX11Test {

	private FlagManagerX11 testable;

	@Test(expected = BoblightConfigurationException.class)
	public void testParseFlagsExtendedX11Flags()
			throws BoblightConfigurationException {
		testable = new FlagManagerX11();

		testable.parseFlags("".split("\\s"));
	}

	@Test
	public void testParseFlagsxtendedX11Flags()
			throws BoblightConfigurationException {
		testable = new FlagManagerX11();

		testable.parseFlags("-u 16 -x -i 1.0 -d".split("\\s"));

		assertEquals(16, testable.getPixels());
		assertEquals(1.0, testable.getInterval(), 0);
	}

}
