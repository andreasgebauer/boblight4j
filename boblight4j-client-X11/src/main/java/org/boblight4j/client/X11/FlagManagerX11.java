package org.boblight4j.client.X11;

import org.boblight4j.client.AbstractFlagManager;
import org.boblight4j.client.AbstractFlagManager.CommandLineArgs;
import org.boblight4j.client.X11.FlagManagerX11.X11Flags;
import org.boblight4j.exception.BoblightConfigurationException;
import org.kohsuke.args4j.Option;

/**
 * clients/boblight-X11/flagmanager-X11.cpp
 * 
 * @author agebauer
 * @param <X11Flags>
 * 
 */
public class FlagManagerX11 extends AbstractFlagManager<X11Flags> {

	public class X11Flags extends CommandLineArgs {
		@Option(name = "-i")
		String interval;

		@Option(name = "-u")
		int pixels;

		@Option(name = "-x")
		boolean useXRender;

		@Option(name = "-d")
		boolean debug;

		@Option(name = "-d")
		String debugDisplay;
	}

	public boolean debug;
	public String debugdpy;
	public double interval;
	int method;
	int pixels;

	public FlagManagerX11() {
		// extend the base getopt flags
		// i = interval, u = pixels, x = xgetimage, d = debug
		this.addFlags("i:u:xd::");

		this.interval = 0.1; // default interval is 100 milliseconds
		this.pixels = -1; // -1 says to the capture classes to use default
		this.method = BoblightX11.XRENDER; // default method is fast xrender
		this.debug = false; // no debugging by default
		this.debugdpy = null; // default debug dpy is system default
		this.setSync(true); // sync mode enabled by default
	}

	@Override
	protected void parseFlagsExtended(final X11Flags argv, final int c,
			final String optarg) throws BoblightConfigurationException {

		// starting interval with v means vblank interval
		if (argv.interval.charAt(0) == 'v')
		{
			throw new BoblightConfigurationException(
					"Compiled without opengl support");
		}

		this.interval = Float.parseFloat(argv.interval);
		if (this.interval <= 0.0f)
		{
			throw new BoblightConfigurationException("Wrong value " + optarg
					+ " for interval");
		}

		this.pixels = argv.pixels;
		if (this.pixels <= 0)
		{
			throw new BoblightConfigurationException("Wrong value " + optarg
					+ " for pixels");
		}

		this.method = argv.useXRender ? this.method = BoblightX11.XGETIMAGE
				: BoblightX11.XRENDER;

		this.debug = argv.debug;

		this.debugdpy = argv.debugDisplay;

		// if (c == 'i') // interval
		// {
		// final boolean vblank = false;
		// // starting interval with v means vblank interval
		// if (optarg.charAt(0) == 'v')
		// {
		// // #ifdef HAVE_LIBGL
		// // optarg++;
		// // vblank = true;
		// // #else
		// throw new BoblightConfigurationException(
		// "Compiled without opengl support");
		// // #endif
		// }
		//
		// this.interval = Float.parseFloat(optarg);
		// if (this.interval <= 0.0f)
		// {
		// throw new BoblightConfigurationException("Wrong value "
		// + optarg + " for interval");
		// }
		//
		// if (vblank)
		// {
		// if (this.interval < 1.0)
		// {
		// throw new BoblightConfigurationException("Wrong value "
		// + optarg + " for vblank interval");
		// }
		// // negative interval means vblank optarg--;
		// this.interval *= -1.0;
		// }
		// }
		// else if (c == 'u') // nr of pixels to use
		// {
		// this.pixels = Integer.valueOf(optarg);
		// if (this.pixels <= 0)
		// {
		// throw new BoblightConfigurationException("Wrong value "
		// + optarg + " for pixels");
		// }
		// }
		// else if (c == 'x') // use crap xgetimage instead of sleek xrender
		// {
		// this.method = BoblightX11.XGETIMAGE;
		// }
		// else if (c == 'd') // turn on debug mode
		// {
		// this.debug = true;
		// if (optarg != null && optarg.length() > 0) // optional debug dpy
		// {
		// this.debugdpy = optarg;
		// }
		// }
	}

	@Override
	public void printHelpMessage() {
		StringBuilder msg = new StringBuilder();

		msg.append("Usage: boblight-X11 [OPTION]\n");
		msg.append("\n");
		msg.append("  options:\n");
		msg.append("\n");
		msg.append("  -p  priority, from 0 to 255, default is 128\n");
		msg.append("  -s  address:[port], set the address and optional port to connect to\n");
		msg.append("  -o  add libboblight option, syntax: [light:]option=value\n");
		msg.append("  -l  list libboblight options\n");
		msg.append("  -i  set the interval in seconds, default is 0.1\n");
		// #ifdef HAVE_LIBGL
		// msg.append("      prefix the value with v to wait for a number of vertical blanks instead\n");
		// #endif
		msg.append("  -u  set the number of pixels/rows to use\n");
		msg.append("      default is 64 for xrender and 16 for xgetimage\n");
		msg.append("  -x  use XGetImage instead of XRender\n");
		msg.append("  -d  debug mode\n");
		msg.append("  -f  fork\n");
		msg.append("  -y  set the sync mode, default is on, valid options are \"on\" and \"off\"\n");
		msg.append("\n");

		System.out.print(msg.toString());
	}

	@Override
	protected X11Flags getArgBean() {
		return new X11Flags();
	}

}
