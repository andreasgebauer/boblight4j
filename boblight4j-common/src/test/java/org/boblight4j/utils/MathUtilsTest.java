package org.boblight4j.utils;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.boblight4j.exception.BoblightRuntimeException;
import org.junit.Test;

public class MathUtilsTest {

	@Test(expected = IllegalArgumentException.class)
	public void testClampDoubleDoubleDoubleIllegalArguments() {
		assertEquals(0d, MathUtils.clamp(0d, 1d, 0d), 0d);
	}

	@Test
	public void testClampDoubleDoubleDouble() {
		assertEquals(0d, MathUtils.clamp(0d, 0d, 1d), 0d);
		assertEquals(0d, MathUtils.clamp(-1000d, 0d, 1d), 0d);
		assertEquals(1d, MathUtils.clamp(1000d, 0d, 1d), 0d);
		assertEquals(0d, MathUtils.clamp(1000d, 0d, 0d), 0d);
		assertEquals(1d, MathUtils.clamp(-1000d, 1d, 1d), 0d);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testClampFloatFloatFloatIllegalArguments() {
		assertEquals(0f, MathUtils.clamp(0f, 1f, 0f), 0f);
	}

	@Test
	public void testClampFloatFloatFloat() {
		assertEquals(0f, MathUtils.clamp(0f, 0f, 1f), 0f);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testClampIntIntIntIllegalArgument() {
		assertEquals(0, MathUtils.clamp(0, 1, 0));
	}

	@Test
	public void testClampIntIntInt() {
		assertEquals(0, MathUtils.clamp(0, 0, 1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testClampLongLongLongIllegalArgument() {
		assertEquals(0l, MathUtils.clamp(0l, 1l, 0l));
	}

	@Test
	public void testClampLongLongLong() {
		assertEquals(0l, MathUtils.clamp(0l, 0l, 1l));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testClampShortShortShortIllegalArgument() {
		assertEquals(0, MathUtils.clamp((short) 0, (short) 1, (short) 0));
	}

	@Test
	public void testClampShortShortShort() {
		assertEquals(0, MathUtils.clamp((short) 0, (short) 0, (short) 1));
		assertEquals(0, MathUtils.clamp((short) -1000, (short) 0, (short) 1));
		assertEquals(1, MathUtils.clamp((short) 1000d, (short) 0d, (short) 1d));
		assertEquals(0, MathUtils.clamp((short) 1000d, (short) 0d, (short) 0d));
		assertEquals(1, MathUtils.clamp((short) -1000d, (short) 1d, (short) 1d));
	}

	@Test
	public void testClampTTTIlegalArguments() {

		Number low = null, high = null, value = null;

		try {
			MathUtils.clamp(value, low, high);
		} catch (Exception e) {
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
	}

	@Test
	public void testClampTTT() {

		Number expected = 0, low = 0, high = 1, value = 0;
		List<Number[]> values = new ArrayList<Number[]>();
		values.add(new Number[] { expected, value, low, high });
		values.add(new Number[] { expected.floatValue(), value.floatValue(),
				low.floatValue(), high.floatValue() });
		values.add(new Number[] { new Double(0), new Double(0), new Double(0),
				new Double(1) });
		values.add(new Number[] { new Short((short) 0), new Short((short) 0),
				new Short((short) 0), new Short((short) 1) });
		values.add(new Number[] { new Long(0), new Long(0), new Long(0),
				new Long(1) });

		for (Number[] objects : values) {
			assertEquals(objects[0],
					MathUtils.clamp(objects[1], objects[2], objects[3]));
		}

	}

	@Test(expected = BoblightRuntimeException.class)
	public void testClampTTTTypeNotSupported() {

		Number number = new Number() {

			@Override
			public long longValue() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int intValue() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public float floatValue() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public double doubleValue() {
				// TODO Auto-generated method stub
				return 0;
			}
		};

		MathUtils.clamp(number, number, number);

	}
}
