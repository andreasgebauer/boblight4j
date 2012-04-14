package org.boblight4j.server.config;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Vector;

import org.boblight4j.device.AbstractDevice;
import org.boblight4j.device.Light;
import org.boblight4j.exception.BoblightException;
import org.boblight4j.server.ClientsHandler;
import org.boblight4j.server.RemoteClientsHandlerImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.powermock.reflect.internal.WhiteboxImpl;

public class ConfigTest {

	@Rule
	public ExpectedException ex = ExpectedException.none();

	private TcpServerConfigImpl testable;

	@Before
	public void setUp() {
		this.testable = new TcpServerConfigImpl();
	}

	@Test
	public void testBuildConfig() throws BoblightException {
		final Vector<Light> lights = new Vector<Light>();
		final Vector<AbstractDevice> devices = new Vector<AbstractDevice>();
		this.testable.buildConfig(Mockito.mock(RemoteClientsHandlerImpl.class), devices,
				lights);
	}

	@Test
	public void testCheckConfig() throws BoblightException,
			IllegalArgumentException, IllegalAccessException {

		this.ex.expect(BoblightException.class);

		final Vector<ConfigLine> m_globalconfiglines = new Vector<ConfigLine>();
		m_globalconfiglines.add(new ConfigLine("interface 127.0.0.1", 2));
		m_globalconfiglines.add(new ConfigLine("port 127.0.0.1", 3));

		final Field field = WhiteboxImpl.getField(TcpServerConfigImpl.class,
				"globalConfigLines");

		field.set(this.testable, m_globalconfiglines);

		this.testable.checkConfig();
	}

	@Test
	public void testLoadConfigFromFile() throws Exception {
		this.testable.loadConfigFromFile(new File(ConfigTest.class.getResource(
				"/boblight.10pc.conf").toURI()));
	}

}
