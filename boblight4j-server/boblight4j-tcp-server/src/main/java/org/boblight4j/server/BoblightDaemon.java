package org.boblight4j.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.boblight4j.exception.BoblightException;
import org.boblight4j.server.config.ConfigReader;
import org.boblight4j.server.config.ConfigUpdater;
import org.boblight4j.server.config.Device;
import org.boblight4j.server.config.LightConfig;
import org.boblight4j.server.config.PlainTextConfigFileReader;
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

		@Option(name = "-c", usage = "The config file")
		private File configFile = new File(DEFAULTCONF);

		@Option(name = "-h", usage = "Print this help message")
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
		BoblightDaemon boblightDaemon = new BoblightDaemon();
		try {
			boblightDaemon.init(args);
		} catch (final Exception e) {
			LOG.error("Fatal error occurred", e);
			boblightDaemon.printHelpMessage();
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
		this.args = this.parseFlags(args);
		if (this.args.help) {
			throw new BoblightException("help");
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

		final SocketClientsHandlerImpl clients = new SocketClientsHandlerImpl();

		// class for loading
		final ConfigReader configReader = new PlainTextConfigFileReader(
				this.args.configFile);

		// class for parsing config
		final TcpServerConfigCreator configCreator = new TcpServerConfigCreator(
				configReader);

		final TcpServerConfigImpl config = configCreator.loadConfig(clients,
				null);
		clients.createLights(config.getLights());

		final List<Device> devices = config.getDevices();

		final ConfigUpdater updater = new ConfigUpdater(this.args.configFile,
				configCreator, clients, config);
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
		final ServerArgs argsBean = new ServerArgs();
		new CmdLineParser(argsBean).printUsage(System.out);
	}

	protected void shutdown() {
		LOG.info("shutting down");
	}
}
