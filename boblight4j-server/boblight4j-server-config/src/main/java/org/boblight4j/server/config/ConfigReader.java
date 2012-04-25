package org.boblight4j.server.config;

import java.io.FileNotFoundException;
import java.util.List;

import org.boblight4j.exception.BoblightException;

public interface ConfigReader {

	public static final int SECTCOLOR = 3;
	public static final int SECTDEVICE = 2;
	public static final int SECTGLOBAL = 1;
	public static final int SECTLIGHT = 4;
	public static final int SECTNOTHING = 0;

	void loadConfig() throws BoblightException, FileNotFoundException;

	List<ConfigGroup> getLightLines();

	List<ConfigLine> getGlobalConfigLines();

	String getFileName();

	List<ConfigGroup> getColorLines();

	List<ConfigGroup> getDeviceLines();

}
