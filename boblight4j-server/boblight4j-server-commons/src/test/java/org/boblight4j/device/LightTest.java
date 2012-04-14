package org.boblight4j.device;

import java.util.Vector;

import org.boblight4j.server.config.Color;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class LightTest {

	Light testable;

	@Before
	public void setUp() {
		this.testable = new Light();
	}

	@Test
	public void testAddUser() {
		final Device mock = Mockito.mock(Device.class);

		this.testable.addUser(mock);
	}

	@Test
	public void testClearUser() {
		this.testable.clearUser(Mockito.mock(Device.class));
	}

	@Test
	public void testGetColorValue() {

		this.testable.setInterpolation(true);

		this.testable.setRgb(new float[] { 1, 1, 1 }, 1);
		this.testable.setRgb(new float[] { 1, 1, 1 }, 2);
		this.testable.setRgb(new float[] { 1, 1, 1 }, 3);

		final Vector<Color> colors = new Vector<Color>();
		final Color red = new Color();
		red.setName("red");
		red.setRgb(new float[] { 1, 0, 0 });
		red.setBlacklevel(0.1f);
		this.testable.addColor(red);

		final Color green = new Color();
		green.setName("green");
		green.setRgb(new float[] { 0, 1, 0 });
		this.testable.addColor(green);

		final Color blue = new Color();
		blue.setName("blue");
		blue.setRgb(new float[] { 0, 0, 1 });
		this.testable.addColor(blue);

		final double colorValue = this.testable.getColorValue(0, 1006);

		Assert.assertEquals(1.0, colorValue, 0);
	}

	@Test
	public void testGetColorValueInitial() {

		this.testable.setInterpolation(true);

		this.testable.setRgb(new float[] { 1.0f, 1.0f, 1.0f }, 0);

		final double colorValue = this.testable.getColorValue(0, 0);

		Assert.assertEquals(0, colorValue, 0);
	}

}
