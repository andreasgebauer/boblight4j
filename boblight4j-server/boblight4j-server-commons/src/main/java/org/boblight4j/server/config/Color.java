package org.boblight4j.server.config;

/**
 * A color consists of 3 values for red, green and blue, a gamma value, an
 * adjustment value and a black level value. A color can be identified by the
 * name. <br>
 * <br>
 * [color]<br>
 * name red<br>
 * rgb FF0000<br>
 * gamma 1.0f<br>
 * adjust 1.0f<br>
 * blacklevel 0.0f<br>
 * 
 * @author andreas_gebauer
 * 
 */
public class Color {

	private float adjust;

	private float blacklevel;
	private float gamma;
	private String name;
	private float[] rgb = new float[3];

	public Color() {
		this.gamma = 1.0f;
		this.adjust = 1.0f;
		this.blacklevel = 0.0f;
	}

	public float getAdjust() {
		return this.adjust;
	}

	public float getBlacklevel() {
		return this.blacklevel;
	}

	public float getGamma() {
		return this.gamma;
	}

	public String getName() {
		return this.name;
	}

	public float[] getRgb() {
		return this.rgb;
	}

	public void setAdjust(final float adjust) {
		this.adjust = adjust;
	}

	public void setBlacklevel(final float blacklevel) {
		this.blacklevel = blacklevel;
	}

	public void setGamma(final float gamma) {
		this.gamma = gamma;
	}

	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Set the rgb value of this color.
	 * 
	 * @param rgb
	 */
	public void setRgb(final float[] rgb) {
		this.rgb = rgb.clone();
	}

}
