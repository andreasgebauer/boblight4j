package org.boblight4j.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.boblight4j.client.mbean.ClientAccessor;
import org.boblight4j.exception.BoblightCommunicationException;
import org.boblight4j.exception.BoblightConfigurationException;
import org.boblight4j.exception.BoblightException;
import org.boblight4j.exception.BoblightParseException;
import org.boblight4j.utils.MBeanUtils;
import org.boblight4j.utils.MathUtils;
import org.boblight4j.utils.Message;
import org.boblight4j.utils.MessageQueue;
import org.boblight4j.utils.Misc;
import org.boblight4j.utils.Pointer;
import org.boblight4j.utils.StdIO;

/**
 * The <code>Client</code> class is the client part of the client server
 * architecture of boblight4j. It's main purpose is to communicate with the
 * server.<br>
 * <br>
 * 
 * boblight equivalent: boblight-client.cpp CBoblight
 * 
 * @author agebauer
 * 
 */
public class Client {

	private static final Logger LOG = Logger.getLogger(Client.class);

	private static final String PROTOCOLVERSION = "5";
	private static final int DEFAULT_PORT = 19333;
	private static final int MAXDATA = 100000;

	private String address;
	private int port;
	/**
	 * timeout in milliseconds
	 */
	private int uSecTimeout;

	private final List<Light> lights = new ArrayList<Light>();
	private final MessageQueue messageQueue = new MessageQueue();

	private SocketChannel socketChannel;

	/**
	 * Default constructor.
	 */
	public Client() {
		MBeanUtils.registerBean("org.boblight.client:type=Lights",
				new ClientAccessor(this));
	}

	/**
	 * Sets the color of a screen pixel.
	 * 
	 * @param xPos
	 * @param yPos
	 * @param rgb
	 */
	public void addPixel(final int xPos, final int yPos, final int[] rgb) {
		for (int i = 0; i < this.lights.size(); i++)
		{
			final Light light = this.lights.get(i);
			if (yPos >= light.getHScanScaled()[0]
					&& yPos <= light.getHScanScaled()[1]
					&& xPos >= light.getVScanScaled()[0]
					&& xPos <= light.getVScanScaled()[1])
			{
				// any of the three color values must be greater than the
				// threshold
				if (rgb[0] >= light.getThreshold()
						|| rgb[1] >= light.getThreshold()
						|| rgb[2] >= light.getThreshold())
				{
					light.rgb[0] += MathUtils.clamp(rgb[0], 0, 255);
					light.rgb[1] += MathUtils.clamp(rgb[1], 0, 255);
					light.rgb[2] += MathUtils.clamp(rgb[2], 0, 255);
				}
				light.rgb[3]++;
			}
		}
	}

	/**
	 * Sets the color of a light.
	 * 
	 * @param lightnr
	 *            the index of the light
	 * @param rgb
	 *            the RGB value
	 * @throws BoblightException
	 */
	public final void addPixel(final int lightnr, final int[] rgb)
			throws BoblightException {
		this.checkLightExists(lightnr);

		if (lightnr < 0)
		{
			for (int i = 0; i < this.lights.size(); i++)
			{
				final Light light = this.lights.get(i);
				if (rgb[0] >= light.getThreshold()
						|| rgb[1] >= light.getThreshold()
						|| rgb[2] >= light.getThreshold())
				{
					light.rgb[0] += MathUtils.clamp(rgb[0], 0, 255);
					light.rgb[1] += MathUtils.clamp(rgb[1], 0, 255);
					light.rgb[2] += MathUtils.clamp(rgb[2], 0, 255);
				}
				light.rgb[3]++;
			}
		}
		else
		{
			final Light light = this.lights.get(lightnr);
			if (rgb[0] >= light.getThreshold()
					|| rgb[1] >= light.getThreshold()
					|| rgb[2] >= light.getThreshold())
			{
				light.rgb[0] += MathUtils.clamp(rgb[0], 0, 255);
				light.rgb[1] += MathUtils.clamp(rgb[1], 0, 255);
				light.rgb[2] += MathUtils.clamp(rgb[2], 0, 255);
			}
			light.rgb[3]++;
		}

	}

	/**
	 * Checks if a light with the index given exists. Throws
	 * {@link BoblightException} when the light doesn't exist.
	 * 
	 * @param lightNr
	 * @throws BoblightException
	 */
	private void checkLightExists(final int lightNr) throws BoblightException {
		if (lightNr >= this.lights.size())
		{
			throw new BoblightException("light " + lightNr
					+ " doesn't exist (have " + this.lights.size() + " lights)");
		}
	}

	/**
	 * Connects the client to the server.
	 * 
	 * @param address
	 *            the server address
	 * @param port
	 *            the port to connect to
	 * @param usectimeout
	 *            the connection timeout in milliseconds
	 * @throws BoblightException
	 */
	public void connect(final String address, final int port,
			final int usectimeout) throws BoblightException {

		Message message;
		// CTcpData data;
		// int64_t now;
		// int64_t target;
		String word;

		// set address
		this.uSecTimeout = usectimeout;
		if (address == null)
		{
			this.address = "127.0.0.1";
		}
		else
		{
			this.address = address;
		}

		// set port
		if (port >= 0)
		{
			this.port = port;
		}
		else
		{
			// set to default port
			this.port = DEFAULT_PORT;
		}

		try
		{
			this.socketChannel = SocketChannel.open();
			// socketChannel.configureBlocking(false);

			// try to open a tcp connection
			this.socketChannel.connect(new InetSocketAddress(this.address,
					this.port));

			// write hello to the server, we should get hello back
			this.writeDataToSocket("hello\n");

			this.readDataToQueue();

			message = this.messageQueue.getMessage();
			if (!this.parseWord(message, "hello"))
			{
				throw new BoblightCommunicationException(this.address + ":"
						+ this.port + " sent gibberish");
			}

			// get the protocol version from the server
			this.writeDataToSocket("get version\n");

			this.readDataToQueue();

			message = this.messageQueue.getMessage();

			if (!this.parseWord(message, "version")
					|| (word = Misc.getWord(message.message)) == null)
			{
				throw new BoblightCommunicationException(this.address + ":"
						+ this.port + " sent gibberish");
			}

			// if we don't get the same protocol version back as we have, we
			// can't work together
			if (!word.equals(PROTOCOLVERSION))
			{
				throw new BoblightCommunicationException("version mismatch, "
						+ this.address + ":" + this.port + " has version \""
						+ word + "\", libboblight has version \""
						+ PROTOCOLVERSION + "\"");
			}

			// get lights info, like number, names and area
			this.writeDataToSocket("get lights\n");

			this.readDataToQueue();

			message = this.messageQueue.getMessage();

			this.parseLights(message);
		}
		catch (final IOException e)
		{
			throw new BoblightCommunicationException(e);
		}

	}

	/**
	 * Destroy the client. Does nothing right now.
	 */
	public void destroy() {
		try
		{
			this.socketChannel.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Returns the light name for the integer given.
	 * 
	 * @param lightnr
	 *            the zero-based index of the light
	 * @return the light's name
	 * @throws BoblightException
	 */
	public String getLightName(int lightnr) throws BoblightException {
		// negative lights don't exist, so we set it to an
		// invalid number to get the error message
		if (lightnr < 0)
		{
			lightnr = this.lights.size();
		}

		this.checkLightExists(lightnr);

		return this.lights.get(lightnr).getName();
	}

	public List<Light> getLights() {
		return this.lights;
	}

	public int getNrLights() {
		return this.lights.size();
	}

	/**
	 * Parses the response message to the get lights request.
	 * 
	 * @param message
	 *            the message to parse
	 * @return true in case of parsing succeeds, false otherwise
	 * @throws BoblightException
	 */
	private void parseLights(Message message) throws BoblightException {
		String word;
		int nrlights = 0;

		// first word in the message is "lights", second word is the number of
		// lights
		if (!this.parseWord(message, "lights")
				|| (word = Misc.getWord(message.message)) == null)
		{
			throw new BoblightCommunicationException(
					"Unable to parse number of lights.");
		}

		try
		{
			nrlights = Integer.parseInt(word);
			if (nrlights < 1)
			{
				throw new BoblightConfigurationException(
						"Number of lights is negative.");
			}
		}
		catch (final NumberFormatException e)
		{
			throw new BoblightParseException(
					"Number of lights is unparseable.", e);
		}

		final LightConfigMBean gblCfg = new LightConfig();
		MBeanUtils.registerBean("org.boblight4j.client:type=GlobalLightConfig",
				gblCfg);

		for (int i = 0; i < nrlights; i++)
		{
			final Light light = new Light(gblCfg);

			// read some data to the message queue if we have no messages
			if (this.messageQueue.getNrMessages() == 0)
			{
				this.readDataToQueue();
			}

			message = this.messageQueue.getMessage();

			// first word sent is "light, second one is the name
			String lightName;
			if (!this.parseWord(message, "light")
					|| (lightName = Misc.getWord(message.message)) == null)
			{
				throw new BoblightParseException(
						"Cannot parse light name. Received following message: "
								+ message.message.toString());
			}
			light.setName(lightName);

			MBeanUtils.registerBean("org.boblight4j.client:type=LightConfig ["
					+ light.getName(), light);

			// third one is "scan"
			if (!this.parseWord(message, "scan"))
			{
				throw new BoblightParseException(
						"Cannot parse light scan range. Identifier 'scan' wasn't found.");
			}

			// now we read the scanrange
			final StringBuilder scanarea = new StringBuilder("");
			for (int j = 0; j < 4; j++)
			{
				if ((word = Misc.getWord(message.message)) == null)
				{
					throw new BoblightParseException("Cannot parse scan range.");
				}
				scanarea.append(word).append(' ');
			}

			// ConvertFloatLocale(scanarea); // workaround for locale mismatch
			// (,
			// and .)

			Object[] rs = null;
			if ((rs = StdIO.sscanf(scanarea.toString(), "%f %f %f %f")).length != 4)
			{
				throw new BoblightParseException("Cannot parse scan range.");
			}

			light.setHscan((Float) rs[0], (Float) rs[1]);
			light.setVscan((Float) rs[2], (Float) rs[3]);

			this.lights.add(light);
		}
	}

	private boolean parseWord(final Message message, final String wordtocmp)
			throws BoblightParseException {
		final String readword = Misc.getWord(message.message);
		if (!readword.equals(wordtocmp))
		{
			return false;
		}

		return true;
	}

	public void ping(final Pointer<Integer> outputused, final boolean send)
			throws BoblightException {
		String word;

		if (send)
		{
			this.writeDataToSocket("ping\n");
		}

		this.readDataToQueue();

		final Message message = this.messageQueue.getMessage();

		if ((word = Misc.getWord(message.message)) == null
				|| !word.equals("ping"))
		{
			throw new BoblightException(this.address + ":" + this.port
					+ " sent gibberish");
		}

		// client can set outputused to NULL
		// should return the value?
		if (outputused != null)
		{
			try
			{
				final Integer valueOf = Integer.valueOf(word);
				if ((word = Misc.getWord(message.message)) == null)
				{
					throw new BoblightException(this.address + ":" + this.port
							+ " sent gibberish");
				}
			}
			catch (final NumberFormatException e)
			{
				throw new BoblightException(this.address + ":" + this.port
						+ " sent gibberish", e);
			}

		}

	}

	private void readDataToQueue() throws BoblightCommunicationException {
		try
		{
			// CTcpData data;
			long now = System.currentTimeMillis();
			final long target = now + this.uSecTimeout;
			final int nrmessages = this.messageQueue.getNrMessages();

			while (now < target
					&& this.messageQueue.getNrMessages() == nrmessages)
			{
				InputStream is;
				String line = null;
				final char[] data = new char[MAXDATA];
				is = this.socketChannel.socket().getInputStream();
				final InputStreamReader br = new InputStreamReader(is);
				final int read = br.read(data);
				if (read == -1)
				{
					throw new BoblightCommunicationException(
							"No data available");
				}
				line = new String(data, 0, read);
				// if (m_socket.Read(data) != SUCCESS) {
				// m_error = m_socket.GetError();
				// return false;
				// }

				this.messageQueue.addData(line);

				if (this.messageQueue.getRemainingDataSize() > MAXDATA)
				{
					throw new BoblightCommunicationException(this.address + ":"
							+ this.port + " sent too much data");
				}

				now = System.currentTimeMillis();
			}

			if (nrmessages == this.messageQueue.getNrMessages())
			{
				throw new BoblightCommunicationException(this.address + ":"
						+ this.port + " read timed out");
			}
		}
		catch (final IOException e)
		{
			throw new BoblightCommunicationException(e);
		}
	}

	public void sendRgb(final boolean sync, final Pointer<Integer> outputused)
			throws IOException, BoblightException {
		final StringBuilder data = new StringBuilder();

		for (int i = 0; i < this.lights.size(); i++)
		{
			final float[] rgb = this.lights.get(i).getRgb();
			data.append("set light " + this.lights.get(i).getName() + " rgb "
					+ rgb[0] + " " + rgb[1] + " " + rgb[2] + "\n");
			if (this.lights.get(i).getAutospeed() > 0.0
					&& this.lights.get(i).getSinglechange() > 0.0)
			{
				data.append("set light " + this.lights.get(i).getName()
						+ " singlechange "
						+ this.lights.get(i).getSinglechange() + "\n");
			}
		}

		// send a message that we want devices to sync to our input
		if (sync)
		{
			data.append("sync\n");
		}

		// if we want to check if our output is used, send a ping message
		if (outputused != null)
		{
			data.append("ping\n");
		}

		this.writeDataToSocket(data.toString());

		if (outputused != null)
		{
			this.ping(outputused, false);
		}
	}

	/**
	 * Sets a boblight option.
	 * 
	 * @param lightnr
	 * @param option
	 * @throws BoblightException
	 */
	public void setOption(final int lightnr, final String option)
			throws BoblightException {
		this.checkLightExists(lightnr);

		if (lightnr < 0)
		{
			for (int i = 0; i < this.lights.size(); i++)
			{
				final Light light = this.lights.get(i);
				if (light.setOption(option))
				{
					this.writeDataToSocket("set light " + light.getName() + " "
							+ option + "\n");
				}
			}
		}
		else
		{
			final Light light = this.lights.get(lightnr);
			if (light.setOption(option))
			{
				this.writeDataToSocket("set light " + light.getName() + " "
						+ option + "\n");
			}
		}
	}

	/**
	 * Creates a set priority client message and calls writeDataToSocket.
	 * 
	 * @param priority
	 * @throws BoblightException
	 */
	public void setPriority(final int priority) throws BoblightException {
		this.writeDataToSocket("set priority " + priority + "\n");
	}

	/**
	 * Sets the scan range for each light. Must be called before any pixel is
	 * added with addPixel.
	 * 
	 * @param width
	 * @param height
	 */
	public void setScanRange(final int width, final int height) {
		for (int i = 0; i < this.lights.size(); i++)
		{
			final Light light = this.lights.get(i);
			light.calculateScaledScanRange(width, height);
		}
	}

	/**
	 * Writes a <code>String</code> to the socket using a PrintWriter and
	 * 
	 * @param string
	 * @throws BoblightException
	 */
	private void writeDataToSocket(final String string)
			throws BoblightException {
		if (!this.socketChannel.isConnected() || !this.socketChannel.isOpen()
				|| this.socketChannel.socket().isOutputShutdown())
		{
			throw new BoblightCommunicationException(
					"Server closed connection unexpectedly.");
		}
		try
		{
			this.socketChannel.write(ByteBuffer.wrap(string.getBytes()));
		}
		catch (final IOException e)
		{
			throw new BoblightCommunicationException(e);
		}

	}
}
