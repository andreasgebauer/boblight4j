package org.boblight4j.server;

import org.boblight4j.server.config.Color;

public class ColorUtils {

	public static Color blue() {
		final Color color = new Color();
		color.setName("bblue");
		color.setRgb(new float[] { 0, 0, 1 });
		return color;
	}

	public static Color green() {
		final Color color = new Color();
		color.setName("green");
		color.setRgb(new float[] { 0, 1, 0 });
		return color;
	}

	public static Color red() {
		final Color color = new Color();
		color.setName("red");
		color.setRgb(new float[] { 1, 0, 0 });
		return color;
	}

}
