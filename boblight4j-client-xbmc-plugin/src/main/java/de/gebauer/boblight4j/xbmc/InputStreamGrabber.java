package de.gebauer.boblight4j.xbmc;

import java.io.IOException;

import org.boblight4j.client.Client;
import org.boblight4j.client.ClientImpl;
import org.boblight4j.client.FlagManager;
import org.boblight4j.client.grabber.AbstractPassiveGrabber;
import org.boblight4j.exception.BoblightException;

public class InputStreamGrabber extends AbstractPassiveGrabber implements
		RGBHandler {

	public static void main(String[] args) {
		try
		{
			final FlagManagerInputStreamGrabber flagManager = new FlagManagerInputStreamGrabber();
			flagManager.parseFlags(args);
			final InputStreamGrabber grabber = new InputStreamGrabber(
					new ClientImpl(), flagManager.isSync(), 20, 20);
			grabber.setup(flagManager);
		}
		catch (BoblightException e)
		{
			e.printStackTrace();
		}
	}

	private InputStreamRGBReader reader;
	private FlagManagerInputStreamGrabber flagManager;

	public InputStreamGrabber(Client client, boolean sync, int width, int height) {
		super(client, sync, width, height);
	}

	@Override
	public void setup(FlagManager flagManager) throws BoblightException {
		reader = new InputStreamRGBReader();
		reader.setup(System.in, this);
		new Thread(reader).start();
		this.client.connect(flagManager.getAddress(), flagManager.getPort(),
				5000);

		this.flagManager = (FlagManagerInputStreamGrabber) flagManager;

		this.flagManager.parseBoblightOptions(this.client);
	}

	@Override
	public void setScanRange(String substring) {
		final int commaPos = substring.indexOf(',');
		final int width = Integer.parseInt(substring.substring(0, commaPos));
		final int height = Integer.parseInt(substring.substring(commaPos + 1));
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

	@Override
	public void sendRgb(boolean b, Object object) throws IOException,
			BoblightException {
		this.client.sendRgb(this.sync, null);
	}

	@Override
	public Client getClient() {
		return this.client;
	}

}
