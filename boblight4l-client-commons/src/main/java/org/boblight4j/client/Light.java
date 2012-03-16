package org.boblight4j.client;

import java.util.Arrays;

import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightParseException;
import org.boblight4j.utils.MathUtils;
import org.boblight4j.utils.Misc;
import org.boblight4j.utils.Pointer;

/**
 * boblight-client.cpp CLight
 * 
 * @author agebauer
 * 
 */
public class Light extends LightConfig {

	private static final float CHANGE_LIMIT = 0.001f;
	private static final float HSV_DEGREES_BLUE = 240.0f;
	private static final float HSV_DEGREES_GREEN = 120.0f;
	private static final float HSV_DEGREES_RED = 360.0f;

	private static final double HUNDRET = 100.0;

	private final LightConfigMBean globalConfig;

	private String name;

	private int width;
	private int height;

	// preset
	private final float hscan[] = new float[2];
	private final float vscan[] = new float[2];

	// runtime calculated //
	public int rgb[] = new int[4];
	private float prevrgb[] = new float[3];

	public Light(final LightConfigMBean globalConfig) {

		this.globalConfig = globalConfig;

		// set default values;
		for (final BoblightOptions opt : BoblightOptions.values())
		{
			opt.doPostProcess(this, opt.getDefault());
		}

		this.setSingleChange(0.0f);

		this.width = -1;
		this.height = -1;
	}

	/**
	 * Sets the scaled scan range.
	 * 
	 * @param width
	 * @param height
	 */
	public final void calculateScaledScanRange(final int width, final int height) {
		this.width = width;
		this.height = height;

		this.setHscanScaledStart((int) (this.hscan[0] / HUNDRET * height));
		this.setHscanScaledEnd((int) (this.hscan[1] / HUNDRET * height));
		this.getVScanScaled()[0] = (int) (this.vscan[0] / HUNDRET * width);
		this.getVScanScaled()[1] = (int) (this.vscan[1] / HUNDRET * width);
	}

	@Override
	public final float getAutospeed() {
		if (this.globalConfig.isUse() && this.globalConfig.getAutospeed() != -1)
		{
			return this.globalConfig.getAutospeed();
		}
		return super.getAutospeed();
	}

	protected float getHscanEnd() {
		return this.hscan[1];
	}

	protected float getHscanStart() {
		return this.hscan[0];
	}

	public final String getName() {
		return this.name;
	}

	public final float[] getRgb() {
		final float[] rgbNew = new float[4];
		// if no pixels are set, the denominator is 0, so just return black
		if (this.rgb[3] == 0)
		{
			for (int i = 0; i < 3; i++)
			{
				rgbNew[i] = this.rgb[i];
			}
			return rgbNew;
		}

		// convert from numerator/denominator to float
		for (int i = 0; i < 3; i++)
		{
			rgbNew[i] = MathUtils.clamp((float) this.rgb[i]
					/ (float) this.rgb[3] / 255.0f, 0.0f, 1.0f);
		}

		// reset values
		this.rgb[0] = 0;
		this.rgb[1] = 0;
		this.rgb[2] = 0;
		this.rgb[3] = 0;

		// this tries to set the speed based on how fast the input is changing
		// it needs sync mode to work properly
		if (this.getAutospeed() > 0.0)
		{
			float change = Math.abs(rgbNew[0] - this.prevrgb[0])
					+ Math.abs(rgbNew[1] - this.prevrgb[1])
					+ Math.abs(rgbNew[2] - this.prevrgb[2]);
			change /= 3.0f;

			// only apply single change if it's large enough, otherwise we risk
			// sending it continuously
			if (change > CHANGE_LIMIT)
			{
				this.setSingleChange(MathUtils.clamp(
						(float) (change * this.getAutospeed() / 10.0), 0.0f,
						1.0f));
			}
			else
			{
				this.setSingleChange(0.0f);
			}
		}

		this.prevrgb = Arrays.copyOf(rgbNew, rgbNew.length);
		// memcpy(m_prevrgb, rgb, sizeof(m_prevrgb));

		// we need some hsv adjustments
		if (this.getValue() != 1.0f || this.getValueRangeStart() != 0.0f
				|| this.getValueRangeEnd() != 1.0f
				|| this.getSaturation() != 1.0f
				|| this.getSatRangeStart() != 0.0f
				|| this.getSatRangeEnd() != 1.0f)
		{
			// rgb - hsv conversion, thanks wikipedia!
			final float hsv[] = new float[3];
			final float max = Math.max(rgbNew[0],
					Math.max(rgbNew[1], rgbNew[2]));
			final float min = Math.min(rgbNew[0],
					Math.min(rgbNew[1], rgbNew[2]));

			if (min == max) // grayscale
			{
				hsv[0] = -1.0f; // undefined
				hsv[1] = 0.0f; // no saturation
				hsv[2] = min; // value
			}
			else
			{
				if (max == rgbNew[0]) // red zone
				{
					hsv[0] = 60.0f * ((rgbNew[1] - rgbNew[2]) / (max - min))
							+ HSV_DEGREES_RED;
					while (hsv[0] >= HSV_DEGREES_RED)
					{
						hsv[0] -= HSV_DEGREES_RED;
					}
				}
				else if (max == rgbNew[1]) // green zone
				{
					hsv[0] = 60.0f * ((rgbNew[2] - rgbNew[0]) / (max - min))
							+ HSV_DEGREES_GREEN;
				}
				else if (max == rgbNew[2]) // blue zone
				{
					hsv[0] = 60.0f * ((rgbNew[0] - rgbNew[1]) / (max - min))
							+ HSV_DEGREES_BLUE;
				}

				hsv[1] = (max - min) / max; // saturation
				hsv[2] = max; // value
			}

			// saturation and value adjustment
			hsv[1] = MathUtils.clamp(hsv[1] * this.getSaturation(),
					this.getSatRangeStart(), this.getSatRangeEnd());
			hsv[2] = MathUtils.clamp(hsv[2] * this.getValue(),
					this.getValueRangeStart(), this.getValueRangeEnd());

			if (hsv[0] == -1.0f) // grayscale
			{
				for (int i = 0; i < 3; i++)
				{
					rgbNew[i] = hsv[2];
				}
			}
			else
			{
				final int hi = (int) (hsv[0] / 60.0f) % 6;
				final float f = hsv[0] / 60.0f - (int) (hsv[0] / 60.0f);

				final float s = hsv[1];
				final float v = hsv[2];
				final float p = v * (1.0f - s);
				final float q = v * (1.0f - f * s);
				final float t = v * (1.0f - (1.0f - f) * s);

				if (hi == 0)
				{
					rgbNew[0] = v;
					rgbNew[1] = t;
					rgbNew[2] = p;
				}
				else if (hi == 1)
				{
					rgbNew[0] = q;
					rgbNew[1] = v;
					rgbNew[2] = p;
				}
				else if (hi == 2)
				{
					rgbNew[0] = p;
					rgbNew[1] = v;
					rgbNew[2] = t;
				}
				else if (hi == 3)
				{
					rgbNew[0] = p;
					rgbNew[1] = q;
					rgbNew[2] = v;
				}
				else if (hi == 4)
				{
					rgbNew[0] = t;
					rgbNew[1] = p;
					rgbNew[2] = v;
				}
				else if (hi == 5)
				{
					rgbNew[0] = v;
					rgbNew[1] = p;
					rgbNew[2] = q;
				}
			}

			for (int i = 0; i < 3; i++)
			{
				rgbNew[i] = MathUtils.clamp(rgbNew[i], 0.0f, 1.0f);
			}
		}
		return rgbNew;
	}

	@Override
	public final float getSatRangeEnd() {
		if (this.globalConfig.isUse()
				&& this.globalConfig.getSatRangeEnd() != -1)
		{
			return this.globalConfig.getSatRangeEnd();
		}
		return super.getSatRangeEnd();
	}

	@Override
	public final float getSatRangeStart() {
		if (this.globalConfig.isUse()
				&& this.globalConfig.getSatRangeStart() != -1)
		{
			return this.globalConfig.getSatRangeStart();
		}
		return super.getSatRangeStart();
	}

	@Override
	public final float getSaturation() {
		if (this.globalConfig.isUse()
				&& this.globalConfig.getSaturation() != -1)
		{
			return this.globalConfig.getSaturation();
		}
		return super.getSaturation();
	}

	@Override
	public final float getSinglechange() {
		if (this.globalConfig.isUse()
				&& this.globalConfig.getSinglechange() != -1)
		{
			return this.globalConfig.getSinglechange();
		}
		return super.getSinglechange();
	}

	@Override
	public final int getThreshold() {
		if (this.globalConfig.isUse() && this.globalConfig.getThreshold() != -1)
		{
			return this.globalConfig.getThreshold();
		}
		return super.getThreshold();
	}

	@Override
	public final float getValue() {
		if (this.globalConfig.isUse() && this.globalConfig.getValue() != -1)
		{
			return this.globalConfig.getValue();
		}
		return super.getValue();
	}

	@Override
	public final float getValueRangeEnd() {
		if (this.globalConfig.isUse()
				&& this.globalConfig.getValueRangeEnd() != -1)
		{
			return this.globalConfig.getValueRangeEnd();
		}
		return super.getValueRangeEnd();
	}

	@Override
	public final float getValueRangeStart() {
		if (this.globalConfig.isUse()
				&& this.globalConfig.getValueRangeStart() != -1)
		{
			return this.globalConfig.getValueRangeStart();
		}
		return super.getValueRangeStart();
	}

	protected float getVscanEnd() {
		return this.vscan[1];
	}

	protected float getVscanStart() {
		return this.vscan[0];
	}

	public final void setHscan(final float start, final float end) {
		this.setHscanStart(start);
		this.setHscanEnd(end);
	}

	protected void setHscanEnd(final float end) {
		this.hscan[1] = end;
	}

	protected void setHscanStart(final float start) {
		this.hscan[0] = start;
	}

	public final void setName(final String name) {
		this.name = name;
	}

	/**
	 * Set a boblight option.
	 * 
	 * @param option
	 *            the option and the value e.g. "speed 65.0"
	 * @return true if the option should be sent to the server
	 * @throws BoblightConfigurationException
	 *             if option is empty
	 */
	public final boolean setOption(final String option)
			throws BoblightConfigurationException {
		final Pointer<String> stroption = new Pointer<String>(option);

		String optName = null;
		try
		{
			optName = Misc.getWord(stroption);
		}
		catch (final BoblightParseException e)
		{
			// string with only whitespace
			throw new BoblightConfigurationException("Empty option", e);
		}

		for (final BoblightOptions opt : BoblightOptions.values())
		{
			if (optName.equals(opt.getName()))
			{
				Object value = null;
				if (boolean.class.equals(opt.getType()))
				{
					value = Boolean.parseBoolean(stroption.get());
				}
				else if (float.class.equals(opt.getType()))
				{
					value = Float.parseFloat(stroption.get());
				}
				else if (int.class.equals(opt.getType()))
				{
					value = Integer.parseInt(stroption.get());
				}
				return opt.postProcess(this, value);
			}
		}
		// option not found
		throw new BoblightConfigurationException("Unknown option " + optName);
	}

	public final void setVscan(final float start, final float end) {
		this.vscan[0] = start;
		this.vscan[1] = end;
	}

	protected void setVscanEnd(final float value) {
		this.vscan[1] = value;
	}

	protected void setVscanStart(final float value) {
		this.vscan[0] = value;
	}

}
