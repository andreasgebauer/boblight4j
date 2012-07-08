package org.boblight4j.server;

import org.boblight4j.server.config.ColorConfig;

public class ColorUtils {

	public static ColorConfig blue() {
		final ColorConfig color = new ColorConfig("blue");
		color.setRgb(new float[] { 0, 0, 1 });
		return color;
	}

	public static ColorConfig green() {
		final ColorConfig color = new ColorConfig("green");
		color.setRgb(new float[] { 0, 1, 0 });
		return color;
	}

	public static ColorConfig red() {
		final ColorConfig color = new ColorConfig("red");
		color.setRgb(new float[] { 1, 0, 0 });
		return color;
	}

}
