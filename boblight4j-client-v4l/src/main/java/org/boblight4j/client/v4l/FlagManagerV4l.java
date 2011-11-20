package org.boblight4j.client.v4l;

import org.boblight4j.client.AbstractFlagManager;
import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightParseException;
import org.boblight4j.utils.StdIO;

class FlagManagerV4l extends AbstractFlagManager {

	private int channel;
	protected boolean debug;
	protected String device;
	int width;
	int height;
	private boolean checksignal;
	private String customcodec;
	private String standard;
	private String strdebugdpy;
	private String strstandard;

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

		// emptpy standard meands ffmpeg doesn't change it
		this.standard = null;

		this.checksignal = false;

		this.debug = false;
	}

	public int getChannel() {
		return channel;
	}

	@Override
	protected void parseFlagsExtended(final String[] argv, final int c,
			final String optarg) throws BoblightConfigurationException {
		if (c == 'c')
		{
			this.device = optarg;
		}
		else if (c == 'w')
		{
			Object[] sscanf;
			try
			{
				sscanf = StdIO.sscanf(optarg, "%ix%i");

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
		else if (c == 'v')
		{
			this.strstandard = optarg;
			this.standard = this.strstandard;
		}
		else if (c == 'i')
		{
			try
			{
				this.channel = Integer.valueOf(optarg);
			}
			catch (final NumberFormatException e)
			{
				throw new RuntimeException("Wrong value " + optarg
						+ " for channel", e);
			}
		}
		else if (c == 'd')
		{
			this.debug = true;
			// if (optarg != null && !optarg.isEmpty()) // optional debug dpy
			// {
			// this.strdebugdpy = optarg;
			// }
		}
		else if (c == 'n')
		{
			this.checksignal = true;
		}
		else if (c == 'e')
		{
			this.customcodec = optarg;
		}
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
