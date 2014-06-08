package org.boblight4j.client;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;

import junit.framework.Assert;

import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class AbstractFlagManagerTest {

	private AbstractFlagManager<CommandLineArgs> testable;

	@Before
	public void setUp() throws Exception {
		this.testable = new AbstractFlagManager<CommandLineArgs>() {

		};
	}

	@Test
	public void testParseDefaultBoblightOptions() throws BoblightException {

		this.testable.parseFlags("-o saturation=2.0".split("\\s"));

		final Client client = Mockito.mock(Client.class);
		this.testable.parseBoblightOptions(client);

		Mockito.verify(client).setOption(null, "saturation 2.0");
	}

	@Test
	public void testParseBoblightOptionsLightSettings()
			throws BoblightException {
		final Client client = Mockito.mock(Client.class);
		final LightsHolder lightsHolder = mock(LightsHolder.class);

		this.testable.parseFlags("-o light1:saturation=2.0".split("\\s"));

		when(lightsHolder.getLights()).thenReturn(mock(Collection.class));
		when(client.getLightsHolder()).thenReturn(lightsHolder);
		when(client.getLightsHolder().getLights().size()).thenReturn(1);
		this.testable.parseBoblightOptions(client);

		Mockito.verify(client).setOption("light1", "saturation 2.0");
	}

	@Test
	public void testParseBoblightOptionsPrintHelp() throws BoblightException {

		this.testable.parseFlags("-l".split("\\s"));

		assertTrue(this.testable.isPrintOptions());
	}

	@Test
	public void testParseFlags() throws BoblightConfigurationException {

		// first pass

		String host = "localhost";
		int port = 65534;
		int priority = 255;
		boolean sync = true;

		String command = "-p " + priority + " -s " + host + ":" + port + " -y "
				+ sync;
		this.testable.parseFlags(command.split("\\s"));

		Assert.assertEquals(port, this.testable.getPort());
		Assert.assertEquals(host, this.testable.getAddress());
		Assert.assertEquals(priority, this.testable.getPriority());
		Assert.assertEquals(sync, this.testable.isSync());

		// second pass

		host = "remotehost";
		port = 0;
		priority = 0;
		sync = false;

		command = "-p " + priority + " -s " + host + ":" + port + " -y " + sync;
		this.testable.parseFlags(command.split("\\s"));

		Assert.assertEquals(port, this.testable.getPort());
		Assert.assertEquals(host, this.testable.getAddress());
		Assert.assertEquals(priority, this.testable.getPriority());
		Assert.assertEquals(sync, this.testable.isSync());
	}

}
