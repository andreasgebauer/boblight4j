package org.boblight4j.client.video;

import org.boblight4j.client.ClientImpl;
import org.boblight4j.client.grabber.Grabber;

public interface ImageGrabberServiceProvider {

	Grabber create(ClientImpl client, boolean sync, int width, int height);

}
