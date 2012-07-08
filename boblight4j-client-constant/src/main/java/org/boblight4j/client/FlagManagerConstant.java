package org.boblight4j.client;

import org.boblight4j.client.FlagManagerConstant.ConstantClientArgs;
import org.boblight4j.exception.BoblightConfigurationException;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineParser;

class FlagManagerConstant extends AbstractFlagManager<ConstantClientArgs> {

	public class ConstantClientArgs extends CommandLineArgs {

		@Argument(multiValued = false, usage = "color in RRGGBB hex notation", metaVar = "COLOR", required = true)
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

	public void printHelpMessage() {
		System.out.println("Usage: boblight-constant [OPTION] COLOR");
		System.out.println("");
		System.out.println("[OPTION]:");
		System.out.println("");

		final CmdLineParser parser = new CmdLineParser(getArgBean());
		parser.printUsage(System.out);
	}

}
