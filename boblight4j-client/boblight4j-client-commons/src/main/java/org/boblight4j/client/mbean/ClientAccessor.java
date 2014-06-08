package org.boblight4j.client.mbean;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.boblight4j.client.AbstractRemoteClient;
import org.boblight4j.client.Light;

public class ClientAccessor implements ClientAccessorMBean {

	private final AbstractRemoteClient client;

	public ClientAccessor(final AbstractRemoteClient boblightClient) {
		this.client = boblightClient;
	}

	private Collection<Light> getLights() {
		return this.client.getLightsHolder().getLights();
	}

	@Override
	public float getSaturation() {
		final Collection<Light> lights = this.getLights();
		final Iterator<Light> iterator = lights.iterator();
		if (iterator.hasNext()) {
			return iterator.next().getSaturation();
		}
		return -1;
	}

	public void setAutoSpeed() {
		// TODO implement
	}

	@Override
	public void setSaturation(final float saturation) {
		for (final Light l : this.getLights()) {
			l.setSaturation(saturation);
		}
	}

}
