package org.boblight4j.server;

import gnu.getopt.Getopt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.boblight4j.device.Device;
import org.boblight4j.device.Light;
import org.boblight4j.exception.BoblightException;
import org.boblight4j.server.config.ConfigImpl;
import org.boblight4j.server.config.ConfigUpdater;
import org.boblight4j.utils.Pointer;

/**
 * This class represents the main entry point for the boblight4j server
 * application.
 * 
 * @author agebauer
 * 
 */
public class BoblightDaemon {

	private static final Logger LOG = Logger.getLogger(BoblightDaemon.class
			.getName());

	private static final String DEFAULTCONF = "/etc/boblight.conf";

	private static boolean stop;

	public static void main(final String[] args) {
		try
		{
			new BoblightDaemon().init(args);
		}
		catch (final Exception e)
		{
			LOG.fatal("Fatal error occurred", e);
			System.exit(1);
		}
		System.exit(0);
	}

	/**
	 * Initialises the BoblightDaemon.
	 * 
	 * @param args
	 *            the program arguments
	 * @throws IOException
	 * @throws BoblightException
	 */
	private void init(final String[] args) throws IOException,
			BoblightException {
		// read flags
		final Pointer<String> configfile = new Pointer<String>(null);
		final Pointer<Boolean> help = new Pointer<Boolean>(null);

		try
		{
			this.parseFlags(args, help, configfile);
			if (help.get())
			{
				throw new BoblightException("help");
			}
		}
		catch (final BoblightException e1)
		{
			this.printHelpMessage();
			System.exit(1);
		}

		// init our logfile
		this.printFlags(args.length, args);

		// set up signal handlers
		// signal(SIGTERM, SignalHandler);
		// signal(SIGINT, SignalHandler);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				BoblightDaemon.this.shutdown();
			}
		}));

		final List<Light> lights = new ArrayList<Light>(); // lights pool
		final ClientsHandlerImpl clients = new ClientsHandlerImpl(lights);

		// class for loading and parsing config load and parse config
		final ConfigImpl config = new ConfigImpl();
		config.loadConfigFromFile(configfile.get());

		config.checkConfig();

		// where we store devices
		final List<Device> devices = new ArrayList<Device>();
		config.buildConfig(clients, devices, lights);

		final ConfigUpdater updater = new ConfigUpdater(new File(
				configfile.get()), clients, config, devices, lights);
		updater.startThread();

		// start the devices
		LOG.info("starting devices");
		for (int i = 0; i < devices.size(); i++)
		{
			devices.get(i).startThread();
		}

		// run the clients handler
		clients.process();

		while (!stop)
		{
			try
			{
				Thread.sleep(100);
			}
			catch (final InterruptedException e)
			{
				LOG.warn("Error during Thread.sleep call.", e);
			}
		}

		// signal that the devices should stop
		LOG.info("signaling devices to stop");
		for (int i = 0; i < devices.size(); i++)
		{
			devices.get(i).asyncStopThread();
		}

		// clean up the clients handler
		clients.cleanup();

		// stop the devices
		LOG.info("waiting for devices to stop");
		for (int i = 0; i < devices.size(); i++)
		{
			devices.get(i).stopThread();
		}
		LOG.info("exiting");
	}

	private void parseFlags(final String[] args, final Pointer<Boolean> help,
			final Pointer<String> configfile) throws BoblightException {
		help.assign(false);
		configfile.assign(DEFAULTCONF);

		final Getopt getopt = new Getopt("flagmanager", args, "c:hf");

		int c;
		while ((c = getopt.getopt()) != -1)
		{
			if (c == 'c')
			{
				configfile.assign(getopt.getOptarg());
			}
			else if (c == 'h')
			{
				help.assign(true);
				return;
			}
			else if (c == '?')
			{
				if (getopt.getopt() == 'c')
				{
					throw new BoblightException("Option "
							+ (char) getopt.getOptopt()
							+ " requires an argument");
				}
				else
				{
					throw new BoblightException("Unknown option "
							+ (char) getopt.getOptopt());
				}
			}
		}

	}

	private void printFlags(final int length, final String[] args) {
		StringBuilder flags = new StringBuilder("starting");

		for (int i = 0; i < length; i++)
		{
			flags.append(' ');
			flags.append(args[i]);
		}

		LOG.info(flags.toString());
	}

	private void printHelpMessage() {

	}

	protected void shutdown() {
		LOG.info("shutting down");
	}
}
