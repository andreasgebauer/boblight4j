package org.boblight4j.client.v4l;

import org.boblight4j.client.AbstractFlagManager;
import org.boblight4j.client.v4l.FlagManagerV4l.V4lArgs;
import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightParseException;
import org.boblight4j.utils.StdIO;
import org.kohsuke.args4j.Option;

class FlagManagerV4l extends AbstractFlagManager<V4lArgs> {

	public class V4lArgs extends AbstractFlagManager.CommandLineArgs {

		@Option(name = "-c")
		String device;

		@Option(name = "-w")
		String sscanf;

		@Option(name = "-v")
		String standard;

		@Option(name = "-i")
		int channel;

		@Option(name = "-d")
		boolean debug;

		@Option(name = "-n")
		boolean checkSignal;

		@Option(name = "-e")
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
		// c = device, w == widthxheight, v = video standard, i = input, d =
		// debug mode, e = codec
		this.addFlags("c:w:v:i:d::ne:");

		// default device
		this.device = "/dev/video0";

		// default size
		this.width = 64;
		this.height = 64;

		// sync mode enabled by default
		this.setSync(true);

		// channel of -1 means ffmpeg doesn't change it
		this.channel = -1;

		// empty standard means ffmpeg doesn't change it
		this.standard = null;

		this.checksignal = false;

		this.debug = false;
	}

	public int getChannel() {
		return channel;
	}

	@Override
	protected V4lArgs getArgBean() {
		return new V4lArgs();
	}

	@Override
	protected void parseFlagsExtended(final V4lArgs argv, final int c,
			final String optarg) throws BoblightConfigurationException {

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
						throw new RuntimeException("Wrong value " + optarg
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

	@Override
	public void printHelpMessage() {
		final StringBuilder msg = new StringBuilder();
		msg.append("Usage: boblight-v4l [OPTION]\n");
		msg.append("\n");
		msg.append("  options:\n");
		msg.append("\n");
		msg.append("  -p  priority, from 0 to 255, default is 128\n");
		msg.append("  -s  address:[port], set the address and optional port to connect to\n");
		msg.append("  -o  add libboblight option, syntax: [light:]option=value\n");
		msg.append("  -l  list libboblight options\n");
		msg.append("  -f  fork\n");
		msg.append("  -c  set the device to use, default is /dev/video0\n");
		msg.append("  -w  widthxheight of the captured image, example: -w 400x300\n");
		msg.append("  -v  video standard\n");
		msg.append("  -i  video input number\n");
		msg.append("  -n  only turn on boblight client when there's a video signal\n");
		msg.append("  -e  use custom codec, default is video4linux2 or video4linux\n");
		msg.append("  -d  debug mode\n");
		msg.append("  -y  set the sync mode, default is on, valid options are \"on\" and \"off\"\n");
		msg.append("\n");

		System.out.println(msg.toString());
	}

}
