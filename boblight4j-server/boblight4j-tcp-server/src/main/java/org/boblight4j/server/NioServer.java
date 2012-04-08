package org.boblight4j.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.boblight4j.exception.BoblightException;

public class NioServer implements Runnable {
	private static final Logger LOG = Logger.getLogger(NioServer.class);

	private boolean accept = true;
	// The host:port combination to listen on
	private final InetAddress hostAddress;

	private final Map<SocketChannel, List<ByteBuffer>> pendingData = new HashMap<SocketChannel, List<ByteBuffer>>();

	private final int port;

	// The buffer into which we'll read data when it's available
	private final ByteBuffer readBuffer = ByteBuffer.allocate(8192);
	// The selector we'll be monitoring
	private final Selector selector;
	private boolean stop;
	private final ClientsHandler worker;

	public NioServer(final InetAddress hostAddress, final int port,
			final ClientsHandler worker) throws IOException {
		this.hostAddress = hostAddress;
		this.port = port;
		this.worker = worker;
		this.selector = this.initSelector();
	}

	private void accept(final SelectionKey key) throws IOException {
		// For an accept to be pending the channel must be a server socket
		// channel.
		final ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key
				.channel();

		// Accept the connection and make it non-blocking
		final SocketChannel socketChannel = serverSocketChannel.accept();

		LOG.info(String.format("%s connected", socketChannel.socket()
				.getRemoteSocketAddress()));

		this.worker.addClient(new Client(socketChannel));

		socketChannel.configureBlocking(false);

		// Register the new SocketChannel with our Selector, indicating
		// we'd like to be notified when there's data waiting to be read
		socketChannel.register(this.selector, SelectionKey.OP_READ);
	}

	public final void blockAccept(final boolean doBlock) {
		this.accept = !doBlock;
	}

	private Selector initSelector() throws IOException {
		// Create a new selector
		final Selector socketSelector = SelectorProvider.provider()
				.openSelector();

		// Create a new non-blocking server socket channel
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);

		// Bind the server socket to the specified address and port
		final InetSocketAddress isa = new InetSocketAddress(this.hostAddress,
				this.port);
		serverChannel.socket().bind(isa);

		// Register the server socket channel, indicating an interest in
		// accepting new connections
		serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);

		return socketSelector;
	}

	private void read(final SelectionKey key) throws IOException {
		final SocketChannel socketChannel = (SocketChannel) key.channel();

		// Clear out our read buffer so it's ready for new data
		this.readBuffer.clear();

		// Attempt to read off the channel
		int numRead;
		try
		{
			numRead = socketChannel.read(this.readBuffer);
		}
		catch (final IOException e)
		{
			// The remote forcibly closed the connection, cancel
			// the selection key and close the channel.
			key.cancel();
			socketChannel.close();
			return;
		}

		if (numRead == -1)
		{
			// Remote entity shut the socket down cleanly. Do the
			// same from our end and cancel the channel.
			key.channel().close();
			key.cancel();
			return;
		}

		try
		{
			// Hand the data off to our worker thread
			this.worker.handleMessages(socketChannel, this.readBuffer.array(),
					numRead);
		}
		catch (final BoblightException e)
		{
			LOG.error("Exception while handling messages. Removing client.", e);
			this.worker.removeClient(socketChannel);
		}
	}

	@Override
	public final void run() {
		while (!this.stop)
		{
			try
			{
				// Wait for an event one of the registered channels
				this.selector.select();

				// Iterate over the set of keys for which events are available
				final Iterator<SelectionKey> selectedKeys = this.selector
						.selectedKeys().iterator();
				while (selectedKeys.hasNext())
				{
					final SelectionKey key = selectedKeys.next();
					selectedKeys.remove();

					if (!key.isValid())
					{
						continue;
					}

					// Check what event is available and deal with it
					if (key.isAcceptable() && this.accept)
					{
						this.accept(key);
					}
					else if (key.isReadable())
					{
						this.read(key);
					}
					else if (key.isWritable())
					{
						this.write(key);
					}
				}
				Thread.sleep(2);
			}
			catch (final Exception e)
			{
				LOG.error("NioServer exception.", e);
			}
		}
	}

	public final void stop() {
		this.stop = true;
	}

	private void write(final SelectionKey key) throws IOException {
		final SocketChannel socketChannel = (SocketChannel) key.channel();

		synchronized (this.pendingData)
		{
			final List<ByteBuffer> queue = this.pendingData.get(socketChannel);

			// Write until there's not more data ...
			while (!queue.isEmpty())
			{
				final ByteBuffer buf = queue.get(0);

				LOG.info("Writing " + new String(buf.array()));
				socketChannel.write(buf);
				if (buf.remaining() > 0)
				{
					// ... or the socket's buffer fills up
					break;
				}
				queue.remove(0);
			}

			if (queue.isEmpty())
			{
				// We wrote away all data, so we're no longer interested
				// in writing on this socket. Switch back to waiting for
				// data.
				key.interestOps(SelectionKey.OP_READ);
			}
		}
	}
}