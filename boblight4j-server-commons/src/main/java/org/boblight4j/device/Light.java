package org.boblight4j.device;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.boblight4j.server.config.Color;
import org.boblight4j.utils.MathUtils;
import org.boblight4j.utils.Pair;

/**
 * Runtime class for a light.
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

	private static final Logger LOG = Logger.getLogger(Light.class);

	private final List<ColorLightAdjust> colorLightAdjust = new ArrayList<ColorLightAdjust>();

	private final List<Color> colors = new ArrayList<Color>();
	// 2
	private float hscan[] = new float[2];

	private boolean interpolation;
	private float autoSpeed;
	private float singleChange;

	private int threshold;
	private String name;

	// 3
	private float prevrgb[] = new float[3];
	private long prevtime; // previous write time

	// 3
	private float rgb[] = new float[3];
	private float speed;

	// float FindMultiplier(float *rgb, float ceiling);
	// float FindMultiplier(float *rgb, float *ceiling);

	private long time; // current write time

	private boolean use;

	// device using this light
	private final List<Pair<Device, Float>> users = new ArrayList<Pair<Device, Float>>();

	// 2
	private float vscan[] = new float[2];

	public Light() {
		this.time = -1;
		this.prevtime = -1;

		this.speed = 100;
		this.use = true;
		this.interpolation = false;

		this.hscan[0] = 0.0f;
		this.hscan[1] = 100.0f;
		this.vscan[0] = 0.0f;
		this.vscan[1] = 100.0f;
	}

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
	public void addUser(final Device device) {
		// add CDevice pointer to users if it doesn't exist yet
		for (int i = 0; i < this.users.size(); i++)
		{
			if (this.users.get(i).getKey().equals(device))
			{
				return;
			}
		}
		// set single change
		this.users.add(new Pair<Device, Float>(device, (float) 0.0));
	}

	public void clearUser(final Device device) {
		// clear CDevice* from users
		final Iterator<Pair<Device, Float>> it = this.users.iterator();
		while (it.hasNext())
		{
			final Pair<Device, Float> cur = it.next();
			if (cur.getKey().equals(device))
			{
				this.users.remove(cur);
				return;
			}
		}
	}

	private float findMultiplier(final float[] fs, final float rgb2) {
		float multiplier = Float.MAX_VALUE;

		for (int i = 0; i < 3; i++)
		{
			if (fs[i] > 0.0 && rgb2 / fs[i] < multiplier)
			{
				multiplier = (rgb2 / fs[i]);
			}
		}
		return multiplier;
	}

	private float findMultiplier(final float[] rgb, final float[] ceiling) {
		float multiplier = Float.MAX_VALUE;

		for (int i = 0; i < 3; i++)
		{
			if (rgb[i] > 0.0 && ceiling[i] / rgb[i] < multiplier)
			{
				multiplier = ceiling[i] / rgb[i];
			}
		}
		return multiplier;
	}

	public float getAdjust(final int colornr) {
		if (this.colorLightAdjust.get(colornr).getAdjust() != 1.0)
		{
			return this.colorLightAdjust.get(colornr).getAdjust();
		}
		return this.colors.get(colornr).getAdjust();
	}

	public Point2D.Float[] getAdjusts(final int color) {
		return this.colorLightAdjust.get(color).getAdjusts();
	}

	public float getBlacklevel(final int colornr) {
		return this.colors.get(colornr).getBlacklevel();
	}

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
	 * @return
	 */
	public float getColorValue(final int colornr, final long time) {
		// need two writes for interpolation
		if (this.interpolation && this.prevtime == -1)
		{
			return 0.0f;
		}

		float rgb[];
		final float[] rgbCur = Arrays.copyOf(this.rgb, this.rgb.length);
		if (this.interpolation)
		{
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
			for (int i = 0; i < 3; i++)
			{
				final float[] rgbPrev = this.prevrgb;
				final double diff = rgbCur[i] - rgbPrev[i];
				rgb[i] = (float) (rgbPrev[i] + diff * multiply);
			}
		}
		else
		{
			rgb = Arrays.copyOf(rgbCur, rgbCur.length);
		}

		if (rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 0)
		{
			return 0f;
		}

		final float maxrgb[] = { 0.0f, 0.0f, 0.0f };
		for (int i = 0; i < this.colors.size(); i++)
		{
			for (int j = 0; j < 3; j++)
			{
				final Color color = this.colors.get(i);
				maxrgb[j] += color.getRgb()[j];
			}
		}

		final float expandvalue = this.findMultiplier(rgb, 1);
		for (int i = 0; i < 3; i++)
		{
			rgb[i] *= expandvalue;
		}

		final float range = this.findMultiplier(rgb, maxrgb);
		for (int i = 0; i < 3; i++)
		{
			rgb[i] *= range;
		}

		float colorvalue = 0;
		for (int i = 0; i <= colornr; i++)
		{
			final Color cColor = this.colors.get(i);
			colorvalue = this.findMultiplier(cColor.getRgb(), rgb);
			colorvalue = MathUtils.clamp(colorvalue, 0, 1);

			for (int j = 0; j < 3; j++)
			{
				final float d = cColor.getRgb()[j];
				rgb[j] -= d * colorvalue;
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
	public final float getSingleChange(final Device device) {
		for (int i = 0; i < this.users.size(); i++)
		{
			Pair<Device, Float> dev = this.users.get(i);
			if (dev.getKey().equals(device))
			{
				return dev.getValue();
			}
		}
		return 0;
	}

	public final float getSpeed() {
		return this.speed;
	}

	public final long getTime() {
		return this.time;
	}

	public final Pair<Device, Float> getUser(final int j) {
		return this.users.get(j);
	}

	public final List<Pair<Device, Float>> getUsers() {
		return this.users;
	}

	public final float[] getVscan() {
		return this.vscan;
	}

	public final boolean isInterpolation() {
		return this.interpolation;
	}

	public final boolean isUse() {
		return this.use;
	}

	/**
	 * Sets the single change to 0 for the specified device.
	 * 
	 * @param device
	 */
	public final void resetSingleChange(final Device device) {
		for (int i = 0; i < this.users.size(); i++)
		{
			if (this.users.get(i).getKey().equals(device))
			{
				this.users.get(i).setValue(0.0f);
				return;
			}
		}
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
		this.hscan = hscan.clone();
	}

	public final void setInterpolation(final boolean interpolation) {
		LOG.info("setting interpolation to " + interpolation);
		this.interpolation = interpolation;
	}

	public final void setName(final String name) {
		this.name = name;
	}

	public final void setRgb(final float[] rgb, final long time) {
		for (int i = 0; i < 3; i++)
		{
			rgb[i] = MathUtils.clamp(rgb[i], 0.0f, 1.0f);
		}

		this.prevrgb = this.rgb;
		this.rgb = rgb.clone();

		this.prevtime = this.time;
		this.time = time;
	}

	public final void setSingleChange(final float singlechange) {
		for (int i = 0; i < this.users.size(); i++)
		{
			this.users.get(i).setValue(MathUtils.clamp(singlechange, 0f, 1f));
		}
	}

	public final void setSpeed(final float speed) {
		LOG.info("setting speed to " + speed);
		this.speed = speed;
	}

	public final void setTime(final long time) {
		this.time = time;
	}

	public final void setUse(final boolean use) {
		this.use = use;
	}

	public final void setVscan(final float[] vscan) {
		this.vscan = vscan.clone();
	}

	@Override
	public final String toString() {
		return "CLight [name=" + this.name + ", time=" + this.time + ", rgb="
				+ Arrays.toString(this.rgb) + ", speed=" + this.speed
				+ ", interpolation=" + this.interpolation + ", use=" + this.use
				+ ", colors=" + this.colors + ", hscan="
				+ Arrays.toString(this.hscan) + ", vscan="
				+ Arrays.toString(this.vscan) + ", users=" + this.users
				+ ", m_threshold=" + this.threshold + ", m_autospeed="
				+ this.autoSpeed + ", m_singlechange=" + this.singleChange
				+ "]";
	}

}
