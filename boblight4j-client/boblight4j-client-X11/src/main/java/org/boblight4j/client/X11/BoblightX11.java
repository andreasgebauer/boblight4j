package org.boblight4j.client.X11;

import org.boblight4j.Constants;
import org.boblight4j.client.AbstractRemoteBoblightClient;
import org.boblight4j.client.LightsHolderImpl;
import org.boblight4j.client.SocketClient;
import org.boblight4j.client.grabber.ActiveGrabber;
import org.boblight4j.client.grabber.Grabber;
import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runnable class.
 * 
 * @author agebauer
 * 
 */
public class BoblightX11 extends AbstractRemoteBoblightClient {

	public enum RenderMethod {
		XGETIMAGE, XRENDER
	}

	private static final Logger LOG = LoggerFactory
			.getLogger(BoblightX11.class);

	public static void main(final String[] args) {
		System.exit(new BoblightX11(args).doRun());
	}

	private FlagManagerX11 flagmanager;

	public BoblightX11(final String[] args) {
		super(args);
	}

	@Override
	protected FlagManagerX11 getFlagManager() {
		if (this.flagmanager == null) {
			this.flagmanager = new FlagManagerX11();
		}
		return this.flagmanager;
	}

	@Override
	protected int run() {

		int width = this.flagmanager.getPixels();
		int height = this.flagmanager.getPixels();

		// set default pixels
		if (this.flagmanager.getPixels() == -1) {
			// xgetimage is crap slow so we grab 16x16 pixels
			if (this.flagmanager.method == RenderMethod.XGETIMAGE) {
				width = 16;
				height = 16;
			} else {
				// xrender is very fast so we scale down the root window
				// to a 64x64 pixels pixmap
				width = 64;
				height = 64;
			}
		}

		while (!this.isStop()) {
			final SocketClient client = new SocketClient(
					new LightsHolderImpl());
			try {
				// init boblight
				// void* boblight = boblight_init();

				LOG.info("Connecting to boblightd\n");

				// try to connect, if we can't then bitch to stderr and destroy
				// boblight

				client.connect(this.flagmanager.getAddress(),
						this.flagmanager.getPort(),
						Constants.CONNECTION_TIMEOUT);

				client.sendPriority(this.flagmanager.getPriority());

				// LOG.log(Level.SEVERE, boblight.getError().getMessage(),
				// boblight.getError());
			} catch (final BoblightException e) {
				LOG.error("Error occurred during connect", e);
				LOG.info("Waiting 10 seconds before trying again\n");
				client.destroy();
				try {
					Thread.sleep(Constants.RETRY_DELAY_ERROR);
				} catch (final InterruptedException ex) {
					ex.printStackTrace();
				}
				continue;
			}

			LOG.info("Connection to boblightd opened\n");
			// if we can't parse the boblight option lines (given with -o)
			// properly, just exit
			try {
				this.flagmanager.parseBoblightOptions(client);
			} catch (final BoblightException error) {
				LOG.error("Error parsing boblight4j options.", error);
				this.flagmanager.printOptions();
				return 1;
			} catch (final Exception error) {
				LOG.error("Unexpected Error:", error);
				return 1;
			}

			try {
				// set up grabber, based on whether we want to use xrender or
				// xgetimage
				Grabber grabber = null;
				if (this.flagmanager.method == RenderMethod.XGETIMAGE) {
					grabber = new GrabberXGetImage(client,
							this.flagmanager.isSync(), width, height,
							this.flagmanager.getInterval());
				} else if (this.flagmanager.method == RenderMethod.XRENDER) {
					grabber = new GrabberXRender(client,
							this.flagmanager.isSync(), width, height,
							this.flagmanager.getInterval());
				} else {
					throw new BoblightConfigurationException("Grab method "
							+ this.flagmanager.method + " not supported.");
				}

				if (this.flagmanager.debug) {
					grabber.setupDebug();
				}

				grabber.setup(this.flagmanager);

				if (grabber instanceof ActiveGrabber) {
					((ActiveGrabber) grabber).run();
				}
			} catch (final Exception e) {
				client.destroy();
				LOG.error(
						"Exception occured during add pixel or sending rgb values. Trying again in a few seconds.",
						e);
				try {
					Thread.sleep(Constants.RETRY_DELAY_ERROR);
				} catch (final InterruptedException ex) {
					LOG.error("", ex);
				}
				continue;
			}

			// keep checking the connection to boblightd every 10 seconds, if it
			// breaks we try to connect again
			while (!this.isStop()) {
				try {
					client.ping(null, true);
				} catch (final BoblightException e) {
					LOG.error("BoblightException occurred during Ping", e);
					break;
				}

				try {
					Thread.sleep(Constants.RETRY_DELAY_ERROR);
				} catch (final InterruptedException e) {
					LOG.error("", e);
				}
			}
			client.destroy();
		}
		LOG.info("Exiting\n");
		return 0;
	}
}
