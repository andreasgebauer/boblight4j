package org.boblight4j.server.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.boblight4j.device.Device;
import org.boblight4j.device.Light;
import org.boblight4j.device.builder.DeviceBuilder;
import org.boblight4j.device.builder.DeviceBuilderFactory;
import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightException;
import org.boblight4j.exception.BoblightParseException;
import org.boblight4j.server.ClientsHandler;
import org.boblight4j.utils.MBeanUtils;
import org.boblight4j.utils.Misc;
import org.boblight4j.utils.Pointer;
import org.boblight4j.utils.StdIO;

public class ConfigImpl implements Config {

	private static final Logger LOG = Logger.getLogger(ConfigImpl.class);

	private static final int SECTCOLOR = 3;
	private static final int SECTDEVICE = 2;
	private static final int SECTGLOBAL = 1;
	private static final int SECTLIGHT = 4;
	private static final int SECTNOTHING = 0;

	List<ConfigLine> globalConfigLines = new ArrayList<ConfigLine>();

	private List<ConfigGroup> colorLines = new ArrayList<ConfigGroup>();
	private List<ConfigGroup> deviceLines = new ArrayList<ConfigGroup>();
	private List<ConfigGroup> lightLines = new ArrayList<ConfigGroup>();
	private String fileName;

	/**
	 * Set up where to bind the listening socket. Configuration for this should
	 * already be valid here. Whe are checking if the configured interface is
	 * reachable.
	 * 
	 * @param clientsHandler
	 *            the clientsHandler interface
	 * @throws BoblightParseException
	 *             in case of
	 * @throws UnknownHostException
	 *             in case of host is not resolvable
	 */
	private void buildClientsHandlerConfig(final ClientsHandler clientsHandler)
			throws BoblightParseException, UnknownHostException {
		// empty string means bind to *
		InetAddress ifc = null;
		int port = 19333; // default port
		for (int i = 0; i < this.globalConfigLines.size(); i++)
		{
			final Pointer<String> line = new Pointer<String>(
					this.globalConfigLines.get(i).line);
			String word = Misc.getWord(line);
			if (word.equals("interface"))
			{
				ifc = InetAddress.getByName(Misc.getWord(line));
			}
			else if (word.equals("port"))
			{
				word = Misc.getWord(line);
				port = Integer.valueOf(word);
			}
		}
		clientsHandler.setInterface(ifc, port);
	}

	@Override
	public List<Color> buildColorConfig() throws BoblightException {
		final List<Color> colors = new ArrayList<Color>();
		for (int i = 0; i < this.colorLines.size(); i++)
		{
			final Color color = new Color();

			for (int j = 0; j < this.colorLines.get(i).lines.size(); j++)
			{
				final Pointer<String> linePtr = new Pointer<String>(
						this.colorLines.get(i).lines.get(j).line);

				final String key = Misc.getWord(linePtr);
				final String value = Misc.getWord(linePtr);

				if (key.equals("name"))
				{
					color.setName(value);
				}
				else if (key.equals("rgb"))
				{
					final int irgb = Integer.valueOf(value, 16);
					final float frgb[] = new float[3];

					for (int k = 0; k < 3; k++)
					{
						frgb[k] = (float) ((irgb >> (2 - k) * 8 & 0xFF) / 255.0);
					}
					color.setRgb(frgb);
				}
				else if (key.equals("gamma"))
				{
					color.setGamma(Float.valueOf(value));
				}
				else if (key.equals("adjust"))
				{
					color.setAdjust(Float.valueOf(value));
				}
				else if (key.equals("blacklevel"))
				{
					color.setBlacklevel(Float.valueOf(value));
				}
			}

			// we need at least a name for a color
			if (color.getName() == null || color.getName().isEmpty())
			{
				throw new BoblightException(String.format(
						"%s: color %d has no name", this.fileName, i + 1));
			}
			colors.add(color);
		}
		return colors;
	}

	@Override
	public void buildConfig(final ClientsHandler clients,
			final List<Device> devices, final List<Light> lights)
			throws BoblightException {
		LOG.info("building config");

		try
		{
			this.buildClientsHandlerConfig(clients);
		}
		catch (final UnknownHostException e)
		{
			// wrap exception
			throw new BoblightException(e);
		}

		final List<Color> colors = this.buildColorConfig();

		final List<Device> tmpDevices = this.buildDeviceConfig(clients);
		final Iterator<Device> iterator = tmpDevices.iterator();
		while (iterator.hasNext())
		{
			final Device next = iterator.next();
			devices.add(next);
		}

		final List<Light> tmpLights = this.buildLightConfig(tmpDevices, colors);
		for (final Light light : tmpLights)
		{
			lights.add(light);

			MBeanUtils.registerBean("org.boblight.server.config:type=Light ["
					+ light.getName() + "]", new LightAccessor(light));

		}

		LOG.info("built config successfully");
	}

	@Override
	public List<Device> buildDeviceConfig(final ClientsHandler clients)
			throws BoblightConfigurationException, BoblightParseException {
		final List<Device> devices = new ArrayList<Device>();

		for (int i = 0; i < this.deviceLines.size(); i++)
		{
			final Pointer<String> line = new Pointer<String>();
			final int linenr = this.getLineWithKey("type",
					this.deviceLines.get(i).lines, line);

			final String type = Misc.getWord(line);

			DeviceBuilder devBldr = DeviceBuilderFactory.createBuilder(type,
					this.deviceLines, linenr, this.fileName);
			devices.add(devBldr.build(i, clients, type));

		}
		return devices;
	}

	@Override
	public List<Light> buildLightConfig(final List<Device> devices,
			final List<Color> colors) throws BoblightException {
		final List<Light> lights = new ArrayList<Light>();
		// CLight globallight = new CLight(); // default values

		for (int i = 0; i < this.lightLines.size(); i++)
		{
			final Light light = new Light();

			this.setLightName(light, this.lightLines.get(i).lines, i);

			this.setLightScanRange(light, this.lightLines.get(i).lines);

			// check the colors on a light
			for (int j = 0; j < this.lightLines.get(i).lines.size(); j++)
			{
				final Pointer<String> linePtr = Pointer.of(this.lightLines
						.get(i).lines.get(j).line);

				final String key = Misc.getWord(linePtr);
				if (!key.equals("color"))
				{
					continue;
				}

				// we already checked these in the syntax check
				final String colorname = Misc.getWord(linePtr);
				final String devicename = Misc.getWord(linePtr);
				final String devicechannel = Misc.getWord(linePtr);

				boolean colorfound = false;
				for (int k = 0; k < colors.size(); k++)
				{
					if (colors.get(k).getName().equals(colorname))
					{
						colorfound = true;
						light.addColor(colors.get(k));
						break;
					}
				}
				if (!colorfound) // this color doesn't exist
				{
					throw new BoblightException(String.format(
							"%s line %d: no color with name %s", this.fileName,
							this.lightLines.get(i).lines.get(j).linenr,
							colorname));
				}

				final int ichannel = Integer.valueOf(devicechannel);

				// loop through the devices, check if one with this name exists
				// and if the channel on it exists
				boolean devicefound = false;
				for (int k = 0; k < devices.size(); k++)
				{
					final Device cDevice = devices.get(k);
					if (cDevice.getName().equals(devicename))
					{
						if (ichannel > cDevice.getNrChannels())
						{
							final String msg = String
									.format("%s line %d: channel %d wanted but device %s has %d channels",
											this.fileName,
											this.lightLines.get(i).lines.get(j).linenr,
											ichannel, cDevice.getName(),
											cDevice.getNrChannels());
							throw new BoblightException(msg);
						}
						devicefound = true;
						final Channel chnl = new Channel(
								light.getNrColors() - 1, i);
						cDevice.addChannel(chnl);
						break;
					}
				}
				if (!devicefound)
				{
					throw new BoblightException(String.format(
							"%s line %d: no device with name %s",
							this.fileName,
							this.lightLines.get(i).lines.get(j).linenr,
							devicename));
				}
			}
			lights.add(light);
		}
		return lights;

	}

	private boolean checkColorConfig() {
		return true;
	}

	@Override
	public boolean checkConfig() throws BoblightException {
		boolean valid = true;
		LOG.info("checking config lines");

		this.checkGlobalConfig();

		if (!this.checkDeviceConfig())
		{
			valid = false;
		}

		if (!this.checkColorConfig())
		{
			valid = false;
		}

		if (!this.checkLightConfig())
		{
			valid = false;
		}

		if (valid)
		{
			LOG.info("config lines valid");
		}

		return valid;
	}

	private boolean checkDeviceConfig() {
		return true;
	}

	private void checkGlobalConfig() throws BoblightException {
		boolean valid = true;

		for (int i = 0; i < this.globalConfigLines.size(); i++)
		{
			final Pointer<String> line = new Pointer<String>(
					this.globalConfigLines.get(i).line);
			String key = null;
			String value = null;

			// we already checked each line starts with one word
			key = Misc.getWord(line);

			try
			{
				// every line here needs to have another word
				value = Misc.getWord(line);
			}
			catch (final BoblightParseException e)
			{
				LOG.fatal(String.format("%s line %d: no value for key %s",
						this.fileName, this.globalConfigLines.get(i).linenr,
						key));
				valid = false;
				continue;

			}

			if (key.equals("interface"))
			{
				// not much to check here
				continue;
			}
			else if (key.equals("port"))// check tcp/ip port
			{
				int port = -1;
				final String msg = String.format(
						"%s line %d: wrong value %s for key %s", this.fileName,
						this.globalConfigLines.get(i).linenr, value, key);
				try
				{
					port = Integer.valueOf(value);
				}
				catch (final NumberFormatException e)
				{
					throw new BoblightException(msg, e);
				}
				if (port < 0 || port > 65535)
				{
					throw new BoblightException(msg);
				}
			}
			else
			// we don't know this one
			{
				throw new BoblightException(String.format(
						"%s line %d: unknown key %s", this.fileName,
						this.globalConfigLines.get(i).linenr, key));
			}
		}

		if (!valid)
		{
			throw new BoblightException(
					"Config not valid because of previous errors");
		}

	}

	private boolean checkLightConfig() {
		return true;
	}

	@Override
	public void clearConfig() {
		this.globalConfigLines.clear();
		this.deviceLines.clear();
		this.colorLines.clear();
		this.lightLines.clear();
	}

	private int getLineWithKey(final String key, final List<ConfigLine> lines,
			final Pointer<String> line) throws BoblightParseException {
		for (int i = 0; i < lines.size(); i++)
		{
			line.assign(lines.get(i).line);
			final String word = Misc.getWord(line);
			if (word.equals(key))
			{
				return lines.get(i).linenr;
			}
		}
		return -1;
	}

	@Override
	public void loadConfigFromFile(final String file)
			throws FileNotFoundException, BoblightException {
		int linenr = 0;
		int currentsection = SECTNOTHING;

		this.fileName = file;

		LOG.info(String.format("opening %s", file));

		final File configfile = new File(file);
		// try to open the config file
		if (!configfile.canRead())
		{
			throw new BoblightException(String.format("%s: %s", file, ""));
		}

		// read lines from the config file and store them in the appropriate
		// sections
		final FileReader fr = new FileReader(configfile);
		final LineNumberReader lnr = new LineNumberReader(fr);
		String line = null;
		try
		{
			while ((line = lnr.readLine()) != null)
			{
				linenr++;
				final Pointer<String> buffer = new Pointer<String>(line);
				// if the line doesn't have a word it's not important
				String key = null;
				try
				{
					key = Misc.getWord(buffer);
				}
				catch (final BoblightParseException e)
				{
					continue;
				}

				// ignore comments
				if (key.charAt(0) == '#')
				{
					continue;
				}

				// check if we entered a section
				if (key.equals("[global]"))
				{
					currentsection = SECTGLOBAL;
					continue;
				}
				else if (key.equals("[device]"))
				{
					currentsection = SECTDEVICE;
					this.deviceLines.add(new ConfigGroup());
					continue;
				}
				else if (key.equals("[color]"))
				{
					currentsection = SECTCOLOR;
					this.colorLines.add(new ConfigGroup());
					continue;
				}
				else if (key.equals("[light]"))
				{
					currentsection = SECTLIGHT;
					this.lightLines.add(new ConfigGroup());
					continue;
				}

				// we're not in a section
				if (currentsection == SECTNOTHING)
				{
					continue;
				}

				final ConfigLine configline = new ConfigLine(line, linenr);

				// store the config line in the appropriate section
				if (currentsection == SECTGLOBAL)
				{
					this.globalConfigLines.add(configline);
				}
				else if (currentsection == SECTDEVICE)
				{
					this.deviceLines.get(this.deviceLines.size() - 1).lines
							.add(configline);
				}
				else if (currentsection == SECTCOLOR)
				{
					this.colorLines.get(this.colorLines.size() - 1).lines
							.add(configline);
				}
				else if (currentsection == SECTLIGHT)
				{
					this.lightLines.get(this.lightLines.size() - 1).lines
							.add(configline);
				}
			}
		}
		catch (IOException e)
		{
			throw new BoblightConfigurationException("Unable to read file.", e);
		}
		// PrintConfig();
	}

	private void setLightName(final Light light, final List<ConfigLine> lines,
			final int lightnr) throws BoblightParseException,
			BoblightConfigurationException {
		final Pointer<String> line = new Pointer<String>();
		final int linenr = this.getLineWithKey("name", lines, line);
		if (linenr == -1)
		{
			throw new BoblightConfigurationException(String.format(
					"%s: light %d has no name", this.fileName, lightnr + 1));
		}

		light.setName(Misc.getWord(line));
	}

	private void setLightScanRange(final Light light,
			final List<ConfigLine> lines) throws BoblightParseException {
		// hscan and vdscan are optional
		final Pointer<String> line = new Pointer<String>();
		int linenr = this.getLineWithKey("hscan", lines, line);
		if (linenr != -1)
		{

			final Object[] sscanf = StdIO.sscanf(line.get(), "%f %f");
			light.setHscan(new float[] { (Float) sscanf[0], (Float) sscanf[1] });

		}

		linenr = this.getLineWithKey("vscan", lines, line);
		if (linenr != -1)
		{
			final Object[] sscanf = StdIO.sscanf(line.get(), "%f %f");
			light.setVscan(new float[] { (Float) sscanf[0], (Float) sscanf[1] });
		}
	}

}