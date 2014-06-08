package org.boblight4j.client;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Map;

import junit.framework.Assert;

import org.boblight4j.client.mbean.LightConfigMBean;
import org.boblight4j.exception.BoblightException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

public class LightsHolderImplTest {

	private LightsHolderImpl testable;

	@Before
	public void setUp() throws Exception {
		this.testable = new LightsHolderImpl();
	}

	@Test
	public void testAddPixelIntArrayIntInt() throws BoblightException,
			IOException {
		final Map<String, Light> internalState = Whitebox.getInternalState(
				this.testable, Map.class);
		final Light light = Mockito.mock(Light.class);
		Whitebox.setInternalState(light, LightConfigMBean.class,
				Mockito.mock(LightConfigMBean.class));
		Whitebox.setInternalState(light, int[].class, new int[] { 0, 0, 0, 0 });

		Mockito.when(light.getHScanScaled()).thenReturn(new int[] { 0, 20 });
		Mockito.when(light.getVScanScaled()).thenReturn(new int[] { 0, 20 });

		internalState.put("light1", light);

		this.testable.addPixel(0, 0, new int[] { 255, 255, 255 });

		Assert.assertEquals(255, light.rgb[0]);
		Assert.assertEquals(255, light.rgb[1]);
		Assert.assertEquals(255, light.rgb[2]);
		Assert.assertEquals(1, light.rgb[3]);

	}

}
