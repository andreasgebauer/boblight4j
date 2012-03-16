package org.boblight4j.client;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

public class CustomOptionHandler extends OptionHandler<String> {

	public CustomOptionHandler(CmdLineParser parser, OptionDef option,
			Setter<String> setter) {
		super(parser, option, setter);
	}

	@Override
	public int parseArguments(Parameters params) throws CmdLineException {
		if (!setter.isMultiValued())
		{
			throw new CmdLineException(owner, "not multivalued");
		}

		int i = 0;
		for (; i < params.size(); i++)
		{
			final String parameter = params.getParameter(i);
			if (parameter.startsWith("-"))
			{
				break;
			}
			setter.addValue(parameter);
		}
		return i;
	}

	@Override
	public String getDefaultMetaVariable() {
		return null;
	}

}
