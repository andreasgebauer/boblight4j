package org.boblight4j.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.boblight4j.exception.BoblightException;
import org.boblight4j.server.config.ConfigUpdater;
import org.boblight4j.server.config.Device;
import org.boblight4j.server.config.Light;
import org.boblight4j.server.config.TcpServerConfigImpl;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents the main entry point for the boblight4j server
 * application.
 * 
 * @author agebauer
 * 
 */
public class BoblightDaemon {

	/**
	 * The arguments bean for the boblight4j server daemon arguments.
	 * 
	 * @author agebauer
	 * 
	 */
	public static class ServerArgs {

		@Option(name = "-c")
		private File configFile = new File(DEFAULTCONF);

		@Option(name = "-h")
		private boolean help;

	}

	private static final Logger LOG = LoggerFactory
			.getLogger(BoblightDaemon.class.getName());

	private static final String DEFAULTCONF = "/etc/boblight.conf";

	private static boolean stop;

	private ServerArgs args;

	/**
	 * Main method. Starts the boblight4j server daemon.
	 * 
	 * @param args
	 *            the program arguments
	 */
	public static void main(final String[] args) {
		try {
			new BoblightDaemon().init(args);
		} catch (final Exception e) {
			LOG.error("Fatal error occurred", e);
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
		try {
			this.args = this.parseFlags(args);
			if (this.args.help) {
				throw new BoblightException("help");
			}
		} catch (final BoblightException e1) {
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
		final SocketClientsHandlerImpl clients = new SocketClientsHandlerImpl(
				lights);

		// class for loading and parsing config load and parse config
		final TcpServerConfigImpl config = new TcpServerConfigImpl();
		config.loadConfigFromFile(this.args.configFile);

		config.checkConfig();

		// where we store devices
		final List<Device> devices = new ArrayList<Device>();
		config.buildConfig(clients, devices, lights);

		final ConfigUpdater updater = new ConfigUpdater(this.args.configFile,
				clients, config, devices, lights);
		updater.startThread();

		// start the devices
		LOG.info("starting devices");
		for (int i = 0; i < devices.size(); i++) {
			devices.get(i).startThread();
		}

		// run the clients handler
		clients.process();

		while (!stop) {
			try {
				Thread.sleep(100);
			} catch (final InterruptedException e) {
				LOG.warn("Error during Thread.sleep call.", e);
			}
		}

		// signal that the devices should stop
		LOG.info("signaling devices to stop");
		for (int i = 0; i < devices.size(); i++) {
			devices.get(i).asyncStopThread();
		}

		// clean up the clients handler
		clients.cleanup();

		// stop the devices
		LOG.info("waiting for devices to stop");
		for (int i = 0; i < devices.size(); i++) {
			devices.get(i).stopThread();
		}
		LOG.info("exiting");
	}

	private ServerArgs parseFlags(final String[] args) throws BoblightException {
		try {
			final ServerArgs argsBean = new ServerArgs();
			new CmdLineParser(argsBean).parseArgument(args.clone());
			return argsBean;
		} catch (CmdLineException e) {
			throw new BoblightException(e);
		}
	}

	private void printFlags(final int length, final String[] args) {
		StringBuilder flags = new StringBuilder("starting");

		for (int i = 0; i < length; i++) {
			flags.append(' ');
			flags.append(args[i]);
		}

		LOG.info(flags.toString());
	}

	private void printHelpMessage() {
		// TODO: print help message
	}

	protected void shutdown() {
		LOG.info("shutting down");
	}
}
