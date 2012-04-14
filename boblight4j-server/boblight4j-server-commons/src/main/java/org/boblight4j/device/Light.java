package org.boblight4j.device;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.boblight4j.server.config.Color;
import org.boblight4j.utils.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class representing a light section in the configuration.
 * 
 * <br>
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
public class Light {

	public class ColorCalculator {
		private boolean interpolation;
		private long time = -1;
		private long prevtime;
		private float[] prevrgb;

		public ColorCalculator(boolean interpolated) {
			interpolation = interpolated;
		}

		public float[] getInitialColorValue(float[] rgbStart) {
			float rgb[];
			final float[] rgbCur = Arrays.copyOf(rgbStart, rgbStart.length);
			if (this.interpolation) {
				rgb = new float[3];

				final long timeRecv = this.time;
				final long timePrev = this.prevtime;

				float multiply = 0f;
				final long timeDiff = timeRecv - timePrev;
				if (timeDiff > 0) // don't want to
									// divide by 0
				{
					multiply = (time - timeRecv) / (float) timeDiff;
				}
				multiply = MathUtils.clamp(multiply, 0f, 1f);
				for (int i = 0; i < 3; i++) {
					final float[] rgbPrev = this.prevrgb;
					final double diff = rgbCur[i] - rgbPrev[i];
					rgb[i] = (float) (rgbPrev[i] + diff * multiply);
				}
			} else {
				rgb = Arrays.copyOf(rgbCur, rgbCur.length);
			}

			return rgb;
		}

		public float[] getMaxRgb(List<Color> colors) {
			final float maxrgb[] = { 0.0f, 0.0f, 0.0f };
			for (int i = 0; i < colors.size(); i++) {
				for (int j = 0; j < 3; j++) {
					final Color color = colors.get(i);
					maxrgb[j] += color.getRgb()[j];
				}
			}
			return maxrgb;
		}

	}

	private static final int FULL_SPEED = 100;

	private static final Logger LOG = LoggerFactory.getLogger(Light.class);

	private final List<ColorLightAdjust> colorLightAdjust = new ArrayList<ColorLightAdjust>();
	private final List<Color> colors = new ArrayList<Color>();
	private final float hscan[] = new float[2];

	private float autoSpeed;
	private float singleChange;

	private int threshold;
	private String name;

	// 3
	private float rgb[] = new float[3];
	private float speed;

	// float FindMultiplier(float *rgb, float ceiling);
	// float FindMultiplier(float *rgb, float *ceiling);

	private boolean use;

	// device using this light
	private final List<AbstractDevice> users = new ArrayList<AbstractDevice>();

	// 2
	private float vscan[] = new float[2];

	private ColorCalculator colorCalculator = new ColorCalculator(false);

	/**
	 * Constructs a light.
	 */
	public Light() {

		this.speed = FULL_SPEED;
		this.use = true;

		this.hscan[0] = 0.0f;
		this.hscan[1] = 100.0f;
		this.vscan[0] = 0.0f;
		this.vscan[1] = 100.0f;
	}

	/**
	 * Adds a color to this light.
	 * 
	 * @param color
	 *            the color to add
	 */
	public void addColor(final Color color) {
		this.colors.add(color);
		this.colorLightAdjust.add(new ColorLightAdjust());
	}

	/**
	 * Add a device.
	 * 
	 * @param device
	 *            the device to add
	 */
	public void addUser(final AbstractDevice device) {
		// add CDevice pointer to users if it doesn't exist yet
		if (this.users.contains(device)) {
			return;
		}
		this.users.add(device);
	}

	/**
	 * Removes a device.
	 * 
	 * @param device
	 *            the device to remove
	 */
	public void clearUser(final AbstractDevice device) {
		this.users.remove(device);
	}

	private float findMultiplier(final float[] fs, final float rgb2) {
		float multiplier = Float.MAX_VALUE;

		for (int i = 0; i < 3; i++) {
			if (fs[i] > 0.0 && rgb2 / fs[i] < multiplier) {
				multiplier = (rgb2 / fs[i]);
			}
		}
		return multiplier;
	}

	private float findMultiplier(final float[] rgb, final float[] ceiling) {
		float multiplier = Float.MAX_VALUE;

		for (int i = 0; i < 3; i++) {
			if (rgb[i] > 0.0 && ceiling[i] / rgb[i] < multiplier) {
				multiplier = ceiling[i] / rgb[i];
			}
		}
		return multiplier;
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
	public Collection<Color> getColors() {
		return this.colors;
	}

	/**
	 * Calculates the color value for the color specified.
	 * 
	 * @param colornr
	 *            the color by index
	 * @param time
	 *            the time
	 * @return the (interpolated) color value
	 */
	public float getColorValue(final int colornr, final long time) {

		// need two writes for interpolation
		if (this.colorCalculator.interpolation && this.colorCalculator.prevtime == -1) {
			return 0.0f;
		}

		float[] calcRgb = this.colorCalculator.getInitialColorValue(this.rgb);

		if (calcRgb[0] == 0 && calcRgb[1] == 0 && calcRgb[2] == 0) {
			return 0f;
		}

		float[] maxRgb = this.colorCalculator.getMaxRgb(this.colors);

		final float expandvalue = this.findMultiplier(calcRgb, 1);
		for (int i = 0; i < 3; i++) {
			calcRgb[i] *= expandvalue;
		}

		final float range = this.findMultiplier(calcRgb, maxRgb);
		for (int i = 0; i < 3; i++) {
			calcRgb[i] *= range;
		}

		float colorvalue = 0;
		for (int i = 0; i <= colornr; i++) {
			final Color cColor = this.colors.get(i);
			colorvalue = this.findMultiplier(cColor.getRgb(), calcRgb);
			colorvalue = MathUtils.clamp(colorvalue, 0, 1);

			for (int j = 0; j < 3; j++) {
				final float d = cColor.getRgb()[j];
				calcRgb[j] -= d * colorvalue;
			}
		}

		return colorvalue / expandvalue;
	}

	public final float getGamma(final int colorNr) {
		return this.colors.get(colorNr).getGamma();
	}

	public final float[] getHscan() {
		return this.hscan;
	}

	public final String getName() {
		return this.name;
	}

	public final int getNrColors() {
		return this.colors.size();
	}

	public final int getNrUsers() {
		return this.users.size();
	}

	public final float[] getRgb() {
		return this.rgb;
	}

	// colors

	/**
	 * Returns the single change. Which means the multiplier of the difference
	 * of wanted value and current value which afterwards is added to the
	 * current value.
	 * 
	 * @param device
	 * @return
	 */
	public final float getSingleChange(final AbstractDevice device) {
		return device.getSingleChange();
	}

	public final float getSpeed() {
		return this.speed;
	}

	public final long getTime() {
		return this.colorCalculator.time;
	}

	public final AbstractDevice getUser(final int j) {
		return this.users.get(j);
	}

	public final List<AbstractDevice> getUsers() {
		return this.users;
	}

	public final float[] getVscan() {
		return this.vscan;
	}

	public final boolean isUse() {
		return this.use;
	}

	/**
	 * Sets the single change to 0 for the specified device.
	 * 
	 * @param device
	 */
	public final void resetSingleChange(final AbstractDevice device) {
		device.setSingleChange(0.0f);
	}

	public final void setAdjust(final int colorNr, final float adjust) {
		this.colorLightAdjust.get(colorNr).setAdjust(adjust);
	}

	public final void setAdjusts(final int colorNr, final Point2D.Float[] array) {
		this.colorLightAdjust.get(colorNr).setAdjusts(array);
	}

	public final void setColor(final int i, final Color color) {
		this.colors.set(i, color);
	}

	public final void setHscan(final float[] hscan) {
		this.hscan[0] = hscan[0];
		this.hscan[1] = hscan[1];
	}

	public final void setInterpolation(final boolean interpolation) {
		LOG.info("setting interpolation to " + interpolation);
		this.colorCalculator.interpolation = interpolation;
	}

	public final void setName(final String name) {
		this.name = name;
	}

	/**
	 * Sets the given rgb values as colors for this light.
	 * 
	 * @param rgb
	 *            the color
	 * @param time
	 *            the time stamp the RGB values where grabbed.
	 */
	public final void setRgb(final float[] rgb, final long time) {
		for (int i = 0; i < 3; i++) {
			rgb[i] = MathUtils.clamp(rgb[i], 0.0f, 1.0f);
		}

		this.colorCalculator.prevrgb = this.rgb;
		this.rgb = rgb.clone();

		this.colorCalculator.prevtime = colorCalculator.time;
		this.colorCalculator.time = time;
	}

	public final void setSingleChange(final float singlechange) {
		for (int i = 0; i < this.users.size(); i++) {
			this.users.get(i).setSingleChange(
					MathUtils.clamp(singlechange, 0f, 1f));
		}
	}

	public final void setSpeed(final float speed) {
		LOG.info("setting speed to " + speed);
		this.speed = speed;
	}

	public final void setUse(final boolean use) {
		this.use = use;
	}

	public final void setVscan(final float[] vscan) {
		this.vscan = vscan.clone();
	}

	@Override
	public final String toString() {
		return "Light [name=" + this.name + ", rgb="
				+ Arrays.toString(this.rgb) + ", speed=" + this.speed
				+ ", interpolation=" + this.colorCalculator.interpolation
				+ ", use=" + this.use + ", colors=" + this.colors + ", hscan="
				+ Arrays.toString(this.hscan) + ", vscan="
				+ Arrays.toString(this.vscan) + ", users=" + this.users
				+ ", m_threshold=" + this.threshold + ", m_autospeed="
				+ this.autoSpeed + ", m_singlechange=" + this.singleChange
				+ "]";
	}

}
