package org.boblight4j.utils;

import org.boblight4j.exception.BoblightParseException;

public class Misc {

	/**
	 * get the first word (separated by whitespace) from string data and place
	 * that in word then remove that word from string data
	 * 
	 * @throws BoblightParseException
	 */
	public static String getWord(final Pointer<String> line)
			throws BoblightParseException {
		final String ln = line.get();
		if (ln.isEmpty())
		{
			throw new BoblightParseException("line is empty");
		}

		for (int i = 0; i < ln.length(); i++)
		{
			switch (ln.charAt(i))
			{
				case '\n':
				case '\t':
				case ' ':
					line.assign(ln.substring(i + 1).trim());
					return ln.substring(0, i);
				default:
					continue;
			}
		}

		line.assign("");
		return ln.trim();
	}

	/**
	 * Reads the next word from line.
	 * 
	 * @param line
	 *            the line to get a word from
	 * @param parsedWord
	 *            the word read
	 * @return false if line is empty
	 * @throws BoblightParseException
	 */
	public static String getWord(final StringBuilder line)
			throws BoblightParseException {
		final String ln = line.toString();
		if (ln.isEmpty())
		{
			throw new BoblightParseException("line is empty");
		}

		for (int i = 0; i < ln.length(); i++)
		{
			switch (ln.charAt(i))
			{
				case '\n':
				case '\t':
				case ' ':
					line.replace(0, line.length(), ln.substring(i + 1).trim());
					return ln.substring(0, i);
				default:
					continue;
			}
		}

		line.delete(0, line.length());
		return ln.trim();
	}
}
