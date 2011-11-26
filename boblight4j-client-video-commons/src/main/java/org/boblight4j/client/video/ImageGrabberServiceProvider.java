package org.boblight4j.client.video;

import org.boblight4j.client.Client;
import org.boblight4j.client.grabber.Grabber;

public interface ImageGrabberServiceProvider {

	Grabber create(Client client, boolean sync, int width, int height);

}
