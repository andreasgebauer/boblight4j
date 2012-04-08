package org.boblight4j.client;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BoblightConstantTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		new BoblightConstant(new String[] { "-h" });
	}

}
