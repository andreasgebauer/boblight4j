package org.boblight4j.client.X11;

import org.apache.log4j.Logger;
import org.boblight4j.client.AbstractBoblightClient;
import org.boblight4j.client.Client;
import org.boblight4j.client.grabber.Grabber;
import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightException;

public class BoblightX11 extends AbstractBoblightClient {

	private static final Logger LOG = Logger.getLogger(BoblightX11.class);

	static final int XGETIMAGE = 0;
	static final int XRENDER = 1;

	public static void main(final String[] args) {
		System.exit(new BoblightX11(args).doRun());
	}

	private FlagManagerX11 flagmanager;

	private boolean stop;

	public BoblightX11(final String[] args) {
		super(args);
	}

	@Override
	protected FlagManagerX11 getFlagManager() {
		if (this.flagmanager == null)
		{
			this.flagmanager = new FlagManagerX11();
		}
		return this.flagmanager;
	}

	@Override
	protected int run() {

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				LOG.info("Caught KILL signal");
				BoblightX11.this.stop = true;
			}
		}));

		// set default pixels
		if (this.flagmanager.pixels == -1)
		{
			// xgetimage is crap slow so we grab 16x16 pixels
			if (this.flagmanager.method == XGETIMAGE)
			{
				this.flagmanager.pixels = 16;
			}
			else
			{
				// xrender is very fast so we scale down the root window
				// to a 64x64 pixels pixmap
				this.flagmanager.pixels = 64;
			}
		}

		while (!this.stop)
		{
			final Client boblight = new Client();
			try
			{
				// init boblight
				// void* boblight = boblight_init();

				LOG.info("Connecting to boblightd\n");

				// try to connect, if we can't then bitch to stderr and destroy
				// boblight

				boblight.connect(this.flagmanager.getAddress(),
						this.flagmanager.getPort(), 5000);

				boblight.setPriority(this.flagmanager.getPriority());

				// LOG.log(Level.SEVERE, boblight.getError().getMessage(),
				// boblight.getError());
			}
			catch (final BoblightException e)
			{
				LOG.error("Error occurred during connect", e);
				LOG.info("Waiting 10 seconds before trying again\n");
				boblight.destroy();
				try
				{
					Thread.sleep(10000);
				}
				catch (final InterruptedException ex)
				{
					ex.printStackTrace();
				}
				continue;
			}

			LOG.info("Connection to boblightd opened\n");
			// if we can't parse the boblight option lines (given with -o)
			// properly, just exit
			try
			{
				this.flagmanager.parseBoblightOptions(boblight);
			}
			catch (final Exception error)
			{
				LOG.error("", error);
				return 1;
			}

			try
			{
				// set up grabber, based on whether we want to use xrender or
				// xgetimage
				Grabber grabber = null;
				if (this.flagmanager.method == XGETIMAGE)
				{
					grabber = new GrabberXGetImage(boblight,
							this.flagmanager.isSync(), this.flagmanager.pixels,
							this.flagmanager.interval);
				}
				else if (this.flagmanager.method == XRENDER)
				{
					grabber = new GrabberXRender(boblight,
							this.flagmanager.isSync(), this.flagmanager.pixels,
							this.flagmanager.interval);
				}
				else
				{
					throw new BoblightConfigurationException("Grab method "
							+ this.flagmanager.method + " not supported.");
				}

				if (this.flagmanager.debug)
				{
					grabber.setupDebug();
				}

				grabber.setup(this.flagmanager);
				grabber.run();
			}
			catch (final Exception e)
			{
				boblight.destroy();
				LOG.error(
						"Exception occured during add pixel or sending rgb values. Trying again in a few seconds.",
						e);
				try
				{
					Thread.sleep(10000);
				}
				catch (final InterruptedException ex)
				{
					LOG.error("", ex);
				}
				continue;
			}

			// keep checking the connection to boblightd every 10 seconds, if it
			// breaks we try to connect again
			while (!this.stop)
			{
				try
				{
					boblight.ping(null, true);
				}
				catch (final BoblightException e)
				{
					LOG.error("BoblightException occurred during Ping", e);
					break;
				}

				try
				{
					Thread.sleep(10000);
				}
				catch (final InterruptedException e)
				{
					LOG.error("", e);
				}
			}
			boblight.destroy();
		}
		LOG.info("Exiting\n");
		return 0;
	}

}
