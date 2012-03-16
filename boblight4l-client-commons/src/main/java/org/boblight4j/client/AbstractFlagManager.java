package org.boblight4j.client;

import gnu.getopt.Getopt;

import java.util.ArrayList;
import java.util.List;

import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * Base class for all <code>FlagManager</code> implementations.
 * 
 * @author agebauer
 * 
 */
public abstract class AbstractFlagManager implements FlagManager {

	public class CommandLineArgs {
		@Option(name = "-l")
		boolean printBoblightOptions;

		@Option(name = "options", aliases = { "-o" }, handler = CustomOptionHandler.class)
		List<String> options;

		@Option(name = "-p")
		int priority;

		@Option(name = "-s", usage = "server address to connect to (eg localhost[:12345])")
		String server;

		@Option(name = "-y", handler = BooleanOptionHandler.class)
		boolean sync;

		@Option(name = "-h")
		boolean printHelp;
	}

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

		final CommandLineArgs commandLineArgs = new CommandLineArgs();
		CmdLineParser parser = new CmdLineParser(commandLineArgs);
		try
		{
			parser.parseArgument(args);
		}
		catch (CmdLineException e1)
		{
			e1.printStackTrace();
			throw new RuntimeException(e1);
		}

		this.printBoblightOptions = commandLineArgs.printBoblightOptions;

		if (this.printBoblightOptions)
		{
			return;
		}

		this.priority = commandLineArgs.priority;

		if (priority == -1 || priority < 0 || priority > 255)
		{
			throw new RuntimeException("Wrong option " + priority
					+ " for argument -p");
		}

		this.address = commandLineArgs.server;

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
				throw new RuntimeException("Wrong option "
						+ commandLineArgs.server + " for argument -s");
			}
		}

		this.printHelp = commandLineArgs.printHelp;

		this.sync = commandLineArgs.sync;

		if (commandLineArgs.options != null)
		{
			this.options.addAll(commandLineArgs.options);
		}

		final String[] argv = args.clone();
		int optChar;

		final Getopt getopt = new Getopt("flagmanager", args, this.flags);
		while ((optChar = getopt.getopt()) != -1)
		{
			// unknown option
			if (optChar == '?')
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
