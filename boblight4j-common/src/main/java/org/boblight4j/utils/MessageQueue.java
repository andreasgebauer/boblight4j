package org.boblight4j.utils;

import java.util.ArrayDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageQueue {

	private static final Logger LOG = LoggerFactory.getLogger(MessageQueue.class);

	private final ArrayDeque<Message> messages = new ArrayDeque<Message>();
	private final Message remainingData = new Message();

	public void addData(final String packet) {
		LOG.trace(String.format("Adding data %s", packet));

		final long now = System.currentTimeMillis();
		String data = packet;
		int nlpos = data.indexOf('\n'); // position of the newline

		// no newline
		if (nlpos == -1)
		{
			// set the timestamp if there's no remaining data
			if (this.remainingData.message.length() == 0)
			{
				this.remainingData.time = now;
			}

			this.remainingData.message.append(data);
			return;
		}

		// add the data from the last time
		// if there is none, use the now timestamp
		Message message = new Message(this.remainingData);
		if (message.message == null)
		{
			message.time = now;
			message.message = new StringBuilder();
		}

		while (nlpos != -1)
		{
			message.message.append(data.substring(0, nlpos) + "\n"); // get the
																		// string
			// until
			// the newline
			this.messages.add(message); // put the message in the queue

			// reset the message
			// message.message = "";
			message.time = now;

			// if the newline is at the end of the string, we're done here
			if (nlpos + 1 >= data.length())
			{
				data = "";
				break;
			}

			data = data.substring(nlpos + 1); // remove all the data up to and
												// including the newline
			nlpos = data.indexOf('\n'); // search for a new newline

			message = new Message();
		}

		// save the remaining data with the timestamp
		this.remainingData.message = new StringBuilder(data);
		this.remainingData.time = now;
	}

	public Message getMessage() {
		if (this.messages.isEmpty())
		{
			return null;
		}
		return this.messages.poll();
	}

	public int getNrMessages() {
		return this.messages.size();
	}

	public int getRemainingDataSize() {
		return this.remainingData.message.length();
	}
}
