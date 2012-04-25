package org.boblight4j.client;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpectrumAnalyzer extends AbstractRemoteBoblightClient {

	static final Logger LOG = LoggerFactory.getLogger(SpectrumAnalyzer.class);

	private FlagManagerSpectrumAnalyzer flagManager;

	public static void main(final String[] args) {
		new SpectrumAnalyzer(args).doRun();
	}

	public SpectrumAnalyzer(final String[] args) {
		super(args);
	}

	private void captureAudio(final Client boblight) {
		try {
			final AudioFormat format = this.getFormat();
			final DataLine.Info info = new DataLine.Info(TargetDataLine.class,
					format);
			final TargetDataLine line = (TargetDataLine) AudioSystem
					.getLine(info);
			line.open(format);
			line.start();

			final Runnable runner = new CaptureRunner(line, format, boblight);
			final Thread captureThread = new Thread(runner);
			captureThread.start();
		} catch (final LineUnavailableException e) {
			LOG.error("Line unavailable.", e);
		}
	}

	private AudioFormat getFormat() {
		final float sampleRate = 44100;
		final int sampleSizeInBits = 8;
		final int channels = 1;
		final boolean signed = true;
		final boolean bigEndian = true;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
				bigEndian);
	}

	@Override
	protected int run() {

		final AbstractRemoteClient boblight = new SocketClientImpl(
				new LightsHolderImpl());
		if (!this.trySetup(boblight)) {
			return 1;
		}

		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		//
		// while (!stop) {
		// try {
		// boblight.sendrgb(false, null);
		// } catch (IOException e) {
		// e.printStackTrace();
		// } catch (BoblightException e) {
		// e.printStackTrace();
		// }
		// try {
		// Thread.sleep(1000 / 50);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }
		//
		// }
		// }).start();

		this.captureAudio(boblight);

		return 0;
	}

	@Override
	protected FlagManager getFlagManager() {
		if (this.flagManager == null) {
			this.flagManager = new FlagManagerSpectrumAnalyzer();
		}
		return this.flagManager;
	}
}
