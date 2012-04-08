package org.boblight4j.client.grabber;

import org.boblight4j.client.ClientImpl;

public abstract class AbstractSmartActiveGrabber extends AbstractActiveGrabber {

	public AbstractSmartActiveGrabber(ClientImpl client, boolean sync, int width,
			int height) {
		super(client, sync, width, height);
	}

}
