package org.boblight4j.client;

import org.boblight4j.exception.BoblightConfigurationException;
import org.junit.Ignore;
import org.junit.Test;

public class FlagManagerConstantTest {

	@Test(expected = BoblightConfigurationException.class)
	public void test() throws BoblightConfigurationException {
		final FlagManagerConstant flagManagerConstant = new FlagManagerConstant();

		flagManagerConstant.parseFlags(new String[] { "-o", "sss" });
	}

	@Test
	@Ignore
	public void testPrintUsage() throws BoblightConfigurationException {
		final FlagManagerConstant flagManagerConstant = new FlagManagerConstant();

		flagManagerConstant.parseFlags(new String[] { "-h" });
	}

}
