package de.gebauer.boblight4j.xbmc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RGBValue {

	private static final Pattern pattern = Pattern
			.compile("(\\d+),(\\d+)\\:(\\d+),(\\d+),(\\d+)");

	public static RGBValue parse(String string) {
		final Matcher matcher = pattern.matcher(string);
		if (matcher.matches())
		{
			final String xPos = matcher.group(1);
			final String yPos = matcher.group(2);
			final String r = matcher.group(3);
			final String g = matcher.group(4);
			final String b = matcher.group(5);
			return new RGBValue(Integer.parseInt(xPos), Integer.parseInt(yPos),
					new int[] { Integer.parseInt(r), Integer.parseInt(g),
							Integer.parseInt(b) });
		}
		return null;
	}

	public RGBValue(final int xPos, final int yPos, final int[] rgb) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.rgb = rgb;
	}

	public final int xPos;
	public final int yPos;
	public final int[] rgb;
}
