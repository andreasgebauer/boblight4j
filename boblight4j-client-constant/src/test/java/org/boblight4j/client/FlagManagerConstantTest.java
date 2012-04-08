package org.boblight4j.client;

import org.boblight4j.exception.BoblightConfigurationException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FlagManagerConstantTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws BoblightConfigurationException {
		final FlagManagerConstant flagManagerConstant = new FlagManagerConstant();

		flagManagerConstant.parseFlags(new String[] { "-o", "sss" });
	}

	@Test
	public void testPrintUsage() throws BoblightConfigurationException {
		final FlagManagerConstant flagManagerConstant = new FlagManagerConstant();

		flagManagerConstant.parseFlags(new String[] { "-h" });
	}

}
