package de.gebauer.boblight4j.xbmc;

public interface RGBHandler {

	void setScanRange(String substring);

	void handle(RGBValue parse);

	void stop();

}
