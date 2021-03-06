package org.boblight4j.client.v4l;

import org.boblight4j.client.AbstractRemoteBoblightClient;
import org.boblight4j.client.Client;
import org.boblight4j.client.FlagManager;
import org.boblight4j.client.LightsHolderImpl;
import org.boblight4j.client.SocketClient;
import org.boblight4j.client.grabber.ActiveGrabber;
import org.boblight4j.client.grabber.Grabber;
import org.boblight4j.client.video.ImageGrabberFactory;
import org.boblight4j.exception.BoblightException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Boblight4J V4L client.
 * 
 * @author agebauer
 * 
 */
public class BoblightV4l extends AbstractRemoteBoblightClient {

	private static final int SLEEP_AFTER_ERROR = 5000;

	private static final Logger LOG = LoggerFactory
			.getLogger(BoblightV4l.class);

	private FlagManagerV4l flagManager;

	/**
	 * Main entry point.
	 * 
	 * @param args
	 *            the program arguments
	 */
	public static void main(final String[] args) {
		System.exit(new BoblightV4l(args).run());
	}

	private boolean stop;

	/**
	 * Creates a new V4L client.
	 * 
	 * @param args
	 *            the program arguments
	 */
	public BoblightV4l(final String[] args) {
		super(args);
	}

	@Override
	protected final int run() {
		while (!this.stop) {
			// init boblight
			// void* boblight = boblight_init();
			final Client client = new SocketClient(
					new LightsHolderImpl());

			if (!this.trySetup(client)) {
				LOG.warn("Setup failed. Retrying ...");
				continue;
			}

			LOG.info("Connection to boblightd opened");

			// if we can't parse the boblight option lines (given with -o)
			// properly, just exit
			try {
				flagManager.parseBoblightOptions(client);
			} catch (final Exception error) {
				LOG.error("Error parsing boblight options", error);
				return 1;
			}

			// set up videograbber
			final Grabber grabber = new ImageGrabberFactory().getImageGrabber(
					client, true, this.flagManager.width,
					this.flagManager.height);
			try {
				grabber.setup(flagManager);

				if (flagManager.debug) {
					grabber.setupDebug();
				}

			} catch (final BoblightException error) {
				LOG.error(
						"Error occurred while setting up device. Retrying in 5 seconds.",
						error);
				try {
					Thread.sleep(SLEEP_AFTER_ERROR);
				} catch (final InterruptedException e) {
					LOG.warn("Error during call of Thread.sleep().", e);
				}
				client.destroy();
				continue;
			}

			if (grabber instanceof ActiveGrabber) {
				try {
					// this will keep looping until we should stop or boblight
					// gives
					// an error
					((ActiveGrabber) grabber).run();
				} catch (final Exception error) {
					LOG.error("Fatal error occurred", error);
					grabber.cleanup();
					client.destroy();
					return 1;
				}

				grabber.cleanup();

				client.destroy();
			}

		}

		return 0;
	}

	@Override
	protected FlagManager getFlagManager() {
		if (this.flagManager == null) {
			this.flagManager = new FlagManagerV4l();
		}
		return this.flagManager;
	}

}
