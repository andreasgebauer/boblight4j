package org.boblight4j.client;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightException;
import org.boblight4j.exception.BoblightRuntimeException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Tests {@link AbstractBoblightClient}.
 * 
 * @author agebauer
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractBoblightClient.class, System.class, Thread.class })
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

	/**
	 * Tests that the flag manager is returned accordingly.
	 */
	@Test
	public void testGetFlagManager() {
		assertEquals(this.flagManager, this.testable.getFlagManager());
	}

	@Test
	public void testParseArgs() throws BoblightConfigurationException {
		final String[] args = "".split("\\s");

		// constructor called parseFlags in method setUp()

		verify(this.flagManager).parseFlags(args);
	}

	@Test
	public void testParseArgsThrowingException()
			throws BoblightConfigurationException {
		final String[] args = "".split("\\s");

		PowerMockito.mockStatic(System.class);

		doThrow(new BoblightConfigurationException("")).when(flagManager)
				.parseFlags(any(String[].class));

		this.testable.parseArgs(flagManager, args);

		PowerMockito.verifyStatic();
		System.exit(1);

		verify(this.flagManager).printHelpMessage();
	}

	@Test
	public void testParseArgsThrowingRuntimeException()
			throws BoblightConfigurationException {
		final String[] args = "".split("\\s");

		PowerMockito.mockStatic(System.class);

		doThrow(new BoblightRuntimeException("")).when(flagManager).parseFlags(
				any(String[].class));

		this.testable.parseArgs(flagManager, args);

		PowerMockito.verifyStatic();
		System.exit(1);

		verify(this.flagManager).printHelpMessage();
	}

	@Test
	public void testPrintHelp() throws BoblightConfigurationException {
		final String[] args = "".split("\\s");

		PowerMockito.mockStatic(System.class);

		when(flagManager.isPrintHelp()).thenReturn(true);

		this.testable.parseArgs(flagManager, args);

		PowerMockito.verifyStatic();
		System.exit(1);

		verify(this.flagManager).printHelpMessage();
	}

	@Test
	public void testPrintOptions() throws BoblightConfigurationException {
		final String[] args = "".split("\\s");

		PowerMockito.mockStatic(System.class);

		when(flagManager.isPrintOptions()).thenReturn(true);

		this.testable.parseArgs(flagManager, args);

		PowerMockito.verifyStatic();
		System.exit(1);

		verify(this.flagManager).printOptions();
	}

	@Test
	public void testTrySetup() throws BoblightException {
		ClientImpl client = mock(ClientImpl.class);
		this.testable.trySetup(client);

		verify(client).connect(null, 0, 5000);
	}

	@Test
	public void testTrySetupCatchingException() throws BoblightException {
		ClientImpl client = mock(ClientImpl.class);

		doThrow(new BoblightException("")).when(client).connect(anyString(),
				anyInt(), anyInt());

		PowerMockito.mockStatic(Thread.class);

		// will do a sleep of 10 secs
		this.testable.trySetup(client);

		verify(client).connect(null, 0, 5000);
	}
}
