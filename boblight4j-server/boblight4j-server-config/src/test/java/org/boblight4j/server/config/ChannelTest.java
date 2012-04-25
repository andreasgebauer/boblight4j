package org.boblight4j.server.config;

import java.awt.geom.Point2D;

import org.boblight4j.server.Channel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ChannelTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	private Channel channel;

	@Before
	public void setUp() throws Exception {
		this.channel = new Channel(0, 0, "channel1");
	}

	@Test
	public void testGetValue() {
		this.channel.setValue(1);
		this.channel.getValue(0);

		double value = this.channel.getValue(0);

		Assert.assertEquals(1, value, 0);

		//

		this.channel.setAdjusts(new Point2D.Float[] { new Point2D.Float(0.5f,
				0f) });
		this.channel.setValue(0.5f);

		value = this.channel.getValue(0);

		Assert.assertEquals(0.0, value, .1E-6);

		//

		this.channel.setValue(0.8f);

		value = this.channel.getValue(0);

		Assert.assertEquals(0.6, value, .1E-6);

		//

		this.channel.setValue(1f);

		value = this.channel.getValue(0);

		Assert.assertEquals(1, value, .1E-6);

		//

		this.channel.setAdjusts(new Point2D.Float[] { new Point2D.Float(0.5f,
				1f) });

		//
		this.channel.setValue(0.25f);

		value = this.channel.getValue(0);

		Assert.assertEquals(0.5, value, .1E-6);

		//

		this.channel.setValue(0.5f);

		value = this.channel.getValue(0);

		Assert.assertEquals(1.0, value, .1E-6);

		//

		this.channel.setValue(0.8f);

		value = this.channel.getValue(0);

		Assert.assertEquals(1, value, .1E-6);

		//

		this.channel.setValue(1f);

		value = this.channel.getValue(0);

		Assert.assertEquals(1, value, .1E-6);

	}

}
