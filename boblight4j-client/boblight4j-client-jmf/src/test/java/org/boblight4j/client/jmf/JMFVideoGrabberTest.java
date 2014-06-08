package org.boblight4j.client.jmf;

import org.junit.Before;
import org.junit.Test;

public class JMFVideoGrabberTest {

	private JMFVideoGrabber testable;

	@Before
	public void setUp() throws Exception {
		this.testable = new JMFVideoGrabber(null, false, 100, 80);
	}

	@Test
	public void testCleanup() {
	}

	@Test
	public void testGetError() {
	}

	@Test
	public void testRun() {
	}

	@Test
	public void testSetup() throws Exception {
		this.testable.setup(null);
	}

}
