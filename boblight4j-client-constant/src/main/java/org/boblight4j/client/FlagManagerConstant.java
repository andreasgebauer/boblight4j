package org.boblight4j.client;

import org.boblight4j.exception.BoblightConfigurationException;

class FlagManagerConstant extends AbstractFlagManager {

	private Integer color;

	public Integer getColor() {
		return color;
	}

	@Override
	protected void postGetopt(int optind, String[] argv)
			throws BoblightConfigurationException {
		// check if a color was given
		if (optind == argv.length)
		{
			throw new BoblightConfigurationException("no color given");
		}

		// color = Integer.valueOf(argv[optind], 16);
		//
		// if (color == null)
		// {
		//
		// }

		// if ()
		// throw string("wrong value " + ToString(argv[optind]) + " for color");
	}

	public void printHelpMessage() {
		StringBuilder msg = new StringBuilder();
		msg.append("Usage: boblight-constant [OPTION] color\n");
		msg.append("\n");
		msg.append("  color is in RRGGBB hex notation\n");
		msg.append("\n");
		msg.append("  -p  priority, from 0 to 255, default is 128\n");
		msg.append("  -s  address:[port], set the address and optional port to connect to\n");
		msg.append("  -o  add libboblight option, syntax: [light:]option=value\n");
		msg.append("  -l  list libboblight options\n");
		msg.append("  -f  fork\n");
		msg.append("\n");

		System.out.println(msg.toString());
	}

}
