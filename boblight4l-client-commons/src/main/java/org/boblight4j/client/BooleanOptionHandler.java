package org.boblight4j.client;

import org.boblight4j.exception.BoblightParseException;
import org.boblight4j.utils.BooleanParser;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

public class BooleanOptionHandler extends OptionHandler<Boolean> {

	public BooleanOptionHandler(CmdLineParser parser, OptionDef option,
			Setter<? super Boolean> setter) {
		super(parser, option, setter);
	}

	@Override
	public int parseArguments(Parameters params) throws CmdLineException {
		try
		{
			setter.addValue(BooleanParser.parse(params.getParameter(0)));
		}
		catch (BoblightParseException e)
		{
			throw new CmdLineException(owner, e);
		}
		return 1;
	}

	@Override
	public String getDefaultMetaVariable() {
		return "List<String>";
	}

}
