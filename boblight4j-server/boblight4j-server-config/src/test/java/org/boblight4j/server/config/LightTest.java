package org.boblight4j.server.config;

import static org.mockito.Mockito.mock;

import java.util.Vector;

import org.boblight4j.server.Light;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class LightTest {

	Light testable;

	@Before
	public void setUp() {
		this.testable = new Light(new LightConfig("light"), false);
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

		final ColorConfig red = new ColorConfig();
		red.setName("red");
		red.setRgb(new float[] { 1, 0, 0 });
		red.setBlacklevel(0.1f);
		LightConfig config = this.testable.getConfig();
		config.addColor(red);

		final ColorConfig green = new ColorConfig();
		green.setName("green");
		green.setRgb(new float[] { 0, 1, 0 });
		config.addColor(green);

		final ColorConfig blue = new ColorConfig();
		blue.setName("blue");
		blue.setRgb(new float[] { 0, 0, 1 });
		config.addColor(blue);

		final double colorValue = this.testable.getColorValue(0);

		Assert.assertEquals(1.0, colorValue, 0);
	}

	@Test
	public void testGetColorValueInitial() {

		this.testable.setInterpolation(true);

		this.testable.setRgb(new float[] { 1.0f, 1.0f, 1.0f }, 0);

		final double colorValue = this.testable.getColorValue(0);

		Assert.assertEquals(0, colorValue, 0);
	}

}
