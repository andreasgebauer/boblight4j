package org.boblight4j.utils;

import static junit.framework.Assert.assertEquals;

import org.boblight4j.exception.BoblightParseException;
import org.junit.Test;

public class StdIOTest {

	@Test
	public void testSscanf() throws BoblightParseException {
		Object[] sscanf = StdIO.sscanf("70x75", "%ix%i");

		assertEquals(70, sscanf[0]);
		assertEquals(75, sscanf[1]);
	}
}
