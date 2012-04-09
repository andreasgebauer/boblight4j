package org.boblight4j.utils;

import org.boblight4j.exception.BoblightRuntimeException;

public final class MathUtils {

	private MathUtils() {
	}

	public static double clamp(final double multiply, final double low,
			final double high) {
		return Math.max(low, Math.min(multiply, high));
	}

	/**
	 * Clamps a value between high and low.
	 * 
	 * @param value
	 *            the value to clamp
	 * @param low
	 *            the low mark
	 * @param high
	 *            the high mark
	 * @return clamped value
	 */
	public static float clamp(final float value, final float low,
			final float high) {
		return Math.max(low, Math.min(value, high));
	}

	public static int clamp(final int multiply, final int low, final int high) {
		return Math.max(low, Math.min(multiply, high));
	}

	public static long clamp(final long multiply, final long low,
			final long high) {
		return Math.max(low, Math.min(multiply, high));
	}

	public static short clamp(final short multiply, final short low,
			final short high) {
		short clamped = multiply;
		if (high < clamped)
		{
			clamped = high;
		}
		if (low > clamped)
		{
			clamped = low;
		}
		return clamped;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Number> T clamp(final T value, final T low,
			final T high) {
		if (value instanceof Float)
		{
			return (T) Float.valueOf(MathUtils.clamp(
					((Number) value).floatValue(), ((Number) low).floatValue(),
					((Number) high).floatValue()));
		}
		else if (value instanceof Double)
		{
			return (T) Double
					.valueOf(MathUtils.clamp(((Double) value).doubleValue(),
							((Double) low).doubleValue(),
							((Double) high).doubleValue()));
		}
		else if (value instanceof Integer)
		{
			return (T) Integer.valueOf(MathUtils.clamp(
					((Integer) value).intValue(), ((Integer) low).intValue(),
					((Integer) high).intValue()));
		}
		else if (value instanceof Long)
		{
			return (T) Long.valueOf(MathUtils.clamp(((Long) value).longValue(),
					((Long) low).longValue(), ((Long) high).longValue()));
		}

		throw new BoblightRuntimeException("Type " + value.getClass()
				+ " currently not supported");
	}
}
