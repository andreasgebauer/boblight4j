package de.gebauer.boblight4j.xbmc;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class InputStreamRGBReader implements Runnable {

	private boolean stop;
	private LineNumberReader lineNumberReader;
	private RGBHandler rgbHandler;

	public InputStreamRGBReader() {
	}

	public void setup(final InputStream stream, final RGBHandler rgbHandler) {
		this.rgbHandler = rgbHandler;
		lineNumberReader = new LineNumberReader(new InputStreamReader(stream));
	}

	@Override
	public void run() {
		while (!stop)
		{
			String readLine;
			try
			{
				readLine = lineNumberReader.readLine();
				if (readLine == null)
				{
					System.out.println("Stopping because of end of stream");
					this.rgbHandler.stop();

					break;
				}

				if (readLine.startsWith("---"))
				{
					rgbHandler.setScanRange(readLine.substring(3));
				}

				System.out.println(">READ:" + readLine);
				final RGBValue rgbValue = RGBValue.parse(readLine);
				if (rgbValue != null)
				{
					rgbHandler.handle(rgbValue);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
