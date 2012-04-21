package org.boblight4j.client;

import org.boblight4j.Constants;
import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightException;
import org.boblight4j.exception.BoblightRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for runnable clients.
 * 
 * @author agebauer
 * 
 */
public abstract class AbstractBoblightClient {

	private static final Logger LOG = LoggerFactory
			.getLogger(AbstractBoblightClient.class);

	private final FlagManager flagManager;
	private boolean stop = false;

	/**
	 * Constructor.
	 * 
	 * @param args
	 *            the program arguments
	 */
	public AbstractBoblightClient(final String[] args) {
		this.flagManager = this.getFlagManager();
		this.parseArgs(this.flagManager, args);
	}

	/**
	 * Registers a shutdown hook and calls {@link #run()}. The shutdown hook
	 * will cause the client thread to stop.
	 * 
	 * @return the exit code
	 */
	public final int doRun() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				LOG.info("Caught KILL signal. Will halt program.");
				AbstractBoblightClient.this.stop = true;
			}
		}));

		while (!this.stop) {
			// will run the client until it returns ( in case of error or normal
			// termination)
			return this.run();
		}
		return 1;
	}

	/**
	 * Returns the flag manager used for this client implementation.
	 * 
	 * @return the flag manager
	 */
	protected abstract FlagManager getFlagManager();

	/**
	 * Returns if the client should stop.
	 * 
	 * @return whether the client should stop
	 */
	public final boolean isStop() {
		return this.stop;
	}

	/**
	 * Parses the program arguments.
	 * 
	 * @param flagmanager
	 *            the flag manager
	 * @param args
	 *            the program arguments
	 */
	public final void parseArgs(final FlagManager flagmanager,
			final String[] args) {
		try {
			flagmanager.parseFlags(args);
		} catch (final BoblightConfigurationException error) {
			LOG.error(
					"Error occured configuring client with passed program arguments.",
					error);
			flagmanager.printHelpMessage();
			System.exit(1);
		} catch (final BoblightRuntimeException error) {
			flagmanager.printHelpMessage();
			System.exit(1);
		}

		if (flagmanager.isPrintHelp()) // print help message
		{
			flagmanager.printHelpMessage();
			System.exit(1);
		}

		// print boblight options (-o [light:]option=value)
		if (flagmanager.isPrintOptions()) {
			flagmanager.printOptions();
			System.exit(1);
		}

	}

	/**
	 * Run loop.
	 * 
	 * @return exit code
	 */
	protected abstract int run();

	/**
	 * Tries to setup the client.<br>
	 * Connects to the server and sets the priority. Does a sleep in case of
	 * error before returning.
	 * 
	 * @param client
	 *            the client
	 * @return true if setup succeeds, false otherwise
	 */
	public final boolean trySetup(final Client client) {
		try {
			LOG.info("Connecting to boblightd");

			// try to connect, if we can't then bitch to stderr and destroy
			// boblight
			client.connect(this.flagManager.getAddress(),
					this.flagManager.getPort(), Constants.CONNECTION_TIMEOUT);

			client.setPriority(this.flagManager.getPriority());
		} catch (final BoblightException e) {
			LOG.info("Waiting 10 seconds before trying again");
			client.destroy();
			try {
				Thread.sleep(Constants.RETRY_DELAY_ERROR);
			} catch (final InterruptedException ex) {
				LOG.warn("", ex);
			}
			return false;
		}
		return true;
	}

}
