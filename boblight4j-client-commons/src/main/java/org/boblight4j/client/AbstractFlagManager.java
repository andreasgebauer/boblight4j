package org.boblight4j.client;

import java.util.ArrayList;
import java.util.List;

import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightException;
import org.boblight4j.exception.BoblightRuntimeException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * Base class for all flag managers (program argument handlers).
 * 
 * @author agebauer
 * 
 */
public abstract class AbstractFlagManager<T extends CommandLineArgs> implements
		FlagManager {

	private String address;

	private final List<String> options = new ArrayList<String>();
	private int port;

	private boolean printBoblightOptions;
	private boolean printHelp;

	private int priority;
	private boolean sync;
	private CmdLineParser parser;

	public AbstractFlagManager() {
		this.port = -1; // -1 tells libboblight to use default port
		this.address = null; // NULL tells libboblight to use default address
		this.printHelp = false; // don't print helpmessage unless asked to
		this.printBoblightOptions = false; // same for libboblight options
		this.sync = false; // sync mode disabled by default
	}

	@Override
	public final String getAddress() {
		return this.address;
	}

	@Override
	public final int getPort() {
		return this.port;
	}

	@Override
	public final int getPriority() {
		return this.priority;
	}

	@Override
	public final boolean isPrintHelp() {
		return this.printHelp;
	}

	@Override
	public final boolean isPrintOptions() {
		return this.printBoblightOptions;
	}

	public final boolean isSync() {
		return this.sync;
	}

	/**
	 * Parses the boblight options.
	 * 
	 * @param client
	 *            the client
	 * @throws BoblightException
	 */
	public final void parseBoblightOptions(final ClientImpl client)
			throws BoblightException {

		final int nrlights = client.getNrLights();
		for (int i = 0; i < this.options.size(); i++)
		{
			String option = this.options.get(i);
			String lightname;
			String optionname;
			String optionvalue;
			int lightnr = -1;

			// check if we have a light name, otherwise we use all lights
			final int indexOfColon = option.indexOf(':');
			if (indexOfColon != -1)
			{
				lightname = option.substring(0, indexOfColon);
				// check if : isn't the last char in the string
				if (indexOfColon == option.length() - 1)
				{
					throw new BoblightException("wrong option \"" + option
							+ "\", syntax is [light:]option=value");
				}
				// shave off the light name
				option = option.substring(indexOfColon + 1);

				// check which light this is
				boolean lightfound = false;
				for (int j = 0; j < nrlights; j++)
				{
					if (lightname.equals(client.getLightName(j)))
					{
						lightfound = true;
						lightnr = j;
						break;
					}
				}
				if (!lightfound)
				{
					throw new BoblightException("light \"" + lightname
							+ "\" used in option \"" + this.options.get(i)
							+ "\" doesn't exist");
				}
			}

			// check if '=' exists and it's not at the end of the string
			if (option.indexOf('=') == -1
					|| indexOfColon == option.length() - 1)
			{
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

		final T commandLineArgs = getArgBean();
		parser = new CmdLineParser(commandLineArgs);
		try
		{
			parser.parseArgument(args);
		}
		catch (CmdLineException e1)
		{
			throw new BoblightRuntimeException(
					"Error parsing program arguments.", e1);
		}

		this.printBoblightOptions = commandLineArgs.printBoblightOptions;

		if (this.printBoblightOptions)
		{
			return null;
		}

		this.priority = commandLineArgs.priority;

		if (priority == -1 || priority < 0 || priority > 255)
		{
			throw new BoblightRuntimeException("Wrong option " + priority
					+ " for argument -p");
		}

		this.address = commandLineArgs.server;

		// TODO parse address with handler
		// check if we have a port
		if (commandLineArgs.server != null
				&& commandLineArgs.server.indexOf(':') != -1)
		{
			this.address = commandLineArgs.server.substring(0,
					commandLineArgs.server.indexOf(':'));
			commandLineArgs.server = commandLineArgs.server
					.substring(commandLineArgs.server.indexOf(':') + 1);
			try
			{
				this.port = Integer.valueOf(commandLineArgs.server);
			}
			catch (final NumberFormatException nfe)
			{
			}

			if (this.port != -1 && this.port < 0 || this.port > 65535)
			{
				throw new BoblightRuntimeException("Wrong option "
						+ commandLineArgs.server + " for argument -s");
			}
		}

		this.printHelp = commandLineArgs.printHelp;

		this.sync = commandLineArgs.sync;

		if (commandLineArgs.options != null)
		{
			this.options.addAll(commandLineArgs.options);
		}

		// pass our argument to a derived class
		this.parseFlagsExtended(commandLineArgs);

		// some postprocessing
		this.postGetopt(parser);

		return commandLineArgs;
	}

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
	 * Gets called after Getopt for derived classes.
	 * 
	 * @param option
	 *            the option
	 * @param args
	 *            the program arguments
	 * @throws BoblightConfigurationException
	 */
	protected void postGetopt(CmdLineParser parser)
			throws BoblightConfigurationException {
	}

	/**
	 * Prints all available options to stdout.
	 */
	@Override
	public void printOptions() {
		final int nroptions = BoblightOptions.getNrOptions();

		for (int i = 0; i < nroptions; i++)
		{
			System.out.println(BoblightOptions.getOptionDescription(i));
		}
	}

	public void setSync(final boolean b) {
		this.sync = b;
	}

	@Override
	public void printHelpMessage() {
		System.out.println("Usage: boblight-[type] [OPTION]");
		System.out.println("");
		System.out.println("  [OPTION]:");
		System.out.println("");

		parser.printUsage(System.out);
	}

}
