package org.boblight4j.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scanner {

	private static final Pattern INT_PATTERN = Pattern.compile("(\\d+).*");
	private static final Pattern FLOAT_PATTERN = Pattern
			.compile("([-+]?([0-9]*\\.[0-9]+|[0-9]+)).*");

	private String val;

	public Scanner(final String val) {
		this.val = val;
	}

	public final char nextChar() {
		synchronized (this.val)
		{
			final char charAt0 = this.val.charAt(0);
			this.val = this.val.substring(1);
			return charAt0;
		}
	}

	public final float nextFloat() {
		synchronized (this.val)
		{
			final Matcher matcher = FLOAT_PATTERN.matcher(this.val);
			if (matcher.matches())
			{
				final String group2 = matcher.group(1);
				final Float valueOf = Float.valueOf(group2);

				this.val = this.val.substring(group2.length());

				return valueOf;
			}
		}
		return 0;
	}

	public final int nextInt() {
		synchronized (this.val)
		{
			final Matcher matcher = INT_PATTERN.matcher(this.val);
			if (matcher.matches())
			{
				final String group2 = matcher.group(1);
				final Integer valueOf = Integer.valueOf(group2);

				this.val = this.val.substring(group2.length());

				return valueOf;
			}
		}
		return 0;
	}

}
