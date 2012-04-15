package org.boblight4j.server.config;

import java.util.Vector;

public class ConfigGroup {

	public Vector<ConfigLine> lines = new Vector<ConfigLine>();

	@Override
	public String toString() {
		return "ConfigGroup [lines=" + this.lines + "]";
	}

}
