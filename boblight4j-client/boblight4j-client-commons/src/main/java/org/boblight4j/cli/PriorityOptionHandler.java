package org.boblight4j.cli;

import org.boblight4j.exception.BoblightRuntimeException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

public class PriorityOptionHandler extends OptionHandler<Integer> {

	public PriorityOptionHandler(CmdLineParser parser, OptionDef option,
			Setter<? super Integer> setter) {
		super(parser, option, setter);
	}

	@Override
	public int parseArguments(Parameters params) throws CmdLineException {
		Integer priority = Integer.parseInt(params.getParameter(0));
		if (priority == -1 || priority < 0 || priority > 255) {
			throw new BoblightRuntimeException("Wrong option " + priority
					+ " for argument -p");
		}
		this.setter.addValue(priority);

		return 1;
	}

	@Override
	public String getDefaultMetaVariable() {
		return null;
	}

}
