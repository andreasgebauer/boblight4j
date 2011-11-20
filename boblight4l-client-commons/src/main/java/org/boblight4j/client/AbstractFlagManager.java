package org.boblight4j.client;

import gnu.getopt.Getopt;

import java.util.ArrayList;
import java.util.List;

import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightException;
import org.boblight4j.exception.BoblightParseException;
import org.boblight4j.utils.BooleanParser;

/**
 * Base class for all <code>FlagManager</code> implementations.
 * 
 * @author agebauer
 * 
 */
public abstract class AbstractFlagManager implements FlagManager {

	private String address;
	private String flags;

	private final List<String> options = new ArrayList<String>();
	private int port;

	private boolean printBoblightOptions;
	private boolean printHelp;

	private int priority;
	private boolean sync;

	public AbstractFlagManager() {
		this.port = -1; // -1 tells libboblight to use default port
		this.address = null; // NULL tells libboblight to use default address
		this.priority = 128; // default priority
		this.printHelp = false; // don't print helpmessage unless asked to
		this.printBoblightOptions = false; // same for libboblight options
		this.sync = false; // sync mode disabled by default

		// default getopt flags, can be extended in derived classes
		// p = priority, s = address[:port], o = boblight option, l = list
		// boblight options, h = print help message, f = fork
		this.flags = "p:s:o:lhfy:";
	}

	protected void addFlags(final String flags) {
		this.flags += flags;
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
	public final void parseBoblightOptions(final Client client)
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
			if (option.indexOf(':') != -1)
			{
				lightname = option.substring(0, option.indexOf(':'));
				// check if : isn't the last char in the string
				if (option.indexOf(':') == option.length() - 1)
				{
					throw new BoblightException("wrong option \"" + option
							+ "\", syntax is [light:]option=value");
				}
				// shave off the light name
				option = option.substring(option.indexOf(':') + 1);

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
					|| option.indexOf(':') == option.length() - 1)
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
	 */
	@Override
	public void parseFlags(final String[] args)
			throws BoblightConfigurationException {
		final String[] argv = args.clone();
		String option;
		int optChar;

		final Getopt getopt = new Getopt("flagmanager", args, this.flags);
		while ((optChar = getopt.getopt()) != -1)
		{
			// priority
			if (optChar == 'p')
			{
				option = getopt.getOptarg();
				int priority = -1;
				try
				{
					priority = Integer.valueOf(option);
				}
				catch (final NumberFormatException nfe)
				{
					// nothing
				}
				if (priority == -1 || priority < 0 || priority > 255)
				{
					throw new RuntimeException("Wrong option " + option
							+ " for argument -p");
				}
				this.priority = priority;
			}
			// address[:port]
			else if (optChar == 's')
			{
				option = getopt.getOptarg();
				// store address
				this.address = option;

				// check if we have a port
				if (option.indexOf(':') != -1)
				{
					this.address = option.substring(0, option.indexOf(':'));
					option = option.substring(option.indexOf(':') + 1);
					try
					{
						this.port = Integer.valueOf(option);
					}
					catch (final NumberFormatException nfe)
					{
					}

					if (this.port != -1 && this.port < 0 || this.port > 65535)
					{
						throw new RuntimeException("Wrong option " + option
								+ " for argument -s");
					}
				}
			}
			// option for libboblight
			else if (optChar == 'o')
			{
				this.options.add(getopt.getOptarg());
			}
			// list libboblight options
			else if (optChar == 'l')
			{
				this.printBoblightOptions = true;
				return;
			}
			// print help message
			else if (optChar == 'h')
			{
				this.printHelp = true;
				return;
			}
			// sync
			else if (optChar == 'y')
			{
				final String optarg = getopt.getOptarg();
				try
				{
					this.sync = BooleanParser.parse(optarg);
				}
				catch (final BoblightParseException e)
				{
					throw new BoblightConfigurationException("Wrong value "
							+ optarg + " for sync mode", e);
				}
			}
			// unknown option
			else if (optChar == '?')
			{
				// check if we know this option, but expected an argument
				if (this.flags.indexOf((char) getopt.getOptopt() + ":") == -1)
				{
					throw new RuntimeException("Option "
							+ (char) getopt.getOptopt()
							+ "requires an argument");
				}
				else
				{
					throw new RuntimeException("Unknown option "
							+ (char) getopt.getOptopt());
				}
			}
			else
			{
				// pass our argument to a derived class
				this.parseFlagsExtended(argv, optChar, getopt.getOptarg());
			}
		}
		// some postprocessing
		this.postGetopt(getopt.getOptind(), argv);
	}

	/**
	 * Gets called from parseFlags, for derived classes.
	 * 
	 * @param args
	 *            all program arguments
	 * @param option
	 *            the option
	 * @param optarg
	 *            the option argument
	 * @throws BoblightConfigurationException
	 */
	protected void parseFlagsExtended(final String[] args, final int option,
			final String optarg) throws BoblightConfigurationException {
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
	protected void postGetopt(final int option, final String[] args)
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

}
