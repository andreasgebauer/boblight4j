package org.boblight4j.device.builder;

import java.util.List;

import org.apache.log4j.Logger;
import org.boblight4j.device.AbstractDevice;
import org.boblight4j.device.AbstractDevice.DeviceType;
import org.boblight4j.device.DeviceRS232;
import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightParseException;
import org.boblight4j.server.ClientsHandler;
import org.boblight4j.server.config.ConfigGroup;
import org.boblight4j.utils.Misc;
import org.boblight4j.utils.Pointer;

/**
 * The class RS232Builder provides an implementation to build accessors for
 * RS232 devices.<br>
 * <br>
 * Currently supported device types are<br>
 * <ul>
 * <li>momo</li>
 * </ul>
 * 
 * @author agebauer
 * 
 */
public class RS232Builder extends AbstractDeviceBuilder {

	private static final Logger LOG = Logger.getLogger(RS232Builder.class);

	public RS232Builder(final List<ConfigGroup> devicelines,
			final String filename) {
		super(devicelines, filename);
	}

	@Override
	public AbstractDevice build(final int devicenr,
			final ClientsHandler clients, final String type)
			throws BoblightConfigurationException {
		final DeviceRS232 device = new DeviceRS232(clients);

		this.setDeviceName(device, devicenr);
		this.setDeviceOutput(device, devicenr);
		this.setDeviceChannels(device, devicenr);
		this.setDeviceRate(device, devicenr);
		this.setDeviceInterval(device, devicenr);

		// optional
		this.setDeviceAllowSync(device, devicenr);
		this.setDeviceDebug(device, devicenr);
		this.setDeviceBits(device, devicenr);
		this.setDeviceDelayAfterOpen(device, devicenr);

		if (type.equals("momo")) {
			device.setType(DeviceType.MOMO);
			this.setDevicePrefix(device, devicenr);
			this.setDevicePostfix(device, devicenr);
			this.setDeviceEscapeFlag(device, devicenr);
			device.getProtocol().checkValid();
		} else if (type.equals("atmo")) {
			device.setType(DeviceType.ATMO);
		} else if (type.equals("karate")) {
			device.setType(DeviceType.KARATE);
		}
		return device;
	}

	void setDeviceBits(final DeviceRS232 device, final int devicebits)
			throws BoblightConfigurationException {
		LOG.debug("Setting devicebits of " + device.getName() + " to "
				+ devicebits);

		final Pointer<String> line = new Pointer<String>();
		final int linenr = this.getLineWithKey("bits",
				this.deviceLines.get(devicebits).lines, line);
		if (linenr == -1) {
			return;
		}

		String strvalue;
		try {
			strvalue = Misc.getWord(line);
		} catch (final BoblightParseException e) {
			throw new BoblightConfigurationException(
					"Unable to parse value for config key 'bits'", e);
		}

		final int bits = Integer.valueOf(strvalue);
		device.setBits(bits);
	}

	private void setDeviceEscapeFlag(final DeviceRS232 device,
			final int devicenr) throws BoblightConfigurationException {
		final Pointer<String> line = new Pointer<String>();
		int postfix = 0;
		final int linenr = this.getLineWithKey("escape",
				this.deviceLines.get(devicenr).lines, line);
		if (linenr == -1) {
			return; // postfix is optional, so this is not an error
		}

		String strvalue;
		try {
			strvalue = Misc.getWord(line);
			postfix = Integer.valueOf(strvalue, 16);
		} catch (final BoblightParseException e) {
			throw new BoblightConfigurationException(
					"Unable to parse value for config key channels", e);
		}

		device.getProtocol().setEscapeFlag(postfix);
	}

	private void setDevicePostfix(final DeviceRS232 device, final int devicenr)
			throws BoblightConfigurationException {
		final Pointer<String> line = new Pointer<String>();
		int postfix = 0;
		final int linenr = this.getLineWithKey("postfix",
				this.deviceLines.get(devicenr).lines, line);
		if (linenr == -1) {
			throw new BoblightConfigurationException(
					"No postfix given for device " + devicenr);
		}

		String strvalue;
		try {
			strvalue = Misc.getWord(line);
		} catch (final BoblightParseException e) {
			throw new BoblightConfigurationException(
					"Unable to parse value for config key 'postfix'", e);
		}
		postfix = Integer.valueOf(strvalue, 16);
		device.getProtocol().setEndFlag(postfix);
	}

	void setDevicePrefix(final DeviceRS232 device, final int devicenr)
			throws BoblightConfigurationException {
		LOG.debug("Setting deviceprefix of " + device.getName());
		final Pointer<String> line = new Pointer<String>();
		int prefix = 0;
		final int linenr = this.getLineWithKey("prefix",
				this.deviceLines.get(devicenr).lines, line);
		if (linenr == -1) {
			return; // prefix is optional, so this is not an error
		}

		String strvalue;
		try {
			strvalue = Misc.getWord(line);
		} catch (final BoblightParseException e) {
			throw new BoblightConfigurationException(
					"Unable to parse value for config key 'prefix'", e);
		}
		prefix = Integer.valueOf(strvalue, 16);
		device.getProtocol().setStartFlag(prefix);
	}
}
