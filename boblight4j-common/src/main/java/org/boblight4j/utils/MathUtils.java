package org.boblight4j.utils;

import org.boblight4j.exception.BoblightRuntimeException;

public final class MathUtils {

    private MathUtils() {
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
    public static double clamp(final double multiply, final double low, final double high) {
	checkRange(low, high);
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
    public static float clamp(final float value, final float low, final float high) {
	checkRange(low, high);
	return Math.max(low, Math.min(value, high));
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
     * @return the clamped value
     */
    public static int clamp(final int value, final int low, final int high) {
	checkRange(low, high);
	return Math.max(low, Math.min(value, high));
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
    public static long clamp(final long value, final long low, final long high) {
	checkRange(low, high);
	return Math.max(low, Math.min(value, high));
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
    public static short clamp(final short value, final short low, final short high) {
	checkRange(low, high);
	short clamped = value;
	if (high < clamped) {
	    clamped = high;
	}
	if (low > clamped) {
	    clamped = low;
	}
	return clamped;
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
    @SuppressWarnings("unchecked")
    public static <T extends Number> T clamp(final T value, final T low, final T high) {
	if (value == null) {
	    throw new IllegalArgumentException("Argument value must not be null");
	}

	if (value instanceof Float) {
	    return (T) Float.valueOf(MathUtils.clamp(value.floatValue(), low.floatValue(), high.floatValue()));
	} else if (value instanceof Double) {
	    return (T) Double.valueOf(MathUtils.clamp(value.doubleValue(), low.doubleValue(), high.doubleValue()));
	} else if (value instanceof Integer) {
	    return (T) Integer.valueOf(MathUtils.clamp(value.intValue(), low.intValue(), high.intValue()));
	} else if (value instanceof Short) {
	    return (T) Short.valueOf(MathUtils.clamp(value.shortValue(), low.shortValue(), high.shortValue()));
	} else if (value instanceof Long) {
	    return (T) Long.valueOf(MathUtils.clamp(value.longValue(), low.longValue(), high.longValue()));
	}

	throw new BoblightRuntimeException("Type " + value.getClass() + " currently not supported");
    }

    private static <T extends Number> void checkRange(T low, T high) {
	if (high.floatValue() < low.floatValue()) {
	    throw new IllegalArgumentException("High value must be higher than or equal to low value");
	}
    }

}
