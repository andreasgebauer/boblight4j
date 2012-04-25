package org.boblight4j.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.boblight4j.server.config.ColorConfig;
import org.boblight4j.server.config.Device;
import org.boblight4j.server.config.LightConfig;
import org.boblight4j.utils.MathUtils;

/**
 * Calculates the actual color.
 * 
 * @author agebauer
 * 
 */
public class Light {

	private static final int FULL_SPEED = 100;

	private LightConfig config;

	public boolean interpolation;
	long time = -1;
	long prevtime;
	float[] prevrgb = new float[] { 0, 0, 0 };
	float[] rgb = new float[] { 0, 0, 0 };
	private float speed;
	private float singleChange;
	private boolean use;
	private float autoSpeed;
	private int threshold;

	private List<Device> users = new ArrayList<Device>();

	/**
	 * Constructs a colour calculator.
	 * 
	 * @param interpolated
	 *            whether interpolation should be used
	 */
	public Light(LightConfig light, boolean interpolated) {
		this.config = light;
		interpolation = interpolated;
		this.speed = FULL_SPEED;
	}

	/**
	 * Calculates the initial colour value.
	 * 
	 * @param rgbStart
	 * @return
	 */
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

	/**
	 * Calculates the color value for the color specified.
	 * 
	 * @param colornr
	 *            the color by index
	 * @param time
	 *            the time
	 * @param config
	 * @return the (interpolated) color value
	 */
	public float getColorValue(final int colornr) {

		// need two writes for interpolation
		if (this.interpolation && this.prevtime == -1) {
			return 0.0f;
		}

		float[] calcRgb = this.getInitialColorValue(this.rgb);

		if (calcRgb[0] == 0 && calcRgb[1] == 0 && calcRgb[2] == 0) {
			return 0f;
		}

		float[] maxRgb = this.getMaxRgb(config.getColors());

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
			final ColorConfig cColor = config.getColors().get(i);
			colorvalue = this.findMultiplier(cColor.getRgb(), calcRgb);
			colorvalue = MathUtils.clamp(colorvalue, 0, 1);

			for (int j = 0; j < 3; j++) {
				final float d = cColor.getRgb()[j];
				calcRgb[j] -= d * colorvalue;
			}
		}

		return colorvalue / expandvalue;
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
	 * Sets the given RGB values as colours for this light.
	 * 
	 * @param rgb
	 *            the colour
	 * @param time
	 *            the time stamp the RGB values where grabbed.
	 */
	public final void setRgb(final float[] rgb, final long time) {
		for (int i = 0; i < 3; i++) {
			rgb[i] = MathUtils.clamp(rgb[i], 0.0f, 1.0f);
		}

		this.prevrgb = this.rgb;
		this.rgb = rgb.clone();

		this.prevtime = time;
		this.time = time;
	}

	/**
	 * Returns the maximum RGB colour value to apply.
	 * 
	 * @param colors
	 * @return
	 */
	public float[] getMaxRgb(Collection<ColorConfig> colors) {
		final float maxrgb[] = { 0.0f, 0.0f, 0.0f };
		for (ColorConfig color : colors) {
			for (int j = 0; j < 3; j++) {
				maxrgb[j] += color.getRgb()[j];
			}
		}
		return maxrgb;
	}

	public float getSpeed() {
		return this.speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public void setInterpolation(boolean interpolation) {
		this.interpolation = interpolation;
	}

	public void setUse(boolean use) {
		this.use = use;
	}

	public LightConfig getConfig() {
		return this.config;
	}

	public final Device getUser(final int j) {
		return this.users.get(j);
	}

	public final List<Device> getUsers() {
		return this.users;
	}

	public final int getNrUsers() {
		return this.users.size();
	}

	public final void setSingleChange(final float singlechange) {
		for (int i = 0; i < this.users.size(); i++) {
			this.users.get(i).setSingleChange(
					MathUtils.clamp(singlechange, 0f, 1f));
		}
	}

	/**
	 * Add a device.
	 * 
	 * @param device
	 *            the device to add
	 */
	public void addUser(final Device device) {
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
	public void clearUser(final Device device) {
		this.users.remove(device);
	}

	public long getTime() {
		return this.time;
	}

}