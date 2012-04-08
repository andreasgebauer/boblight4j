package org.boblight4j.client.v4l;

import org.apache.log4j.Logger;
import org.boblight4j.client.AbstractBoblightClient;
import org.boblight4j.client.ClientImpl;
import org.boblight4j.client.FlagManager;
import org.boblight4j.client.grabber.ActiveGrabber;
import org.boblight4j.client.grabber.Grabber;
import org.boblight4j.client.video.ImageGrabberFactory;
import org.boblight4j.exception.BoblightException;

public class BoblightV4l extends AbstractBoblightClient {

	private static final int SLEEP_AFTER_ERROR = 5000;

	private static final Logger LOG = Logger.getLogger(BoblightV4l.class);

	private FlagManagerV4l flagManager;

	public static void main(final String[] args) {
		System.exit(new BoblightV4l(args).run());
	}

	private boolean stop;

	public BoblightV4l(final String[] args) {
		super(args);
	}

	@Override
	protected final int run() {

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				LOG.info("Caught KILL signal");
				BoblightV4l.this.stop = true;
			}
		}));

		while (!this.stop)
		{
			// init boblight
			// void* boblight = boblight_init();
			final ClientImpl boblight = new ClientImpl();

			if (!this.trySetup(boblight))
			{
				continue;
			}

			LOG.info("Connection to boblightd opened");

			// if we can't parse the boblight option lines (given with -o)
			// properly, just exit
			try
			{
				flagManager.parseBoblightOptions(boblight);
			}
			catch (final Exception error)
			{
				LOG.error("Error parsing boblight options", error);
				return 1;
			}

			// set up videograbber
			final Grabber grabber = new ImageGrabberFactory().getImageGrabber(
					boblight, true, this.flagManager.width,
					this.flagManager.height);
			try
			{
				grabber.setup(flagManager);
			}
			catch (final BoblightException error)
			{
				LOG.error(
						"Error occurred while setting up device. Retrying in 5 seconds.",
						error);
				try
				{
					Thread.sleep(SLEEP_AFTER_ERROR);
				}
				catch (final InterruptedException e)
				{
					LOG.warn("Error during call of Thread.sleep().", e);
				}
				boblight.destroy();
				continue;
			}

			if (grabber instanceof ActiveGrabber)
			{
				try
				{
					// this will keep looping until we should stop or boblight
					// gives
					// an error
					((ActiveGrabber) grabber).run();
				}
				catch (final Exception error)
				{
					LOG.error("Fatal error occurred", error);
					grabber.cleanup();
					boblight.destroy();
					return 1;
				}

				grabber.cleanup();

				boblight.destroy();
			}

		}

		LOG.info("Exiting\n");
		return 0;
	}

	@Override
	protected FlagManager getFlagManager() {
		if (this.flagManager == null)
		{
			this.flagManager = new FlagManagerV4l();
		}
		return this.flagManager;
	}

}
