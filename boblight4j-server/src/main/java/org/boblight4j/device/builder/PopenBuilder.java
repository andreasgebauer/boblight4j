package org.boblight4j.device.builder;

import java.util.List;

import org.boblight4j.device.Device;
import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.server.ClientsHandler;
import org.boblight4j.server.config.ConfigGroup;

public class PopenBuilder extends DeviceBuilder {

	public PopenBuilder(final List<ConfigGroup> devicelines,
			final String filename) {
		super(devicelines, filename);
	}

	@Override
	public Device build(final int i, final ClientsHandler clients,
			final String type) throws BoblightConfigurationException {
		throw new BoblightConfigurationException("Not implemented yet.");
	}

}
