package org.boblight4j.server.config;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class representing a light section in the configuration.<br>
 * <br>
 * [light]<br>
 * name bottom1<br>
 * color red arduino 1<br>
 * color green arduino 2<br>
 * color blue arduino 3<br>
 * vscan 0 20 <br>
 * hscan 85 10
 * 
 * @author agebauer
 * 
 */
public class LightConfig {

	private static final Logger LOG = LoggerFactory
			.getLogger(LightConfig.class);

	private String name;
	private final List<ColorLightAdjust> colorLightAdjust = new ArrayList<ColorLightAdjust>();
	private final List<ColorConfig> colors = new ArrayList<ColorConfig>();
	private final float hscan[] = new float[2];

	// float FindMultiplier(float *rgb, float ceiling);
	// float FindMultiplier(float *rgb, float *ceiling);

	private boolean use;

	// 2
	private float vscan[] = new float[2];

	/**
	 * Constructs a light.
	 * 
	 * @param lightName
	 */
	public LightConfig(String lightName) {
		this.name = lightName;
		this.use = true;

		this.hscan[0] = 0.0f;
		this.hscan[1] = 100.0f;
		this.vscan[0] = 0.0f;
		this.vscan[1] = 100.0f;
	}

	public final String getName() {
		return this.name;
	}

	/**
	 * Adds a color to this light.
	 * 
	 * @param color
	 *            the color to add
	 */
	public void addColor(final ColorConfig color) {
		this.colors.add(color);
		this.colorLightAdjust.add(new ColorLightAdjust());
	}

	/**
	 * Returns the adjustment value for the color given.
	 * 
	 * @param colornr
	 *            the zero-based index of the color to get the adjustment value
	 *            for
	 * @return the adjustment value of the color given for this light
	 */
	public float getAdjust(final int colornr) {
		if (this.colorLightAdjust.get(colornr).getAdjust() != 1.0) {
			return this.colorLightAdjust.get(colornr).getAdjust();
		}
		return this.colors.get(colornr).getAdjust();
	}

	/**
	 * Returns adjustment values for all colors defined for this light.
	 * 
	 * @param color
	 *            the zero-based index of the color
	 * @return the adjustment values for each color
	 */
	public Point2D.Float[] getAdjusts(final int color) {
		return this.colorLightAdjust.get(color).getAdjusts();
	}

	/**
	 * Returns the black level for the given zero-based index of the color.
	 * 
	 * @param colornr
	 *            the zero-based index of the color you want to get the black
	 *            level for
	 * @return the black level
	 */
	public float getBlacklevel(final int colornr) {
		return this.colors.get(colornr).getBlacklevel();
	}

	/**
	 * Returns all the colors used in this light.
	 * 
	 * @return all defined colors
	 */
	public List<ColorConfig> getColors() {
		return this.colors;
	}

	public final float getGamma(final int colorNr) {
		return this.colors.get(colorNr).getGamma();
	}

	public final float[] getHscan() {
		return this.hscan;
	}

	public final int getNrColors() {
		return this.colors.size();
	}

	public final float[] getVscan() {
		return this.vscan;
	}

	public final boolean isUse() {
		return this.use;
	}

	public final void setAdjust(final int colorNr, final float adjust) {
		this.colorLightAdjust.get(colorNr).setAdjust(adjust);
	}

	public final void setAdjusts(final int colorNr, final Point2D.Float[] array) {
		this.colorLightAdjust.get(colorNr).setAdjusts(array);
	}

	public final void setColor(final int i, final ColorConfig color) {
		this.colors.set(i, color);
	}

	public final void setHscan(final float[] hscan) {
		this.hscan[0] = hscan[0];
		this.hscan[1] = hscan[1];
	}

	public final void setUse(final boolean use) {
		this.use = use;
	}

	public final void setVscan(final float[] vscan) {
		this.vscan = vscan.clone();
	}

	@Override
	public final String toString() {
		return "Light [name=" + this.name + ", colors=" + this.colors
				+ ", hscan=" + Arrays.toString(this.hscan) + ", vscan="
				+ Arrays.toString(this.vscan) + "]";
	}

}
