package org.boblight4j.utils;

import java.util.Arrays;

import org.boblight4j.exception.BoblightParseException;

public final class BooleanParser {

	private static final String[] TRUE = new String[] { "true", "on", "yes" };
	private static final String[] FALSE = new String[] { "false", "off", "no" };

	static
	{
		// sorting is mandatory for binary search
		Arrays.sort(TRUE);
		Arrays.sort(FALSE);
	}

	private BooleanParser() {
	}

	public static final boolean parse(final String optarg)
			throws BoblightParseException {
		String word = optarg.trim();
		// final int firstSpace = word.indexOf(' ');
		// if (firstSpace != -1)
		// {
		// word = word.substring(0, firstSpace);
		// }

		if (Arrays.binarySearch(TRUE, word) >= 0)
		{
			return true;
		}
		else if (Arrays.binarySearch(FALSE, word) >= 0)
		{
			return false;
		}
		// try to parse as integer
		else
		{
			try
			{
				// parse to integer, non-zero is true
				return Integer.valueOf(word) != 0;
			}
			catch (final NumberFormatException e)
			{
				throw new BoblightParseException(
						"Unable to parse boolean value " + word, e);
			}
		}
	}

}
