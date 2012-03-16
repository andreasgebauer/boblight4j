package de.gebauer.boblight4j.xbmc;

import org.boblight4j.client.AbstractFlagManager;
import org.boblight4j.client.Client;
import org.boblight4j.client.grabber.AbstractPassiveGrabber;
import org.boblight4j.exception.BoblightException;

public class InputStreamGrabber extends AbstractPassiveGrabber implements
		RGBHandler {

	private InputStreamRGBReader reader;

	public InputStreamGrabber(Client client, boolean sync, int width, int height) {
		super(client, sync, width, height);
	}

	@Override
	public void setup(AbstractFlagManager flagManager) throws BoblightException {
		reader = new InputStreamRGBReader();
		reader.setup(System.in, this);
		new Thread(reader).start();
	}

	@Override
	public void setScanRange(String substring) {
		this.client.setScanRange(width, height);
	}

	@Override
	public void handle(RGBValue parse) {
		this.client.addPixel(parse.xPos, parse.yPos, parse.rgb);
	}

	@Override
	public void stop() {
		this.cleanup();
	}

	@Override
	public void cleanup() {

	}

}
