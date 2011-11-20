package org.boblight4j.device.builder;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.boblight4j.device.Device;
import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightParseException;
import org.boblight4j.server.ClientsHandler;
import org.boblight4j.server.config.ConfigGroup;
import org.boblight4j.server.config.ConfigLine;
import org.boblight4j.utils.BooleanParser;
import org.boblight4j.utils.Misc;
import org.boblight4j.utils.Pointer;

public abstract class DeviceBuilder {

	public static final int ATMO = 2;
	public static final int DIODER = 6;
	public static final int KARATE = 7;
	public static final int LTBL = 4;
	public static final int MOMO = 1;
	// device types
	public static final int NOTHING = 0;
	public static final int POPEN = 3;

	public static final int SOUND = 5;

	private static final Logger LOG = Logger.getLogger(DeviceBuilder.class);

	protected final List<ConfigGroup> deviceLines;

	private final String fileName;

	public DeviceBuilder(final List<ConfigGroup> devicelines,
			final String filename) {
		this.deviceLines = devicelines;
		this.fileName = filename;
	}

	public abstract Device build(int i, ClientsHandler clients, String type)
			throws BoblightConfigurationException;

	protected int getLineWithKey(final String key,
			final List<ConfigLine> lines, final Pointer<String> line) {
		for (int i = 0; i < lines.size(); i++)
		{
			line.assign(lines.get(i).line);
			try
			{
				final String word = Misc.getWord(line);
				if (word.equals(key))
				{
					return lines.get(i).linenr;
				}
			}
			catch (final BoblightParseException e)
			{
			}
		}
		line.assign(null);
		return -1;
	}

	void setDeviceAllowSync(final Device device, final int devicenr)
			throws BoblightConfigurationException {
		final Pointer<String> line = new Pointer<String>();
		final int linenr = this.getLineWithKey("allowsync",
				this.deviceLines.get(devicenr).lines, line);
		if (linenr == -1)
		{
			return;
		}

		String strvalue;
		try
		{
			strvalue = Misc.getWord(line);
		}
		catch (final BoblightParseException e)
		{
			throw new BoblightConfigurationException(
					"Unable to parse value for config key 'allowsync'", e);
		}

		final boolean allowsync = Boolean.valueOf(strvalue);
		device.setAllowSync(allowsync);
	}

	void setDeviceChannels(final Device device, final int devicenr)
			throws BoblightConfigurationException {
		final Pointer<String> line = new Pointer<String>();
		final int linenr = this.getLineWithKey("channels",
				this.deviceLines.get(devicenr).lines, line);
		if (linenr == -1)
		{
			throw new BoblightConfigurationException(String.format(
					"%s: device %s has no channels", this.fileName,
					device.getName()));
		}

		try
		{
			final String strvalue = Misc.getWord(line);
			final int nrchannels = Integer.valueOf(strvalue);
			device.setNrChannels(nrchannels);
		}
		catch (final BoblightParseException e)
		{
			throw new BoblightConfigurationException(
					"Unable to parse value for config key channels", e);
		}

	}

	void setDeviceDebug(final Device device, final int devicenr)
			throws BoblightConfigurationException {
		final Pointer<String> line = new Pointer<String>();
		final int linenr = this.getLineWithKey("debug",
				this.deviceLines.get(devicenr).lines, line);
		if (linenr == -1)
		{
			return;
		}

		String strvalue;
		try
		{
			strvalue = Misc.getWord(line);
			final boolean value = BooleanParser.parse(strvalue);
			device.setDebug(value);
		}
		catch (final BoblightParseException e)
		{
			throw new BoblightConfigurationException(
					"Unable to parse value for config key 'debug'", e);
		}

	}

	// #ifdef HAVE_LIBPORTAUDIO
	// boolean SetDevicePeriod(CDeviceSound* device, int devicenr)
	// {
	// Pointer<String> line = new Pointer<String>(), strvalue = new
	// Pointer<String>();
	// int linenr = GetLineWithKey("period", m_devicelines.get(devicenr).lines,
	// line);
	// if (linenr == -1)
	// {
	// LogError("%s: device %s has no period", m_filename.c_str(),
	// device.GetName().c_str());
	// return false;
	// }
	// GetWord(line, strvalue);
	//
	// int period;
	// StrToInt(strvalue, period);
	// device.SetPeriod(period);
	//
	// return true;
	// }
	//
	// void SetDeviceLatency(CDeviceSound device, int devicenr)
	// {
	// Pointer<String> line = new Pointer<String>(), strvalue = new
	// Pointer<String>();
	// int linenr = GetLineWithKey("latency", m_devicelines.get(devicenr).lines,
	// line);
	// if (linenr == -1)
	// return;
	//
	// GetWord(line, strvalue);
	//
	// double latency;
	// StrToFloat(strvalue, latency);
	// device.SetLatency(latency);
	// }
	// #endif

	void setDeviceDelayAfterOpen(final Device device, final int devicenr)
			throws BoblightConfigurationException {
		final Pointer<String> line = new Pointer<String>();
		final int linenr = this.getLineWithKey("delayafteropen",
				this.deviceLines.get(devicenr).lines, line);
		if (linenr == -1)
		{
			return;
		}

		String strvalue;
		try
		{
			strvalue = Misc.getWord(line);
		}
		catch (final BoblightParseException e)
		{
			throw new BoblightConfigurationException(
					"Unable to parse value for config key 'delayafteropen'", e);
		}

		final int delayafteropen = Integer.valueOf(strvalue) / 1000;
		device.setDelayAfterOpen(delayafteropen);
	}

	void setDeviceInterval(final Device device, final int devicenr)
			throws BoblightConfigurationException {
		final Pointer<String> line = new Pointer<String>();
		final int linenr = this.getLineWithKey("interval",
				this.deviceLines.get(devicenr).lines, line);
		if (linenr == -1)
		{
			throw new BoblightConfigurationException(String.format(
					"%s: device %s has no interval", this.fileName,
					device.getName()));
		}
		String strvalue;
		try
		{
			strvalue = Misc.getWord(line);
		}
		catch (final BoblightParseException e)
		{
			throw new BoblightConfigurationException(
					"Unable to parse value for config key 'interval'", e);
		}

		final int interval = Integer.valueOf(strvalue);
		device.setInterval(interval);

	}

	protected void setDeviceName(final Device device, final int devicenr)
			throws BoblightConfigurationException {
		final Pointer<String> line = new Pointer<String>();
		int linenr;
		try
		{
			linenr = this.getLineWithKey("name",
					this.deviceLines.get(devicenr).lines, line);
			if (linenr == -1)
			{
				throw new BoblightConfigurationException(String.format(
						"%s: device %d has no name", this.fileName,
						devicenr + 1));
			}
			String strvalue;
			strvalue = Misc.getWord(line);
			device.setName(strvalue);
		}
		catch (final BoblightParseException e)
		{
			throw new BoblightConfigurationException(
					"Unable to parse value for config key name", e);
		}
	}

	boolean setDeviceOutput(final Device device, final int devicenr)
			throws BoblightConfigurationException {
		final Pointer<String> line = new Pointer<String>();
		try
		{
			final int linenr = this.getLineWithKey("output",
					this.deviceLines.get(devicenr).lines, line);
			if (linenr == -1)
			{
				throw new BoblightConfigurationException(String.format(
						"%s: device %s has no output", this.fileName,
						device.getName()));
			}
			final String strvalue = Misc.getWord(line);
			device.setOutput(strvalue + line.get());
		}
		catch (final BoblightParseException e)
		{
			throw new BoblightConfigurationException(
					"Unable to parse value for config key output", e);
		}

		return true;
	}

	void setDeviceRate(final Device device, final int devicenr)
			throws BoblightConfigurationException {
		final Pointer<String> line = new Pointer<String>();
		final int linenr = this.getLineWithKey("rate",
				this.deviceLines.get(devicenr).lines, line);
		if (linenr == -1)
		{
			throw new BoblightConfigurationException(String.format(
					"%s: device %s has no rate", this.fileName,
					device.getName()));
		}
		String strvalue;
		try
		{
			strvalue = Misc.getWord(line);
		}
		catch (final BoblightParseException e)
		{
			throw new BoblightConfigurationException(
					"Unable to parse value for config key 'rate'", e);
		}

		final int rate = Integer.valueOf(strvalue);
		device.setRate(rate);

	}
}
