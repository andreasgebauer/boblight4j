package org.boblight4j.server.config;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.boblight4j.device.AbstractDevice;
import org.boblight4j.device.Device;
import org.boblight4j.device.DeviceRS232;
import org.boblight4j.device.Light;
import org.boblight4j.server.SocketClientsHandlerImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

public class ConfigImplTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Mock
	private SocketClientsHandlerImpl clientsHandler;

	private TcpServerConfigImpl testable;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.testable = new TcpServerConfigImpl();
	}

	@Test
	public void testBuildColorConfig() throws Exception {
		this.testable.buildColorConfig();
	}

	@Test
	public void testBuildConfig() throws Exception {

		final List<Device> devices = new ArrayList<Device>();
		final List<Light> lights = new ArrayList<Light>();

		this.testable.buildConfig(this.clientsHandler, devices, lights);

		verify(this.clientsHandler).setInterface(
				argThat(new ArgumentMatcher<InetAddress>() {
					@Override
					public boolean matches(Object item) {
						if (item == null)
							return true;
						return false;
					}
				}), eq(19333));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testBuildDeviceConfig() throws Exception {
		final ConfigGroup cfgGrp = new ConfigGroup();
		cfgGrp.lines.add(new ConfigLine("name arduino", 1));
		cfgGrp.lines.add(new ConfigLine("output /dev/ttyACM0", 2));
		cfgGrp.lines.add(new ConfigLine("channels 60", 2));
		cfgGrp.lines.add(new ConfigLine("rate 115200", 2));
		cfgGrp.lines.add(new ConfigLine("interval 20000", 2));
		cfgGrp.lines.add(new ConfigLine("type momo", 2));
		cfgGrp.lines.add(new ConfigLine("prefix FF", 2));
		cfgGrp.lines.add(new ConfigLine("postfix 33", 2));
		cfgGrp.lines.add(new ConfigLine("escape 99", 2));
		((Collection<ConfigGroup>) Whitebox.getInternalState(testable,
				"deviceLines")).add(cfgGrp);

		final List<Device> devices = this.testable
				.buildDeviceConfig(this.clientsHandler);

		final DeviceRS232 device = (DeviceRS232) devices.get(0);
		Assert.assertEquals(255, device.getProtocol().getStartFlag());
		Assert.assertEquals(0x33, device.getProtocol().getEndFlag());
		Assert.assertEquals(0x99, device.getProtocol().getEscapeFlag());

	}

	@Test
	public void testBuildLightConfig() throws Exception {
		final List<Device> devices = new ArrayList<Device>();
		final List<Color> colors = new ArrayList<Color>();
		this.testable.buildLightConfig(devices, colors);
	}

	@Test
	public void testCheckConfig() throws Exception {
		this.testable.checkConfig();
	}

	@Test
	public void testClearConfig() {
		this.testable.clearConfig();
	}

	@Test
	public void testLoadConfigFromFile() throws Exception {
		this.testable.loadConfigFromFile(new File(ConfigImplTest.class
				.getResource("/boblight.50pc.conf").toURI()));
	}

}
