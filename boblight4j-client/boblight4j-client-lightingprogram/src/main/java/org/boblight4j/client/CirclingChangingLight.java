package org.boblight4j.client;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CirclingChangingLight implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(CirclingChangingLight.class);

    private Client client;

    public CirclingChangingLight(Client client) {
	this.client = client;
    }

    public static void main(String[] args) throws BoblightConfigurationException {
	Client client = new SocketClient(new LightsHolderImpl());
	CommandLineArgs commandLineArgs = new CommandLineArgs();
	FlagManagerSpectrumAnalyzer flagManager = new FlagManagerSpectrumAnalyzer(commandLineArgs);
	flagManager.parseFlags(new String[] { "-s", "raspberrypi:19333" });
	client.setup(flagManager);
	CirclingChangingLight circlingChangingLight = new CirclingChangingLight(client);
	new Thread(circlingChangingLight).start();
    }

    @Override
    public void run() {

	List<Color> colorsToFade = new ArrayList<Color>();
	colorsToFade.add(new Color(255, 0, 0));
	colorsToFade.add(new Color(0, 255, 0));
	colorsToFade.add(new Color(0, 0, 255));
	colorsToFade.add(new Color(0, 255, 0));

	LightsHolder lightsHolder = this.client.getLightsHolder();

	List<Light> lightsOrdered = new ArrayList<Light>();
	lightsOrdered.add(lightsHolder.getLight("top1"));
	lightsOrdered.add(lightsHolder.getLight("top2"));
	lightsOrdered.add(lightsHolder.getLight("top3"));
	lightsOrdered.add(lightsHolder.getLight("top4"));
	lightsOrdered.add(lightsHolder.getLight("top5"));
	lightsOrdered.add(lightsHolder.getLight("right1"));
	lightsOrdered.add(lightsHolder.getLight("right2"));
	lightsOrdered.add(lightsHolder.getLight("right3"));
	lightsOrdered.add(lightsHolder.getLight("right4"));
	lightsOrdered.add(lightsHolder.getLight("right5"));
	lightsOrdered.add(lightsHolder.getLight("bottom5"));
	lightsOrdered.add(lightsHolder.getLight("bottom4"));
	lightsOrdered.add(lightsHolder.getLight("bottom3"));
	lightsOrdered.add(lightsHolder.getLight("bottom2"));
	lightsOrdered.add(lightsHolder.getLight("bottom1"));
	lightsOrdered.add(lightsHolder.getLight("left5"));
	lightsOrdered.add(lightsHolder.getLight("left4"));
	lightsOrdered.add(lightsHolder.getLight("left3"));
	lightsOrdered.add(lightsHolder.getLight("left2"));
	lightsOrdered.add(lightsHolder.getLight("left"));

	double colorDistance = .29;
	int speed = 80;
	
	int stepsbetween = (int) (lightsHolder.getLights().size() * colorDistance);

	int maxSteps = stepsbetween * colorsToFade.size();

	boolean stop = false;
	int curStep = 0;
	while (!stop) {

	    try {
		for (int i = 0; i < lightsOrdered.size(); i++) {
		    Light light = lightsOrdered.get(i);

		    int colorFromIndex = (curStep + i) / stepsbetween;
		    while (colorFromIndex >= colorsToFade.size()) {
			colorFromIndex -= colorsToFade.size();
		    }
		    int colorToIndex = (curStep + i) / stepsbetween + 1;
		    while (colorToIndex >= colorsToFade.size()) {
			colorToIndex -= colorsToFade.size();
		    }

		    Color from = colorsToFade.get(colorFromIndex);
		    Color to = colorsToFade.get(colorToIndex);

		    int colorWeightFrom = stepsbetween - ((curStep + i) % stepsbetween);
		    int colorWeightTo = stepsbetween - colorWeightFrom;

		    int red = (from.getRed() * colorWeightFrom + to.getRed() * colorWeightTo) / stepsbetween;
		    int green = (from.getGreen() * colorWeightFrom + to.getGreen() * colorWeightTo) / stepsbetween;
		    int blue = (from.getBlue() * colorWeightFrom + to.getBlue() * colorWeightTo) / stepsbetween;

		    lightsHolder.addPixel(light.getName(), new int[] { red, green, blue });

		}

		this.client.sendRgb(true, null);
	    } catch (BoblightException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	    curStep++;
	    if (curStep >= maxSteps) {
		curStep = 0;
	    }

	    try {
		Thread.sleep(speed);
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }
}
