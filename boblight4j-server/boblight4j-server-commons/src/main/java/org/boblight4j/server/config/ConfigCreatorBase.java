package org.boblight4j.server.config;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.boblight4j.device.builder.DeviceBuilder;
import org.boblight4j.device.builder.DeviceBuilderFactory;
import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightException;
import org.boblight4j.exception.BoblightParseException;
import org.boblight4j.server.Channel;
import org.boblight4j.server.ClientsHandler;
import org.boblight4j.utils.MBeanUtils;
import org.boblight4j.utils.Misc;
import org.boblight4j.utils.Pointer;
import org.boblight4j.utils.StdIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ConfigCreatorBase implements ConfigCreator {

    private static final Logger LOG = LoggerFactory
	    .getLogger(ConfigCreatorBase.class);
    private final ConfigReader configReader;

    public ConfigCreatorBase(ConfigReader configReader) {
	this.configReader = configReader;
    }

    @Override
    public Config loadConfig(ClientsHandler<?> clients, Config config) throws BoblightException {
	LOG.info("building config");
	try {
	    this.configReader.loadConfig();
	} catch (FileNotFoundException e1) {
	    throw new BoblightConfigurationException("Configuration file could not be read.", e1);
	}

	List<Device> devices = new ArrayList<Device>();
	config.setDevices(devices);
	List<LightConfig> lights = new ArrayList<LightConfig>();
	config.setLights(lights);

	try {
	    this.buildClientsHandlerConfig(clients);
	} catch (final Exception e) {
	    // wrap exception
	    throw new BoblightException(e);
	}

	final List<ColorConfig> colors = this.buildColorConfig();

	final List<Device> tmpDevices = this.buildDeviceConfig(clients);
	final Iterator<Device> iterator = tmpDevices.iterator();
	while (iterator.hasNext()) {
	    devices.add(iterator.next());
	}

	final List<LightConfig> tmpLights = this.buildLightConfig(tmpDevices,
		colors);
	for (final LightConfig light : tmpLights) {
	    lights.add(light);

	    MBeanUtils.registerBean("org.boblight.server.config:type=Light ["
		    + light.getName() + "]", new LightConfigAccessor(light));

	}

	LOG.info("built config successfully");
	return null;
    }

    public List<LightConfig> buildLightConfig(List<Device> devices,
	    List<ColorConfig> colors) throws BoblightException {
	final List<LightConfig> lights = new ArrayList<LightConfig>();
	// CLight globallight = new CLight(); // default values

	for (int i = 0; i < this.configReader.getLightLines().size(); i++) {
	    ConfigGroup configGroup = this.configReader.getLightLines().get(i);

	    final String lightName = this.getLightName(configGroup.lines, i);
	    final LightConfig light = new LightConfig(lightName);

	    this.setLightScanRange(light, configGroup.lines);

	    // check the colors on a light
	    for (int j = 0; j < configGroup.lines.size(); j++) {
		final Pointer<String> linePtr = Pointer.of(configGroup.lines
			.get(j).line);

		final String key = Misc.getWord(linePtr);
		if (!key.equals("color")) {
		    continue;
		}

		// we already checked these in the syntax check
		final String colorname = Misc.getWord(linePtr);
		final String devicename = Misc.getWord(linePtr);
		final String devicechannel = Misc.getWord(linePtr);

		boolean colorfound = false;
		for (int k = 0; k < colors.size(); k++) {
		    if (colors.get(k).getName().equals(colorname)) {
			colorfound = true;
			light.addColor(colors.get(k));
			break;
		    }
		}
		if (!colorfound) // this color doesn't exist
		{
		    throw new BoblightException(String.format(
			    "%s line %d: no color with name %s",
			    this.configReader.getFileName(),
			    configGroup.lines.get(j).linenr, colorname));
		}

		final int ichannel = Integer.valueOf(devicechannel);

		// loop through the devices, check if one with this name exists
		// and if the channel on it exists
		boolean devicefound = false;
		for (int k = 0; k < devices.size(); k++) {
		    final Device cDevice = devices.get(k);
		    if (cDevice.getName().equals(devicename)) {
			if (ichannel > cDevice.getNrChannels()) {
			    final String msg = String
				    .format("%s line %d: channel %d wanted but device %s has %d channels",
					    this.configReader.getFileName(),
					    configGroup.lines.get(j).linenr,
					    ichannel, cDevice.getName(),
					    cDevice.getNrChannels());
			    throw new BoblightException(msg);
			}
			devicefound = true;
			final Channel chnl = new Channel(
				light.getNrColors() - 1, i, light.getName());
			cDevice.addChannel(chnl);
			break;
		    }
		}
		if (!devicefound) {
		    throw new BoblightException(String.format(
			    "%s line %d: no device with name %s",
			    this.configReader.getFileName(),
			    configGroup.lines.get(j).linenr, devicename));
		}
	    }
	    lights.add(light);
	}
	return lights;
    }

    private String getLightName(final List<ConfigLine> lines, final int lightnr)
	    throws BoblightParseException, BoblightConfigurationException {
	final Pointer<String> line = new Pointer<String>();
	final int linenr = this.getLineWithKey("name", lines, line);
	if (linenr == -1) {
	    throw new BoblightConfigurationException(String.format(
		    "%s: light %d has no name",
		    this.configReader.getFileName(), lightnr + 1));
	}
	return Misc.getWord(line);
    }

    private void setLightScanRange(final LightConfig light,
	    final List<ConfigLine> lines) throws BoblightParseException {
	// hscan and vdscan are optional
	final Pointer<String> line = new Pointer<String>();
	int linenr = this.getLineWithKey("hscan", lines, line);
	if (linenr != -1) {

	    final Object[] sscanf = StdIO.sscanf(line.get(), "%f %f");
	    light.setHscan(new float[] { (Float) sscanf[0], (Float) sscanf[1] });

	}

	linenr = this.getLineWithKey("vscan", lines, line);
	if (linenr != -1) {
	    final Object[] sscanf = StdIO.sscanf(line.get(), "%f %f");
	    light.setVscan(new float[] { (Float) sscanf[0], (Float) sscanf[1] });
	}
    }

    private int getLineWithKey(final String key, final List<ConfigLine> lines,
	    final Pointer<String> line) throws BoblightParseException {
	for (int i = 0; i < lines.size(); i++) {
	    line.assign(lines.get(i).line);
	    final String word = Misc.getWord(line);
	    if (word.equals(key)) {
		return lines.get(i).linenr;
	    }
	}
	return -1;
    }

    List<Device> buildDeviceConfig(ClientsHandler<?> clients)
	    throws BoblightException {
	final List<Device> devices = new ArrayList<Device>();

	for (int i = 0; i < this.configReader.getDeviceLines().size(); i++) {
	    final Pointer<String> line = new Pointer<String>();
	    final int linenr = this.getLineWithKey("type", this.configReader
		    .getDeviceLines().get(i).lines, line);

	    final String type = Misc.getWord(line);

	    DeviceBuilder devBldr = DeviceBuilderFactory.createBuilder(type,
		    this.configReader.getDeviceLines(), linenr,
		    this.configReader.getFileName());
	    devices.add(devBldr.build(i, clients, type));

	}
	return devices;
    }

    List<ColorConfig> buildColorConfig() throws BoblightException {

	final List<ColorConfig> colors = new ArrayList<ColorConfig>();
	for (int i = 0; i < this.configReader.getColorLines().size(); i++) {
	    final ColorConfig color = new ColorConfig();

	    for (int j = 0; j < this.configReader.getColorLines().get(i).lines
		    .size(); j++) {
		final Pointer<String> linePtr = new Pointer<String>(
			this.configReader.getColorLines().get(i).lines.get(j).line);

		final String key = Misc.getWord(linePtr);
		final String value = Misc.getWord(linePtr);

		if (key.equals("name")) {
		    color.setName(value);
		} else if (key.equals("rgb")) {
		    final int irgb = Integer.valueOf(value, 16);
		    final float frgb[] = new float[3];

		    for (int k = 0; k < 3; k++) {
			frgb[k] = (float) ((irgb >> (2 - k) * 8 & 0xFF) / 255.0);
		    }
		    color.setRgb(frgb);
		} else if (key.equals("gamma")) {
		    color.setGamma(Float.valueOf(value));
		} else if (key.equals("adjust")) {
		    color.setAdjust(Float.valueOf(value));
		} else if (key.equals("blacklevel")) {
		    color.setBlacklevel(Float.valueOf(value));
		}
	    }

	    // we need at least a name for a color
	    if (color.getName() == null || color.getName().isEmpty()) {
		throw new BoblightException(String.format(
			"%s: color %d has no name",
			this.configReader.getFileName(), i + 1));
	    }
	    colors.add(color);
	}
	return colors;
    }

    protected abstract void buildClientsHandlerConfig(ClientsHandler<?> clients)
	    throws Exception;

}
