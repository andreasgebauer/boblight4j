package org.boblight4j.server.config;

import java.awt.geom.Point2D.Float;

import org.boblight4j.utils.MathUtils;

/**
 * A channel represents a single color value.
 * 
 * @author agebauer
 * 
 */
public class Channel {

	private double adjust;
	private Float[] adjusts;

	private double blacklevel;
	private final int color;
	private float currentvalue;
	private final float fallback;
	private double gamma;
	private long lastupdate;
	private final int light;
	/**
	 * The multiplier of the difference of wanted value and current value which
	 * afterwards is added to the current value.
	 */
	private double singlechange;
	private double speed;
	private boolean used;
	private float wantedvalue;

	public Channel(final int color, final int light) {
		this.color = color;
		this.light = light;

		this.used = false;

		this.speed = 100.0;
		this.wantedvalue = 0.0f;
		this.currentvalue = 0.0f;
		this.fallback = 0.0f;
		this.lastupdate = -1;
		this.singlechange = 0.0;

		this.gamma = 1.0;
		this.adjust = 1.0;
		this.blacklevel = 0.0;
	}

	public int getColor() {
		return this.color;
	}

	public int getLight() {
		return this.light;
	}

	/**
	 * Gets the computed value for the specified time.
	 * 
	 * @param time
	 *            the time
	 * @return the computed value for this channel at time time
	 */
	public double getValue(final long time) {
		// we need two calls for the speed
		if (this.lastupdate == -1)
		{
			this.lastupdate = time;
			return this.currentvalue;
		}

		// speed of 100.0 means max
		if (this.speed == 100.0)
		{
			this.currentvalue = this.wantedvalue;
		}
		else
		{
			// difference between where we want to be, and where we are
			final double diff = this.wantedvalue - this.currentvalue;
			// difference in time in microseconds between now and the last
			// update
			final float timediff = time - this.lastupdate;
			// exponential speed, makes the value a lot saner
			// the value is halved for every 15 counts that speed lowers by
			final double speed = Math.pow(2.0, (this.speed - 100.0) / 15.0);

			// speed adjustment value, corrected for time
			final double speedadjust = 1.0 - Math.pow(1.0 - speed,
					timediff / 30.0);

			// move the current value closer to the wanted value
			this.currentvalue += diff * speedadjust;
		}

		// single change
		if (this.singlechange > 0.0)
		{
			this.currentvalue += (this.wantedvalue - this.currentvalue)
					* this.singlechange;
		}
		this.singlechange = 0.0;

		// clamp
		this.currentvalue = MathUtils.clamp(this.currentvalue, 0.0f, 1.0f);

		this.lastupdate = time;

		double outputvalue = this.currentvalue;

		// post processing

		// gamma correction
		if (this.gamma != 1.0)
		{
			outputvalue = Math.pow(outputvalue, this.gamma);
		}
		// adjust correction
		if (this.adjust != 1.0)
		{
			outputvalue *= this.adjust;
		}
		// adjust correction
		if (this.adjusts != null)
		{
			boolean set = false;
			boolean checkLast = false;
			Float float1 = null;
			for (int i = 0; i < this.adjusts.length; i++)
			{
				// nearest low point found
				float1 = this.adjusts[i];
				if (float1.x < outputvalue)
				{
					checkLast = true;
					// be sure to not throw IndexArrayOutOfBoundsException
					if (i + 2 <= this.adjusts.length)
					{
						// do line bla bla
						final Float float2 = this.adjusts[i + 1];
						// m
						final double m = (float1.y - float2.y)
								/ (float1.x - float2.x);

						// y = mx + n
						final double n = float1.y - float1.x * m;

						outputvalue = m * outputvalue + n;
						set = true;
						break;
					}
				}
			}

			if (!set && float1 != null)
			{
				Float float2 = null;
				if (checkLast)
				{
					float2 = new Float(1, 1);
				}
				else
				{
					float2 = new Float(0, 0);
				}
				// m
				final double m = (float1.y - float2.y) / (float1.x - float2.x);

				// y = mx + n
				final double n = float1.y - float1.x * m;

				outputvalue = m * outputvalue + n;
			}

		}
		// blacklevel correction
		if (this.blacklevel != 1.0)
		{
			outputvalue = outputvalue * (1.0 - this.blacklevel)
					+ this.blacklevel;
		}

		return outputvalue;
	}

	public void setAdjust(final double d) {
		this.adjust = d;
	}

	public void setAdjusts(final java.awt.geom.Point2D.Float[] adjusts) {
		this.adjusts = adjusts;
	}

	public void setBlacklevel(final double d) {
		this.blacklevel = d;
	}

	public void setGamma(final double d) {
		this.gamma = d;
	}

	/**
	 * Sets the singlechange if passed argument is greater than current
	 * singlechange.
	 * 
	 * Singlechange is the multiplier of the difference of wanted value and
	 * current value which afterwards is added to the current value.
	 * 
	 * @param singlechange
	 *            the value to set
	 */
	public void setSingleChange(final double singlechange) {

		if (singlechange > this.singlechange)
		{
			this.singlechange = singlechange;
		}
	}

	public void setSpeed(final double speed) {
		this.speed = speed;
	}

	public void setUsed(final boolean b) {
		this.used = b;
	}

	/**
	 * Sets the desired value.
	 * 
	 * @param d
	 *            the desired value
	 */
	public void setValue(final float d) {
		this.wantedvalue = d;
	}

	/**
	 * Sets the wanted value to the fallback value.
	 */
	public void setValueToFallback() {
		this.wantedvalue = this.fallback;
	}

	@Override
	public String toString() {
		return "CChannel [light=" + this.light + ", color=" + this.color
				+ ", wantedvalue=" + this.wantedvalue + ", currentvalue="
				+ this.currentvalue + ", fallback=" + this.fallback + ", used="
				+ this.used + ", speed=" + this.speed + ", singlechange="
				+ this.singlechange + ", gamma=" + this.gamma + ", adjust="
				+ this.adjust + ", blacklevel=" + this.blacklevel
				+ ", lastupdate=" + this.lastupdate + "]";
	}

}
