package org.boblight4j.server.config;

import java.util.ArrayList;
import java.util.List;

public class ConfigAccessor implements ConfigAccessorMBean {

	private final TcpServerConfigImpl config;

	public ConfigAccessor(final TcpServerConfigImpl config) {
		this.config = config;
	}

	@Override
	public String[] getGlobalConfigLines() {
		final List<String> lines = new ArrayList<String>();
		for (final ConfigLine line : this.config.globalConfigLines)
		{
			lines.add(line.line);
		}

		return lines.toArray(new String[] {});
	}

}
