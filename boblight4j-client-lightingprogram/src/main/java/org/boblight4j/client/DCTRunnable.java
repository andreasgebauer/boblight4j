package org.boblight4j.client;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.boblight4j.exception.BoblightException;

import edu.emory.mathcs.jtransforms.dct.DoubleDCT_1D;

final class DCTRunnable implements Runnable {

	private static final Logger LOG = Logger.getLogger(DCTRunnable.class);

	private final Client boblight;
	private final DoubleDCT_1D doubleDCT_1D;
	/**
	 * 
	 */
	private final double[] samples;

	DCTRunnable(final double[] samples, final Client boblight) {
		this.boblight = boblight;
		this.doubleDCT_1D = new DoubleDCT_1D(samples.length);
		this.samples = samples.clone();
	}

	@Override
	public void run() {

		final int[] mapping = new int[] { 4, 3, 2, 1, 0, 9, 8, 7, 6, 5, 15, 16,
				17, 18, 19, 10, 11, 12, 13, 14 };

		try
		{
			this.doubleDCT_1D.forward(this.samples, true);

			final int[] result = new int[this.boblight.getNrLights() + 14];

			double re = this.samples[0];
			double im = this.samples[1];
			final double magSqOv = re * re + im * im;
			final int minSrc = 2;
			final int maxSrc = (this.samples.length - minSrc) / 2;
			int curSrc = 0;
			final int minTgt = 0;
			final int maxTgt = result.length - 1;
			int curTgt = minTgt;

			final int scale = maxSrc / maxTgt;

			int valueCnt = 0;
			final int maxSrcIt = (this.samples.length - 1);

			int valMax = 0;

			for (int i = minSrc; i < maxSrcIt; i += minSrc)
			{

				curSrc = (i - minSrc) / 2;

				final int curTgtTmp = curSrc / scale - 3;

				if (curTgtTmp >= 0)
				{
					re = this.samples[i];
					im = this.samples[i + 1];
					final double d = Math.sqrt(re * re + im * im);

					final int greyVal = Math.min(255,
							Math.max(0, (int) (40 * Math.log1p(Math.abs(d)))));

					if (valMax < greyVal)
					{
						valMax = greyVal;
					}

					result[curTgt] += greyVal;

					valueCnt++;
					if (curTgt != curTgtTmp)
					{
						result[curTgt] /= valueCnt;
						valueCnt = 0;
					}
					curTgt = curTgtTmp;
				}
			}

			synchronized (this.boblight)
			{
				for (int i = 0; i < mapping.length; i++)
				{

					final int res = result[i];

					final int[] js = this.valueToColor(res);

					try
					{
						this.boblight.addPixel(mapping[i], js);
					}
					catch (final BoblightException e)
					{
						LOG.error("", e);
					}
				}
				this.boblight.sendRgb(false, null);
			}

		}
		catch (final BoblightException e)
		{
			LOG.error("", e);
		}
		catch (IOException e)
		{
			LOG.error("", e);
		}

	}

	private int[] valueToColor(final int res) {
		final int[] js = new int[3];

		final int val = res * 4;
		if (val > 255 * 3)
		{
			js[0] = res;
		}

		else if (val > 255 * 2)
		{
			js[1] = res;
		}

		else if (val > 255)
		{
			js[2] = res;
		}

		return js;
	}
}