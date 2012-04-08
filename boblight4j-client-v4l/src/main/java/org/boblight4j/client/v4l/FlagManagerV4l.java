package org.boblight4j.client.v4l;

import org.boblight4j.client.AbstractFlagManager;
import org.boblight4j.client.CommandLineArgs;
import org.boblight4j.client.v4l.FlagManagerV4l.V4lArgs;
import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightParseException;
import org.boblight4j.utils.StdIO;
import org.kohsuke.args4j.Option;

class FlagManagerV4l extends AbstractFlagManager<V4lArgs> {

	/**
	 * -c &lt;device> = device, w == widthxheight, v = video standard, i =
	 * input, d = debug mode, e = codec
	 * 
	 * @author agebauer
	 * 
	 */
	public class V4lArgs extends CommandLineArgs {

		@Option(name = "-c", usage = "set the device to use, default is /dev/video0")
		String device = "/dev/video0";

		@Option(name = "-w", usage = "widthxheight of the captured image, example: -w 400x300")
		String sscanf;

		@Option(name = "-v", usage = "video standard")
		String standard;

		@Option(name = "-i", usage = "video input number")
		int channel = -1;

		@Option(name = "-d", metaVar = "debug", usage = "debug mode")
		boolean debug;

		@Option(name = "-n", usage = "only turn on boblight client when there's a video signal")
		boolean checkSignal;

		@Option(name = "-e", usage = "use custom codec, default is video4linux2 or video4linux")
		public String customCodec;
	}

	private int channel;
	protected boolean debug;
	protected String device;
	int width;
	int height;
	private boolean checksignal;
	private String customcodec;
	private String standard;
	private String strdebugdpy;

	FlagManagerV4l() {

		// default size
		this.width = 64;
		this.height = 64;

		// sync mode enabled by default
		this.setSync(true);

	}

	public int getChannel() {
		return channel;
	}

	@Override
	protected V4lArgs getArgBean() {
		return new V4lArgs();
	}

	@Override
	protected void parseFlagsExtended(final V4lArgs argv)
			throws BoblightConfigurationException {

		this.device = argv.device;

		if (argv.sscanf != null)
		{
			Object[] sscanf;
			try
			{
				sscanf = StdIO.sscanf(argv.sscanf, "%ix%i");

				if (sscanf.length != 2)
				{
					this.width = (Integer) sscanf[0];
					this.height = (Integer) sscanf[1];

					if (this.width < 1 || this.height < 1)
					{
						throw new RuntimeException("Wrong value " + argv.sscanf
								+ " for widthxheight");
					}
				}
			}
			catch (BoblightParseException e)
			{
				throw new BoblightConfigurationException("", e);
			}
		}

		this.standard = argv.standard;

		this.channel = argv.channel;

		this.debug = argv.debug;

		this.checksignal = argv.checkSignal;

		this.customcodec = argv.customCodec;
	}

}
