package org.boblight4j.server.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightException;
import org.boblight4j.exception.BoblightParseException;
import org.boblight4j.utils.Misc;
import org.boblight4j.utils.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlainTextConfigFileReader implements ConfigReader {

	private static final Logger LOG = LoggerFactory
			.getLogger(PlainTextConfigFileReader.class);

	private final File configFile;

	public List<ConfigLine> globalConfigLines = new ArrayList<ConfigLine>();

	public List<ConfigGroup> colorLines = new ArrayList<ConfigGroup>();
	public List<ConfigGroup> deviceLines = new ArrayList<ConfigGroup>();
	public List<ConfigGroup> lightLines = new ArrayList<ConfigGroup>();
	public String fileName;

	public PlainTextConfigFileReader(File configFile) {
		this.configFile = configFile;
		this.fileName = configFile.getAbsolutePath();
	}

	@Override
	public void loadConfig() throws BoblightException, FileNotFoundException {
		int linenr = 0;
		int currentsection = SECTNOTHING;

		LOG.info(String.format("opening %s", configFile));

		// try to open the config file
		if (!configFile.canRead()) {
			throw new BoblightException(String.format(
					"%s: kann nicht gelesen werden", configFile));
		}

		// read lines from the config file and store them in the appropriate
		// sections
		final FileReader fr = new FileReader(configFile);
		final LineNumberReader lnr = new LineNumberReader(fr);
		String line = null;
		try {
			while ((line = lnr.readLine()) != null) {
				linenr++;
				final Pointer<String> buffer = new Pointer<String>(line);
				// if the line doesn't have a word it's not important
				String key = null;
				try {
					key = Misc.getWord(buffer);
				} catch (final BoblightParseException e) {
					continue;
				}

				// ignore comments
				if (key.charAt(0) == '#') {
					continue;
				}

				// check if we entered a section
				if (key.equals("[global]")) {
					currentsection = SECTGLOBAL;
					continue;
				} else if (key.equals("[device]")) {
					currentsection = SECTDEVICE;
					this.deviceLines.add(new ConfigGroup());
					continue;
				} else if (key.equals("[color]")) {
					currentsection = SECTCOLOR;
					this.colorLines.add(new ConfigGroup());
					continue;
				} else if (key.equals("[light]")) {
					currentsection = SECTLIGHT;
					this.lightLines.add(new ConfigGroup());
					continue;
				}

				// we're not in a section
				if (currentsection == SECTNOTHING) {
					continue;
				}

				final ConfigLine configline = new ConfigLine(line, linenr);

				// store the config line in the appropriate section
				if (currentsection == SECTGLOBAL) {

					try {
						// every line here needs to have another word
						Misc.getWord(buffer);
					} catch (final BoblightParseException e) {
						throw new BoblightConfigurationException(String.format(
								"%s line %d: no value for key %s",
								this.fileName, linenr, key));
					}

					this.globalConfigLines.add(configline);
				} else if (currentsection == SECTDEVICE) {
					this.deviceLines.get(this.deviceLines.size() - 1).lines
							.add(configline);
				} else if (currentsection == SECTCOLOR) {
					this.colorLines.get(this.colorLines.size() - 1).lines
							.add(configline);
				} else if (currentsection == SECTLIGHT) {
					this.lightLines.get(this.lightLines.size() - 1).lines
							.add(configline);
				}
			}
		} catch (IOException e) {
			throw new BoblightConfigurationException("Unable to read file.", e);
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					LOG.error("Error closing filereader", e);
				}
			}
			if (lnr != null) {
				try {
					lnr.close();
				} catch (IOException e) {
					LOG.error("Error closing linenumberreader", e);
				}
			}
		}

	}

	@Override
	public List<ConfigGroup> getLightLines() {
		return this.lightLines;
	}

	@Override
	public List<ConfigLine> getGlobalConfigLines() {
		return this.globalConfigLines;
	}

	@Override
	public String getFileName() {
		return this.fileName;
	}

	@Override
	public List<ConfigGroup> getColorLines() {
		return this.colorLines;
	}

	@Override
	public List<ConfigGroup> getDeviceLines() {
		return this.deviceLines;
	}

}
