package org.boblight4j.server.config;

import java.util.ArrayList;
import java.util.List;

public class ConfigAccessor implements ConfigAccessorMBean {

	private final Config config;

	public ConfigAccessor(final Config config) {
		this.config = config;
	}

	@Override
	public String[] getGlobalConfigLines() {
		final List<String> lines = new ArrayList<String>();
		for (final ConfigLine line : this.config.getGlobalConfigLines()) {
			lines.add(line.line);
		}

		return lines.toArray(new String[lines.size()]);
	}

}
