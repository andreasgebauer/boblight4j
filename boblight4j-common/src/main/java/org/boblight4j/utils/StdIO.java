package org.boblight4j.utils;

import java.util.ArrayList;
import java.util.List;

import org.boblight4j.exception.BoblightParseException;

public final class StdIO {

	private StdIO() {
	}

	public static final Object[] sscanf(final String value, final String pattern)
			throws BoblightParseException {
		final List<Object> parsed = new ArrayList<Object>();

		String pat = pattern;
		final String val = value;

		final Scanner scanner = new Scanner(val);
		while (!pat.isEmpty())
		{
			if (pat.charAt(0) == '%')
			{
				final char charAt = pat.charAt(1);
				switch (charAt)
				{
					case 'i':
						parsed.add(scanner.nextInt());
						pat = pat.substring(2);
						break;
					case 'f':
						parsed.add(scanner.nextFloat());
						pat = pat.substring(2);
						break;
					default:
						throw new BoblightParseException(
								"Unable to parse given format '" + charAt + "'");
				}
			}
			else
			{
				scanner.nextChar();
				pat = pat.substring(1);
			}
		}
		return parsed.toArray();
	}

}
