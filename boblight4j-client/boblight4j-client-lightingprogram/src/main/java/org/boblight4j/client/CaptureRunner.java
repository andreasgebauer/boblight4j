package org.boblight4j.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.TargetDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class CaptureRunner implements Runnable {

	private static final int DEFAULT_FRAME_SIZE = 1024;

	private static final Logger LOG = LoggerFactory.getLogger(CaptureRunner.class);

	private final Client boblight;
	private byte buffer[];
	private int bufferSize;

	private ExecutorService executor = Executors.newFixedThreadPool(2,
			new ThreadFactory() {

				@Override
				public Thread newThread(final Runnable r) {
					return new Thread(r, "dct thread");
				}
			});

	private final AudioFormat format;
	private int frameSize;
	private final TargetDataLine line;

	private boolean running;
	private static final float spectralScale = 1f;

	private final WindowFunction windowFunction;

	CaptureRunner(final TargetDataLine line, final AudioFormat format,
			final Client boblight) {
		this.line = line;
		this.format = format;
		this.boblight = boblight;
		this.bufferSize = (int) format.getSampleRate() * format.getFrameSize()
				/ 25;
		this.buffer = new byte[this.bufferSize];
		this.frameSize = DEFAULT_FRAME_SIZE;
		this.windowFunction = new VorbisWindowFunction(this.frameSize);
	}

	@Override
	public void run() {
		this.running = true;

		while (this.running)
		{
			final int count = this.line
					.read(this.buffer, 0, this.buffer.length);
			if (count > 0)
			{
				final double[] samples = new double[this.frameSize];
				for (int i = 0; i < this.frameSize; i++)
				{
					final int hi = this.buffer[i];
					final int sampVal = hi;
					samples[i] = sampVal / spectralScale;
				}

				this.windowFunction.applyWindow(samples);

				final Runnable command = new DCTRunnable(samples, this.boblight);
				final Future<?> submit = this.executor.submit(command);

				submit.isDone();
			}
		}
	}
}