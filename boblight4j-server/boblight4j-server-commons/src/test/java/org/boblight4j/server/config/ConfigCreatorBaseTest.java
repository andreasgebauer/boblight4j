package org.boblight4j.server.config;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.boblight4j.device.AbstractDevice;
import org.boblight4j.device.DeviceRS232;
import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightDeviceException;
import org.boblight4j.exception.BoblightException;
import org.boblight4j.server.ClientsHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

public class ConfigCreatorBaseTest {

	private ConfigCreatorBase testable;
	private PlainTextConfigFileReader configReader;
	private ClientsHandler<?> clientsHandler;

	@Before
	public void setUp() throws Exception {
		configReader = new PlainTextConfigFileReader(new File("."));
		testable = new ConfigCreatorBase(configReader) {

			@Override
			protected void buildClientsHandlerConfig(ClientsHandler<?> clients)
					throws Exception {
			}
		};
	}

	@Test(expected = BoblightConfigurationException.class)
	public void testLoadConfig() throws BoblightException {
		testable.loadConfig(null, mock(AbstractConfig.class));
	}

	@Test
	public void testBuildColorConfig() throws BoblightException {
		ConfigGroup e = new ConfigGroup();
		e.lines.add(new ConfigLine("name colorName", 1));
		e.lines.add(new ConfigLine("rgb 7F00FF", 2));
		e.lines.add(new ConfigLine("gamma 0.9f", 3));
		e.lines.add(new ConfigLine("adjust .5f", 4));
		e.lines.add(new ConfigLine("blacklevel 0.2f", 5));
		ArrayList<ConfigGroup> value = new ArrayList<ConfigGroup>();
		value.add(e);
		configReader.colorLines.add(e);

		List<ColorConfig> buildColorConfig = testable.buildColorConfig();
		ColorConfig colorConfig = buildColorConfig.get(0);
		assertEquals("colorName", colorConfig.getName());
		Assert.assertArrayEquals(new float[] { .5f, 0, 1.0f },
				colorConfig.getRgb(), 0.01f);
		assertEquals(.9f, colorConfig.getGamma(), 0);
		assertEquals(.5f, colorConfig.getAdjust(), 0);
		assertEquals(.2f, colorConfig.getBlacklevel(), 0);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testBuildDeviceConfig() throws Exception {
		final ConfigGroup cfgGrp = new ConfigGroup();
		cfgGrp.lines.add(new ConfigLine("name arduino", 1));
		cfgGrp.lines.add(new ConfigLine("output /dev/ttyACM0", 2));
		cfgGrp.lines.add(new ConfigLine("channels 60", 3));
		cfgGrp.lines.add(new ConfigLine("rate 115200", 4));
		cfgGrp.lines.add(new ConfigLine("interval 20000", 5));
		cfgGrp.lines.add(new ConfigLine("type momo", 6));
		cfgGrp.lines.add(new ConfigLine("prefix FF", 7));
		cfgGrp.lines.add(new ConfigLine("postfix 33", 8));
		cfgGrp.lines.add(new ConfigLine("escape 99", 9));
		Collection<ConfigGroup> deviceLines = (Collection<ConfigGroup>) Whitebox
				.getInternalState(configReader, "deviceLines");
		deviceLines.add(cfgGrp);

		final List<Device> devices = this.testable
				.buildDeviceConfig(this.clientsHandler);

		final DeviceRS232 device = (DeviceRS232) devices.get(0);
		Assert.assertEquals(255, device.getProtocol().getStartFlag());
		Assert.assertEquals(0x33, device.getProtocol().getEndFlag());
		Assert.assertEquals(0x99, device.getProtocol().getEscapeFlag());
	}

	@Test
	public void testBuildLightConfig() throws Exception {
		final ConfigGroup cfgGrp = new ConfigGroup();
		cfgGrp.lines.add(new ConfigLine("name		left5", 1));
		cfgGrp.lines.add(new ConfigLine("color		red     arduino 0 .8", 2));
		cfgGrp.lines.add(new ConfigLine("color		green   arduino 1", 3));
		cfgGrp.lines.add(new ConfigLine("color		blue    arduino 2", 4));

		Collection<ConfigGroup> deviceLines = (Collection<ConfigGroup>) Whitebox
				.getInternalState(configReader, "lightLines");
		deviceLines.add(cfgGrp);

		final List<Device> devices = new ArrayList<Device>();
		Device mock = mock(Device.class);
		when(mock.getNrChannels()).thenReturn(3);
		when(mock.getName()).thenReturn("arduino");
		devices.add(mock);
		
		final List<ColorConfig> colors = new ArrayList<ColorConfig>();
		colors.add(new ColorConfig("red"));
		colors.add(new ColorConfig("green"));
		colors.add(new ColorConfig("blue"));
		List<LightConfig> buildLightConfig = this.testable.buildLightConfig(
				devices, colors);

		LightConfig lightConfig = buildLightConfig.get(0);

		assertEquals(0.8f, lightConfig.getAdjust(0), 0);
	}

}
