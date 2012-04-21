package org.boblight4j.device.builder;

import java.util.List;

import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.server.ClientsHandler;
import org.boblight4j.server.config.ConfigGroup;
import org.boblight4j.server.config.Device;

public class PopenBuilder extends AbstractDeviceBuilder {

	public PopenBuilder(final List<ConfigGroup> devicelines,
			final String filename) {
		super(devicelines, filename);
	}

	@Override
	public Device createDevice(ClientsHandler<?> clientsHandler, int devicenr,
			String type, List<ConfigGroup> deviceLines)
			throws BoblightConfigurationException {
		throw new BoblightConfigurationException("Not implemented yet.");
	}

}
