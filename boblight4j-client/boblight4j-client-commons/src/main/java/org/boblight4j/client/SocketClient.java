package org.boblight4j.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.boblight4j.Constants;
import org.boblight4j.client.mbean.ClientAccessor;
import org.boblight4j.exception.BoblightCommunicationException;
import org.boblight4j.exception.BoblightException;
import org.boblight4j.utils.MBeanUtils;
import org.boblight4j.utils.Message;
import org.boblight4j.utils.MessageQueue;
import org.boblight4j.utils.Misc;
import org.boblight4j.utils.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The <code>Client</code> class is the client part of the client server architecture of boblight4j. It's main purpose is to communicate with the server and to
 * hold information about the color of the lights.<br>
 * <br>
 * 
 * boblight equivalent: boblight-client.cpp CBoblight
 * 
 * @author agebauer
 * 
 */
public class SocketClient extends AbstractRemoteClient {

    private static final String CMD_SET_LIGHT = "set light ";

    static final Logger LOG = LoggerFactory.getLogger(SocketClient.class);

    private static final String PROTOCOLVERSION = "5";
    private static final int DEFAULT_PORT = 19333;
    private static final int MAXDATA = 100000;

    private String address;
    private int port;
    /**
     * timeout in milliseconds.
     */
    private int mSecTimeout;

    private final MessageQueue messageQueue = new MessageQueue();

    private SocketChannel socketChannel;

    /**
     * Default constructor.
     */
    public SocketClient(final LightsHolder lightsHolder) {
	super(lightsHolder);
	MBeanUtils.registerBean("org.boblight.client:type=Lights",
		new ClientAccessor(this));
    }

    /**
     * Connects the client to the server.<br>
     * Additionally we hand shake with the server, check for same client and server version and retrieve the lights configuration from the server.
     * 
     * @param address
     *            the server address
     * @param port
     *            the port to connect to
     * @param mSecTimeout
     *            the connection timeout in milliseconds
     * @throws BoblightException
     */
    public void connect(final String address, final int port,
	    final int mSecTimeout) throws BoblightException {

	Message message;
	// CTcpData data;
	// int64_t now;
	// int64_t target;
	String word;

	// set address
	this.mSecTimeout = mSecTimeout;
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

	    message = this.messageQueue.nextMessage();
	    if (!this.parseWord(message, "hello"))
	    {
		throw new BoblightCommunicationException(this.address + ":"
			+ this.port + " sent gibberish: "
			+ message.message.toString());
	    }

	    // get the protocol version from the server
	    this.writeDataToSocket("get version\n");

	    this.readDataToQueue();

	    message = this.messageQueue.nextMessage();

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

	    message = this.messageQueue.nextMessage();

	    this.parseLights(message);
	} catch (final IOException e)
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
	} catch (IOException e)
	{
	    LOG.error("Error during close socket.", e);
	}
    }

    public Message nextMessage() throws BoblightCommunicationException {
	// read some data to the message queue if we have no messages
	if (this.messageQueue.getNrMessages() == 0)
	{
	    this.readDataToQueue();
	}
	return this.messageQueue.nextMessage();
    }

    public void ping(final Pointer<Integer> outputused, final boolean send)
	    throws BoblightException {
	String word;

	if (send)
	{
	    this.writeDataToSocket("ping\n");
	}

	this.readDataToQueue();

	final Message message = this.messageQueue.nextMessage();

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
		Integer.valueOf(word);
		if ((word = Misc.getWord(message.message)) == null)
		{
		    throw new BoblightException(this.address + ":" + this.port
			    + " sent gibberish");
		}
	    } catch (final NumberFormatException e)
	    {
		throw new BoblightException(this.address + ":" + this.port
			+ " sent gibberish", e);
	    }

	}

    }

    /**
     * Will read data from the socket and add it to the queue until a timeout set by {@link #mSecTimeout} occurs.
     * 
     * @throws BoblightCommunicationException
     */
    private void readDataToQueue() throws BoblightCommunicationException {
	try
	{
	    long now = System.currentTimeMillis();
	    final long target = now + this.mSecTimeout;
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
			    "No data available. EOS.");
		}
		line = new String(data, 0, read);

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
	} catch (final IOException e)
	{
	    throw new BoblightCommunicationException(e);
	}
    }

    /**
     * Sends the rgb values of each light to the tcp server.
     * 
     * @param sync
     * @param outputused
     * @throws IOException
     * @throws BoblightException
     */
    public void sendRgb(final boolean sync, final Pointer<Integer> outputused)
	    throws BoblightException {
	final StringBuilder data = new StringBuilder();

	for (Light light : this.getLightsHolder().getLights())
	{
	    final float[] rgb = light.getRgb();
	    data.append(CMD_SET_LIGHT + light.getName() + " rgb " + rgb[0]
		    + " " + rgb[1] + " " + rgb[2] + "\n");
	    if (light.getAutospeed() > 0.0 && light.getSinglechange() > 0.0)
	    {
		data.append(CMD_SET_LIGHT + light.getName() + " singlechange "
			+ light.getSinglechange() + "\n");
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

    public void sendOption(Light light, String option) throws BoblightException {
	this.writeDataToSocket(CMD_SET_LIGHT + light.getName() + " " + option
		+ "\n");
    }

    /**
     * Creates a set priority client message and calls writeDataToSocket.
     * 
     * @param priority
     * @throws BoblightException
     */
    public void sendPriority(final int priority) throws BoblightException {
	this.writeDataToSocket("set priority " + priority + "\n");
    }

    /**
     * Writes a <code>String</code> to the socket using a PrintWriter and
     * 
     * @param string
     * @throws BoblightException
     */
    private void writeDataToSocket(final String string)
	    throws BoblightException {
	if (this.socketChannel == null)
	{
	    throw new IllegalStateException(
		    "Ensure to connect to server before writing data.");
	}
	if (!this.socketChannel.isConnected() || !this.socketChannel.isOpen()
		|| this.socketChannel.socket().isOutputShutdown())
	{
	    throw new BoblightCommunicationException(
		    "Server closed connection unexpectedly.");
	}
	try
	{
	    this.socketChannel.write(ByteBuffer.wrap(string.getBytes()));
	} catch (final IOException e)
	{
	    throw new BoblightCommunicationException(e);
	}

    }

    @Override
    public boolean setup(FlagManager flagManager) {

	this.address = flagManager.getAddress();
	this.port = flagManager.getPort();

	try
	{

	    LOG.info("Connecting to boblightd");

	    // try to connect, if we can't then bitch to stderr and destroy
	    // boblight
	    connect(this.address, this.port, Constants.CONNECTION_TIMEOUT);

	    sendPriority(flagManager.getPriority());
	} catch (final BoblightException e)
	{
	    LOG.info("Waiting 10 seconds before trying again", e);
	    destroy();
	    try
	    {
		Thread.sleep(Constants.RETRY_DELAY_ERROR);
	    } catch (final InterruptedException ex)
	    {
		LOG.warn("", ex);
	    }
	    return false;
	}
	return true;

    }

}
