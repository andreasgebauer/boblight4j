package org.boblight4j.server.config;

public class ConfigLine {

	public final String line;
	public final int linenr;

	public ConfigLine(final String line, final int linenr) {
		this.line = line;
		this.linenr = linenr;
	}

	@Override
	public String toString() {
		return "ConfigLine [#" + this.linenr + " " + this.line + "]";
	}

}
