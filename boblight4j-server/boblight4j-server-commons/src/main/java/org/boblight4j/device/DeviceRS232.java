package org.boblight4j.device;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;

import org.boblight4j.Constants;
import org.boblight4j.exception.BoblightDeviceException;
import org.boblight4j.server.ClientsHandler;
import org.boblight4j.server.config.Channel;
import org.boblight4j.utils.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceRS232 extends AbstractDevice {

	private static final Logger LOG = LoggerFactory
			.getLogger(DeviceRS232.class);

	private long bits;
	private AbstractOutputWriter deviceOutput;
	private final Protocol protocol;
	private SerialPort serialPort;
	private final Object timer = new Object();

	public DeviceRS232(final ClientsHandler<?> clients) {
		super(clients);
		// default is 8 bit
		this.bits = 8;
		this.protocol = new Protocol();
	}

	@Override
	protected final void close() {

		if (this.serialPort != null) {
			this.serialPort.close();
		}
		// this.output.close();
	}

	public final Protocol getProtocol() {
		return this.protocol;
	}

	public final void setBits(final int bits) {
		this.bits = bits;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected final boolean setup() {

		// m_timer.SetInterval(m_interval);

		CommPortIdentifier portId = null;

		try {
			final Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier
					.getPortIdentifiers();

			// iterate through, looking for the port
			while (portEnum.hasMoreElements()) {
				final CommPortIdentifier currPortId = portEnum.nextElement();

				LOG.info("Got available CommPort: " + currPortId.getName());
				if (super.getOutput().equals(currPortId.getName())) {
					portId = currPortId;
					break;
				}
			}

		} catch (final UnsatisfiedLinkError e) {
			this.stopThread();
			LOG.error("No txrx on java.library.path?", e);
			return false;
		}

		if (portId == null) {
			LOG.warn("Could not find COM port.");
			return false;
		}

		try {
			// open serial port, and use class name for the appName.
			this.serialPort = (SerialPort) portId.open(this.getClass()
					.getName(), Constants.DEVICE_CONNECTION_TIMEOUT);

			// set port parameters
			this.serialPort.setSerialPortParams(this.getRate(),
					SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// serialPort.notifyOnDataAvailable(false);
			// serialPort.removeEventListener();

			// open the streams
			// input = m_serialport.getInputStream();

			this.protocol.checkValid();

			this.deviceOutput = new EscapedOutputWriter(
					this.serialPort.getOutputStream(), this.protocol,
					this.isDebug());

			// add event listeners
			// m_serialport.addEventListener(this);
			// m_serialport.notifyOnDataAvailable(true);

		} catch (final Exception e) {
			LOG.error("Error occurred", e);
			return false;
		}

		if (this.getDelayAfterOpen() > 0) {
			try {
				Thread.sleep(this.getDelayAfterOpen());
			} catch (final InterruptedException e1) {
				LOG.error("Interrupted while delaying.", e1);
				return false;
			}
		}

		try {
			// send the begin flag mutliple times to initialize
			this.deviceOutput.begin();
			this.deviceOutput.begin();
			this.deviceOutput.begin();
			this.deviceOutput.begin();
			this.deviceOutput.begin();
			this.deviceOutput.begin();
		} catch (final IOException e) {
			LOG.error("Error during initialization.", e);
			return false;
		}

		// bytes per channel
		// m_bytes = m_bits / 8 + ((m_bits % 8) ? 1 : 0);
		// m_bytes = (int) (m_bits / 8 + ((m_bits % 8) == 1 ? 1 : 0));

		// allocate a buffer, that can hold the prefix,the number of bytes per
		// channel and the postfix
		// m_buffsize = prefix.length + m_channels.size() * m_bytes
		// + postfix.length;
		// m_buff = ByteBuffer.wrap(new byte[m_buffsize]);
		//
		// for (int i = 0; i < prefix.length; i++) {
		// m_buff.putChar((char) prefix[i]);
		// }

		// copy in the postfix
		// if (postfix.length > 0) {
		// memcpy(m_buff + prefix.length + m_channels.size() * m_bytes,
		// &postfix[0], postfix.length);
		// }

		// set channel bytes to 0, write it twice to make sure the
		// controller is in sync
		// for (int i = 0; i < 2; i++) {
		// try {
		// byte[] bytes = m_buff.array();
		// output.write(bytes);
		// } catch (IOException e) {
		// LOG.fatal(m_name, e);
		// return false;
		// }
		// }

		return true;
	}

	@Override
	public final void sync() {
		if (this.isAllowSync()) {
			synchronized (this.timer) {
				this.timer.notifyAll();
			}

		}
	}

	@Override
	public void writeOutput() throws BoblightDeviceException {

		// get the channel values from the clienshandler
		final long now = System.currentTimeMillis();
		this.clientsHandler.fillChannels(this.channels, now, this);

		final int maxvalue = (1 << this.bits) - 1;

		try {

			StringBuilder buf = null;
			if (this.isDebug()) {
				buf = new StringBuilder();
			}

			this.deviceOutput.begin();

			for (int i = 0; i < this.channels.size(); i++) {
				final Channel cChannel = this.channels.get(i);
				final double value = cChannel.getValue(now);
				final int output = MathUtils.clamp((int) (value * maxvalue), 0,
						maxvalue);
				this.deviceOutput.write(output);

			}

			this.deviceOutput.end();

			if (this.isDebug()) {
				final String format = String.format("%x",
						this.protocol.getEndFlag()).toUpperCase(Locale.ENGLISH);
				buf.append(format);
				if (format.length() == 1) {
					buf.append("0");
				}
			}

			if (this.isDebug()) {
				LOG.debug(buf.toString());
			}
			// LOG.info(buf.toString());

		} catch (final IOException e) {
			throw new BoblightDeviceException(this.getName(), e);
		}

		// if (m_allowsync) {
		// try {
		// synchronized (m_timer) {
		// m_timer.wait();
		// }
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }

	}

	// @Override
	// public void serialEvent(SerialPortEvent arg0) {
	//
	// }

}
