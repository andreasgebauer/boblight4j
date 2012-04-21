package org.boblight4j.client;

import java.util.Arrays;

import junit.framework.Assert;

import org.boblight4j.client.mbean.LightConfigMBean;
import org.boblight4j.exception.BoblightConfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class LightTest {

	private static final int HUNDRET = 100;

	private Light testable;

	@Before
	public void setUp() throws Exception {
		this.testable = new Light(Mockito.mock(LightConfigMBean.class));
	}

	@Test
	public void testCalculateScaledScanRange() {
		this.testable.calculateScaledScanRange(HUNDRET, HUNDRET);

		Assert.assertEquals(0, this.testable.getHScanScaled()[0]);
		Assert.assertEquals(0, this.testable.getHScanScaled()[1]);

		this.testable.setHscan(0, 50);
		this.testable.setVscan(0, 50);

		this.testable.calculateScaledScanRange(HUNDRET, HUNDRET);

		Assert.assertEquals(0, this.testable.getHScanScaled()[0]);
		Assert.assertEquals(50, this.testable.getHScanScaled()[1]);

	}

	@Test
	public void testGetRgb() {
		float[] expected = new float[] { 0, 0, 0, 0 };

		float[] actual = this.testable.getRgb();

		Assert.assertTrue("Arrays not equal: " + Arrays.toString(expected)
				+ " actual: " + Arrays.toString(actual),
				Arrays.equals(expected, actual));

		this.testable.rgb[0] = 255;
		this.testable.rgb[1] = 255;
		this.testable.rgb[2] = 255;
		this.testable.rgb[3] = 1;

		this.testable.setValue(1);

		this.testable.setValue(1);

		this.testable.setValue(1);

		actual = this.testable.getRgb();

		expected = new float[] { 1, 1, 1, 0 };

		Assert.assertTrue("Arrays not equal: " + Arrays.toString(expected)
				+ " actual: " + Arrays.toString(actual),
				Arrays.equals(expected, actual));

	}

	@Test
	public void testSetOption() throws BoblightConfigurationException {

		this.testable.setOption("saturation 2.0");
	}

}
