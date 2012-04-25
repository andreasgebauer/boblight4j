package org.boblight4j.server;

import java.util.List;

public class LightsAccessor implements LightsAccessorMBean {

	private final List<Light> lights;

	public LightsAccessor(final List<Light> lights) {
		this.lights = lights;
	}

	@Override
	public float getSpeed() {
		float globalSpeed = 0;
		int lightCnt = 0;
		for (final Light l : this.lights)
		{
			lightCnt++;
			final float speed = l.getSpeed();
			globalSpeed += speed;
			// not the same for all lights
		}
		return globalSpeed / lightCnt;
	}

	@Override
	public void setSpeed(final float speed) {
		for (final Light l : this.lights)
		{
			l.setSpeed(speed);
		}
	}

}
