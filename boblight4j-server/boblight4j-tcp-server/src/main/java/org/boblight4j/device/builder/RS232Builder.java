package org.boblight4j.device.builder;

import java.util.List;

import org.boblight4j.device.AbstractDevice.DeviceType;
import org.boblight4j.device.DeviceRS232;
import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightParseException;
import org.boblight4j.server.ClientsHandler;
import org.boblight4j.server.config.ConfigGroup;
import org.boblight4j.utils.Misc;
import org.boblight4j.utils.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger LOG = LoggerFactory
			.getLogger(RS232Builder.class);

	public RS232Builder(final List<ConfigGroup> devicelines,
			final String filename) {
		super(devicelines, filename);
	}

	@Override
	public DeviceRS232 createDevice(ClientsHandler<?> clientsHandler,
			int devicenr, String type, List<ConfigGroup> deviceLines)
			throws BoblightConfigurationException {

		DeviceRS232 deviceRS232 = new DeviceRS232(clientsHandler);
		ConfigGroup configGroup = deviceLines.get(devicenr);

		this.setDeviceBits(deviceRS232, configGroup);

		if (type.equals("momo")) {
			deviceRS232.setType(DeviceType.MOMO);
			this.setDevicePrefix(deviceRS232, configGroup);
			this.setDevicePostfix(deviceRS232, configGroup);
			this.setDeviceEscapeFlag(deviceRS232, configGroup);
			deviceRS232.getProtocol().checkValid();
		} else if (type.equals("atmo")) {
			deviceRS232.setType(DeviceType.ATMO);
		} else if (type.equals("karate")) {
			deviceRS232.setType(DeviceType.KARATE);
		}

		return deviceRS232;
	}

	void setDeviceBits(final DeviceRS232 device, final ConfigGroup configGroup)
			throws BoblightConfigurationException {

		final Pointer<String> line = new Pointer<String>();
		final int linenr = this.getLineWithKey("bits", configGroup.lines, line);
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
			final ConfigGroup configGroup)
			throws BoblightConfigurationException {
		final Pointer<String> line = new Pointer<String>();
		int postfix = 0;
		final int linenr = this.getLineWithKey("escape", configGroup.lines,
				line);
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

	private void setDevicePostfix(final DeviceRS232 device,
			final ConfigGroup configGroup)
			throws BoblightConfigurationException {
		final Pointer<String> line = new Pointer<String>();
		int postfix = 0;
		final int linenr = this.getLineWithKey("postfix", configGroup.lines,
				line);
		if (linenr == -1) {
			throw new BoblightConfigurationException(
					"No postfix given for device " + device.getName());
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

	void setDevicePrefix(final DeviceRS232 device, ConfigGroup configGroup)
			throws BoblightConfigurationException {
		LOG.debug("Setting deviceprefix of " + device.getName());
		final Pointer<String> line = new Pointer<String>();
		int prefix = 0;
		final int linenr = this.getLineWithKey("prefix", configGroup.lines,
				line);
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
