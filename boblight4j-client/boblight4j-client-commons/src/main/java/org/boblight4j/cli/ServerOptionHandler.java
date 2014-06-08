package org.boblight4j.cli;

import org.boblight4j.client.CommandLineArgs.Server;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

public class ServerOptionHandler extends OptionHandler<Server> {

	public ServerOptionHandler(CmdLineParser parser, OptionDef option,
			Setter<? super Server> setter) {
		super(parser, option, setter);
	}

	@Override
	public int parseArguments(Parameters params) throws CmdLineException {
		this.setter.addValue(new Server(params.getParameter(0)));
		return 1;
	}

	@Override
	public String getDefaultMetaVariable() {
		return null;
	}

}