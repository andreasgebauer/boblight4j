package org.boblight4j.server;

import static org.junit.Assert.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.boblight4j.server.config.ConfigLine;
import org.boblight4j.server.config.ConfigReader;
import org.boblight4j.server.config.Device;
import org.boblight4j.server.config.LightConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

public class TcpServerConfigCreatorTest {

	private TcpServerConfigCreator testable;

	private ConfigReader configReader;

	private SocketClientsHandlerImpl clientsHandler;

	@Before
	public void setUp() throws Exception {
		configReader = mock(ConfigReader.class);
		this.clientsHandler = mock(SocketClientsHandlerImpl.class);
		testable = new TcpServerConfigCreator(configReader);
	}

	@Test
	public void testBuildClientsHandlerConfig() throws Exception {

		final List<Device> devices = new ArrayList<Device>();
		final List<LightConfig> lights = new ArrayList<LightConfig>();
		final String hostname = "localhost";

		ArrayList<ConfigLine> gloablCfgLines = new ArrayList<ConfigLine>();
		gloablCfgLines.add(new ConfigLine("interface " + hostname, 1));
		gloablCfgLines.add(new ConfigLine("port 19333", 1));

		when(configReader.getGlobalConfigLines()).thenReturn(gloablCfgLines);

		this.testable.buildClientsHandlerConfig(this.clientsHandler);

		verify(this.clientsHandler).setInterface(
				argThat(new ArgumentMatcher<InetAddress>() {
					@Override
					public boolean matches(Object item) {
						if (item instanceof InetAddress
								&& ((InetAddress) item).getHostName().equals(
										hostname))
							return true;
						return false;
					}
				}), eq(19333));

	}

}
