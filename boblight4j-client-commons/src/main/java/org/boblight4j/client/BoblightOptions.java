package org.boblight4j.client;

import java.util.ArrayList;
import java.util.List;

import org.boblight4j.utils.MathUtils;

/**
 * 
 * 
 * options.h
 * 
 * @author agebauer
 * 
 */
enum BoblightOptions {

	AUTOSPEED("autospeed", float.class, 0.0f, 100.0f, 0.0f) {
		@Override
		boolean doPostProcess(final Light light, final Object value) {
			light.setAutospeed(Math.max((Float) value, 0.0f));
			return false;
		}
	},
	HSCANEND("hscanend", float.class, 0f, 100f, -1f) {
		@Override
		boolean doPostProcess(final Light light, final Object value) {
			light.setHscanEnd(MathUtils.clamp(((Float) value).floatValue(),
					light.getHscanStart(), ((Float) this.maxValue).floatValue()));
			return false;
		}
	},
	HSCANSTART("hscanstart", float.class, 0f, 100f, -1f) {
		@Override
		boolean doPostProcess(final Light light, final Object value) {
			light.setHscanStart(MathUtils.clamp(((Float) value).floatValue(),
					((Float) this.minValue).floatValue(), light.getHscanEnd()));
			return false;
		}
	},
	INTERPOLATION("interpolation", boolean.class, false, true, false) {
		@Override
		boolean doPostProcess(final Light light, final Object value) {
			// do not store, send it to the server directly
			return true;
		}
	},
	SATURATION("saturation", float.class, 0.0f, 20.0f, 1.0f) {
		@Override
		boolean doPostProcess(final Light light, final Object value) {
			light.setSaturation(Math.max((Float) value, 0.0f));
			return false;
		}
	},
	SATURATIONMAX("saturationmax", float.class, 0.0f, 1.0f, 1.0f) {
		@Override
		boolean doPostProcess(final Light light, final Object value) {
			light.setSatRangeEnd(this.max((Float) value,
					light.getSatRangeStart()));
			return false;
		}
	},
	SATURATIONMIN("saturationmin", float.class, 0.0f, 1.0f, 0.0f) {
		@Override
		boolean doPostProcess(final Light light, final Object value) {
			light.setSatRangeStart(this.min((Float) value,
					light.getSatRangeEnd()));
			return false;
		}
	},
	SPEED("speed", float.class, 0.0f, 100.0f, 100.0f) {
		@Override
		public boolean doPostProcess(final Light light, final Object value) {
			// do not store, send it to the server directly
			return true;
		}
	},
	THRESHOLD("threshold", int.class, 0, 255, 0) {
		@Override
		boolean doPostProcess(final Light light, final Object value) {
			light.setThreshold(this.clamp((Integer) value));
			return false;
		}
	},
	USE("use", boolean.class, false, true, false) {
		@Override
		boolean doPostProcess(final Light light, final Object value) {
			// do not store, send it to the server directly
			return true;
		}
	},
	VALUE("value", float.class, 0.0f, 20.0f, 1.0f) {
		@Override
		boolean doPostProcess(final Light light, final Object value) {
			light.setValue((Float) value);
			return false;
		}

	},
	VALUEMAX("valuemax", float.class, 0.0f, 1.0f, 1.0f) {
		@Override
		boolean doPostProcess(final Light light, final Object value) {
			light.setValueRangeEnd((Float) value);
			light.setValueRangeEnd(MathUtils.clamp(light.getValueRangeEnd(),
					light.getValueRangeStart(), 1.0f));
			return false;
		}
	},
	VALUEMIN("valuemin", float.class, 0.0f, 1.0f, 0.0f) {
		@Override
		boolean doPostProcess(final Light light, final Object value) {
			light.setValueRangeStart(MathUtils.clamp(
					light.getValueRangeStart(),
					((Float) this.minValue).floatValue(),
					light.getValueRangeEnd()));
			return false;
		}
	},
	VSCANEND("vscanend", float.class, 0f, 100f, -1f) {
		@Override
		boolean doPostProcess(final Light light, final Object value) {
			light.setVscanEnd(MathUtils.clamp(((Float) value).floatValue(),
					light.getVscanStart(), ((Float) this.maxValue).floatValue()));
			return false;
		}
	},
	VSCANSTART("vscanstart", float.class, 0f, 100f, -1f) {
		@Override
		boolean doPostProcess(final Light light, final Object value) {
			light.setVscanStart(MathUtils.clamp(((Float) value).floatValue(),
					((Float) this.minValue).floatValue(), light.getVscanEnd()));
			return false;
		}
	};

	private final Object defaultValue;
	protected final Object maxValue;
	protected final Object minValue;
	private String name;
	private Class<?> type;

	private <T> BoblightOptions(final String name, final Class<T> type,
			final T min, final T max, final T defaultValue) {
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		this.minValue = min;
		this.maxValue = max;
	}

	@SuppressWarnings("unchecked")
	protected <T extends Number> T clamp(final T value2) {
		return (T) MathUtils.clamp(value2, (Number) this.minValue,
				(Number) this.maxValue);
	}

	abstract boolean doPostProcess(Light light, Object value);

	public Object getDefault() {
		return this.defaultValue;
	}

	public Object getMax() {
		return this.maxValue;
	}

	public Object getMin() {
		return this.minValue;
	}

	public String getName() {
		return this.name;
	}

	public Class<?> getType() {
		return this.type;
	}

	@SuppressWarnings("unchecked")
	protected <T extends Number> T max(final T value, final T min) {
		return MathUtils.clamp(value, min, (T) this.maxValue);
	}

	@SuppressWarnings("unchecked")
	protected <T extends Number> T min(final T value, final T max) {
		return MathUtils.clamp(value, (T) this.minValue, max);
	}

	<T> boolean postProcess(final Light light, final T value) {
		return this.doPostProcess(light, value);
	}

	private static final List<String> OPTIONS = new ArrayList<String>();

	static
	{
		StringBuilder option = new StringBuilder();
		int padsize = 1;

		for (final BoblightOptions opt : BoblightOptions.values())
		{
			final int optNameLen = opt.getName().length();
			if (optNameLen + 1 > padsize)
			{
				padsize = optNameLen + 1;
			}
		}

		option.append("name");
		final int length = option.length();

		for (int i = 0; i < Math.max(padsize - length, 1); i++)
		{
			option.append(' ');
		}

		option.append("type    min     max     default");
		OPTIONS.add(option.toString());

		for (final BoblightOptions opt : BoblightOptions.values())
		{
			option = new StringBuilder(opt.getName());
			for (int i = 0; i < padsize - opt.getName().length(); i++)
			{
				option.append(' ');
			}

			String val = opt.getType().getName();
			option.append(val);
			for (int i = 0; i < Math.max(8 - val.length(), 1); i++)
			{
				option.append(' ');
			}

			val = opt.getMin() + "";
			option.append(val);
			for (int i = 0; i < Math.max(8 - val.length(), 1); i++)
			{
				option.append(' ');
			}

			val = opt.getMax() + "";
			option.append(val);
			for (int i = 0; i < Math.max(8 - val.length(), 1); i++)
			{
				option.append(' ');
			}

			val = opt.getDefault() + "";
			option.append(val);
			for (int i = 0; i < Math.max(8 - val.length(), 1); i++)
			{
				option.append(' ');
			}
			OPTIONS.add(option.toString());
		}
	}

	public static int getNrOptions() {
		return OPTIONS.size();
	}

	/**
	 * Returns the option description.<br>
	 * <br>
	 * <strong>boblight eq</strong><br>
	 * const char* CBoblight::GetOptionDescription(int option)
	 * 
	 * @param option
	 *            the zero-based index of the option
	 * @return
	 */
	public static String getOptionDescription(final int option) {
		if (option < 0 || option >= OPTIONS.size())
		{
			return null;
		}

		return OPTIONS.get(option);
	}

}