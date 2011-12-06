package org.boblight4j.device.builder;

import java.util.List;

import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.server.config.ConfigGroup;

public class DeviceBuilderFactory {

	public static DeviceBuilder createBuilder(String type,
			List<ConfigGroup> deviceLines, int linenr, String fileName)
			throws BoblightConfigurationException {
		if (type.equals("popen"))
		{
			return new PopenBuilder(deviceLines, fileName);
		}
		else if (type.equals("momo") || type.equals("atmo")
				|| type.equals("karate"))
		{
			return new RS232Builder(deviceLines, fileName);
		}
		else if (type.equals("ltbl"))
		{
			return new LtblBuilder(deviceLines, fileName);
		}
		else if (type.equals("sound"))
		{
			return new SoundBuilder(deviceLines, fileName);
		}
		else if (type.equals("dioder"))
		{
			return new DioderBuilder(deviceLines, fileName);
		}
		else
		{
			final String msg = String.format("%s line %d: unknown type %s",
					fileName, linenr, type);
			throw new BoblightConfigurationException(msg);
		}

	}

}
