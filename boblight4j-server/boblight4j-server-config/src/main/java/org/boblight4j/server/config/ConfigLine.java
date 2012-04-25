package org.boblight4j.server.config;

public class ConfigLine {

	public final String line;
	public final int linenr;

	public ConfigLine(final String line, final int linenr) {
		this.line = line;
		this.linenr = linenr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((line == null) ? 0 : line.hashCode());
		result = prime * result + linenr;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConfigLine other = (ConfigLine) obj;
		if (line == null) {
			if (other.line != null)
				return false;
		} else if (!line.equals(other.line))
			return false;
		if (linenr != other.linenr)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ConfigLine [#" + this.linenr + " '" + this.line + "']";
	}

}
