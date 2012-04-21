package org.boblight4j.client;

import java.util.ArrayList;
import java.util.List;

import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * Base class for all flag managers (program argument handlers).
 * 
 * @author agebauer
 * 
 * @param <T>
 *            the argument bean
 */
public abstract class AbstractFlagManager<T extends CommandLineArgs> implements
		FlagManager {

	private final T args;

	public AbstractFlagManager() {
		this.args = getArgBean();
	}

	@Override
	public final String getAddress() {
		return this.args.getServer().getAddress();
	}

	@Override
	public final int getPort() {
		return this.args.getServer().getPort();
	}

	@Override
	public final int getPriority() {
		return this.args.getPriority();
	}

	@Override
	public final boolean isPrintHelp() {
		return this.args.isPrintHelp();
	}

	@Override
	public final boolean isPrintOptions() {
		return this.args.isPrintBoblightOptions();
	}

	public final boolean isSync() {
		return this.args.isSync();
	}

	public void setSync(final boolean b) {
		this.args.setSync(b);
	}

	/**
	 * Parses the boblight options.
	 * 
	 * @param client
	 *            the client
	 * @throws BoblightException
	 */
	public final void parseBoblightOptions(final Client client)
			throws BoblightException {

		for (int i = 0; i < this.args.getOptions().size(); i++) {
			String option = this.args.getOptions().get(i);
			String lightname;
			String optionname;
			String optionvalue;
			int lightnr = -1;

			// check if we have a light name, otherwise we use all lights
			final int indexOfColon = option.indexOf(':');
			if (indexOfColon != -1) {
				lightname = option.substring(0, indexOfColon);
				// check if : isn't the last char in the string
				if (indexOfColon == option.length() - 1) {
					throw new BoblightException("wrong option \"" + option
							+ "\", syntax is [light:]option=value");
				}
				// shave off the light name
				option = option.substring(indexOfColon + 1);

				// check which light this is
				boolean lightfound = false;
				for (int j = 0; j < client.getNrLights(); j++) {
					if (lightname.equals(client.getLightName(j))) {
						lightfound = true;
						lightnr = j;
						break;
					}
				}
				if (!lightfound) {
					throw new BoblightException("light \"" + lightname
							+ "\" used in option \""
							+ this.args.getOptions().get(i)
							+ "\" doesn't exist");
				}
			}

			// check if '=' exists and it's not at the end of the string
			if (option.indexOf('=') == -1
					|| indexOfColon == option.length() - 1) {
				throw new BoblightException("wrong option \"" + option
						+ "\", syntax is [light:]option=value");
			}

			// option name is everything before = (already shaved off the
			// light name here)
			optionname = option.substring(0, option.indexOf('='));
			// value is everything after =
			optionvalue = option.substring(option.indexOf('=') + 1);

			// client wants syntax without =
			option = optionname + " " + optionvalue;

			// bitch if we can't set this option
			client.setOption(lightnr, option);
		}
	}

	// boblight options

	/**
	 * Parses the flags for this flag manager implementation.
	 * 
	 * @param args
	 *            the program arguments
	 * @return
	 */
	@Override
	public T parseFlags(final String[] args)
			throws BoblightConfigurationException {

		final CmdLineParser parser = new CmdLineParser(this.args);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e1) {
			throw new BoblightConfigurationException(
					"Error parsing program arguments.", e1);
		}

		if (this.args.isPrintBoblightOptions()) {
			return null;
		}

		// pass our argument to a derived class
		this.parseFlagsExtended(this.args);

		return this.args;
	}

	/**
	 * Returns the arguments bean which is used to hold infromation from the
	 * command line. Override this method to use a sub-classed
	 * {@link CommandLineArgs} bean.
	 * 
	 * @return the arguments bean (a subclass of {@link CommandLineArgs})
	 */
	@SuppressWarnings("unchecked")
	protected T getArgBean() {
		return (T) new CommandLineArgs();
	}

	/**
	 * Gets called from parseFlags, for derived classes.
	 * 
	 * @param args
	 *            all program arguments
	 * @throws BoblightConfigurationException
	 */
	protected void parseFlagsExtended(final T cmdLineArgs)
			throws BoblightConfigurationException {
	}

	/**
	 * Prints all available options to stdout.
	 */
	@Override
	public void printOptions() {
		final int nroptions = BoblightOptions.getNrOptions();

		for (int i = 0; i < nroptions; i++) {
			System.out.println(BoblightOptions.getOptionDescription(i));
		}
	}

	/**
	 * Prints the help message.
	 */
	@Override
	public final void printHelpMessage() {
		System.out.println("Usage: boblight-[type] [OPTION]");
		System.out.println("");
		System.out.println("  [OPTION]:");
		System.out.println("");

		final CmdLineParser parser = new CmdLineParser(getArgBean());
		parser.printUsage(System.out);
	}

}
