package org.boblight4j.device.builder;

import java.util.List;

import org.boblight4j.device.AbstractDevice.DeviceType;
import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.server.config.ConfigGroup;

public class DeviceBuilderFactory {

	private DeviceBuilderFactory() {
	}

	public static DeviceBuilder createBuilder(final String type,
			final List<ConfigGroup> deviceLines, final int linenr,
			final String fileName) throws BoblightConfigurationException {

		DeviceType devType = DeviceType.forName(type);

		switch (devType) {
		case POPEN:
			return new PopenBuilder(deviceLines, fileName);
		case ATMO:
		case MOMO:
		case KARATE:
			return new RS232Builder(deviceLines, fileName);
		case LTBL:
			return new LtblBuilder(deviceLines, fileName);
		case SOUND:
			return new SoundBuilder(deviceLines, fileName);
		case DIODER:
			return new DioderBuilder(deviceLines, fileName);
		}
		throw new BoblightConfigurationException(String.format(
				"%s line %d: unknown type %s", fileName, linenr, type));
	}
}
