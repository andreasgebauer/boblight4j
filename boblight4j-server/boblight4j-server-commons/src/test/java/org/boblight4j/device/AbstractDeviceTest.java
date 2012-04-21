package org.boblight4j.device;

import static org.mockito.Mockito.mock;

import org.boblight4j.exception.BoblightDeviceException;
import org.boblight4j.server.ClientsHandler;
import org.junit.Before;
import org.junit.Test;

public class AbstractDeviceTest {

	private AbstractDevice testable;

	@Before
	public void setUp() throws Exception {
		testable = new AbstractDevice(mock(ClientsHandler.class)) {

			@Override
			protected void writeOutput() throws BoblightDeviceException {
				this.stopThread();
			}

			@Override
			public void sync() {
			}

			@Override
			protected boolean setup() {
				return true;
			}

			@Override
			protected void close() {
			}
		};
	}

	@Test
	public void testRun() {
		testable.run();
	}

}
