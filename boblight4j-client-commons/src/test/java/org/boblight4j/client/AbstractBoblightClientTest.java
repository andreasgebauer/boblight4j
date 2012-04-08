package org.boblight4j.client;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractBoblightClient.class })
public class AbstractBoblightClientTest {

	@Mock
	private FlagManager flagManager;

	private AbstractBoblightClient testable;

	@Before
	public void setUp() throws Exception {
		final String[] args = "".split("\\s");
		this.testable = new AbstractBoblightClient(args) {

			@Override
			protected FlagManager getFlagManager() {
				return AbstractBoblightClientTest.this.flagManager;
			}

			@Override
			protected int run() {
				return 0;
			}
		};
	}

	@Test
	public void testDoRun() {

		PowerMockito.mockStatic(Runtime.class);

		final Runtime runtime = Mockito.mock(Runtime.class);
		when(Runtime.getRuntime()).thenReturn(runtime);

		this.testable.doRun();

		verify(runtime).addShutdownHook(Matchers.any(Thread.class));
	}

	@Test
	public void testGetFlagManager() {
		assertEquals(this.flagManager, this.testable.getFlagManager());
	}

	@Test
	public void testParseArgs() throws BoblightConfigurationException {
		final String[] args = "".split("\\s");

		// constructor called parseFlags

		verify(this.flagManager).parseFlags(args);
	}

	@Test
	public void testTrySetup() throws BoblightException {
		ClientImpl client = mock(ClientImpl.class);
		this.testable.trySetup(client);

		verify(client).connect(null, 0, 5000);
	}

}
