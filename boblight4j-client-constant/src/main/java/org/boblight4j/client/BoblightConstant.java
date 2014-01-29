package org.boblight4j.client;

import org.boblight4j.exception.BoblightException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoblightConstant extends AbstractRemoteBoblightClient {

	private static final int BYTE_SHIFT = 0xFF;

	private static final Logger LOG = LoggerFactory
			.getLogger(BoblightConstant.class);

	private FlagManagerConstant flagmanager;

	public BoblightConstant(String[] args) {
		super(args);
	}

	public static void main(String[] args) {
		System.exit(new BoblightConstant(args).run());
	}

	protected final int run() {

		while (!isStop())
		{
			SocketClientImpl boblight = new SocketClientImpl(
					new LightsHolderImpl());

			if (!trySetup(boblight))
			{
				continue;
			}

			LOG.info("Connection to boblightd opened\n");

			// if we can't parse the boblight option lines (given with -o)
			// properly, just exit
			try
			{
				flagmanager.parseBoblightOptions(boblight);
			}
			catch (Exception error)
			{
				LOG.error("", error);
				return 1;
			}

			// load the color into int array
			int rgb[] = new int[] {
					(flagmanager.getColor() >> 16) & BYTE_SHIFT,
					(flagmanager.getColor() >> 8) & BYTE_SHIFT,
					flagmanager.getColor() & BYTE_SHIFT };

			try
			{
				// set all lights to the color we want and send it
				boblight.getLightsHolder().addPixel(null, rgb);

				// some error happened, probably connection
				// broken, so bitch and try again
				boblight.sendRgb(true, null);

			}
			catch (Exception e)
			{

				LOG.error(
						"Exception occured during add pixel or sending rgb values",
						e);
				boblight.destroy();
				continue;
			}

			// keep checking the connection to boblightd every 10 seconds, if it
			// breaks we try to connect again
			while (!isStop())
			{

				try
				{
					boblight.ping(null, true);
				}
				catch (BoblightException e)
				{
					LOG.error("BoblightException occurred during Ping", e);
					break;
				}

				try
				{
					Thread.sleep(10000);
				}
				catch (InterruptedException e)
				{
					LOG.error("", e);
				}
			}
			// destroy
			boblight.destroy();
		}

		LOG.info("Exiting\n");
		return 0;
	}

	@Override
	protected FlagManager getFlagManager() {
		if (this.flagmanager == null)
		{
			this.flagmanager = new FlagManagerConstant();
		}
		return this.flagmanager;
	}

}
