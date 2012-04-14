package de.gebauer.boblight4j.xbmc;

import java.io.IOException;

import org.boblight4j.client.Client;
import org.boblight4j.exception.BoblightException;

public interface RGBHandler {

	void setScanRange(String substring);

	void handle(RGBValue parse);

	void stop();

	void sendRgb(boolean b, Object object) throws IOException,
			BoblightException;

	Client getClient();

}
