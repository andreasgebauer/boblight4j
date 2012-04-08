package org.boblight4j.client;

import java.util.List;

import org.boblight4j.client.FlagManagerConstant.ConstantClientArgs;
import org.boblight4j.exception.BoblightConfigurationException;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.spi.OptionHandler;

class FlagManagerConstant extends AbstractFlagManager<ConstantClientArgs> {

	public class ConstantClientArgs extends CommandLineArgs {

		@Argument(multiValued = false, usage = "color in RRGGBB hex notation", required = true)
		String color;
	}

	private Integer color;

	public Integer getColor() {
		return color;
	}

	@Override
	protected ConstantClientArgs getArgBean() {
		return new ConstantClientArgs();
	}

	@Override
	public ConstantClientArgs parseFlags(String[] args)
			throws BoblightConfigurationException {
		final ConstantClientArgs argBean = super.parseFlags(args);
		this.color = Integer.valueOf(argBean.color, 16);
		return argBean;
	}

	@Override
	protected void postGetopt(final CmdLineParser parser)
			throws BoblightConfigurationException {

		final List<OptionHandler> args = parser.getArguments();
		if (args.size() > 0)
		{
			final OptionHandler optionHandler = args.get(args.size() - 1);
		}

		final int size = args.size();
		// check if a color was given
		// if (optind == argv.length)
		// {
		// throw new BoblightConfigurationException("no color given");
		// }

		// color = Integer.valueOf(argv[optind], 16);
		//
		// if (color == null)
		// {
		//
		// }

		// if ()
		// throw string("wrong value " + ToString(argv[optind]) + " for color");
	}

	public void printHelpMessage(final CmdLineParser parser) {

		parser.printUsage(System.out);
		// StringBuilder msg = new StringBuilder();
		// msg.append("Usage: boblight-constant [OPTION] color\n");
		// msg.append("\n");
		// msg.append("  color is in RRGGBB hex notation\n");
		// msg.append("\n");
		// msg.append("  -p  priority, from 0 to 255, default is 128\n");
		// msg.append("  -s  address:[port], set the address and optional port to connect to\n");
		// msg.append("  -o  add libboblight option, syntax: [light:]option=value\n");
		// msg.append("  -l  list libboblight options\n");
		// msg.append("  -f  fork\n");
		// msg.append("\n");

		// System.out.println(msg.toString());
	}

}
