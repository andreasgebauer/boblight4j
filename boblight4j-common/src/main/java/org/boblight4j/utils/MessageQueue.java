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
	// position of the newline
	int nlpos = data.indexOf('\n');

	// no newline
	if (nlpos == -1) {
	    // set the timestamp if there's no remaining data
	    if (this.remainingData.message.length() == 0) {
		this.remainingData.time = now;
	    }

	    this.remainingData.message.append(data);
	    return;
	}

	// add the data from the last time
	// if there is none, use the now timestamp
	Message message = new Message(this.remainingData);
	if (message.message == null) {
	    message.time = now;
	    message.message = new StringBuilder();
	}

	while (nlpos != -1) {
	    // get the string
	    message.message.append(data.substring(0, nlpos));
	    message.message.append('\n');

	    message.time = now;

	    // put the message in the queue
	    this.messages.add(message);

	    // if the newline is at the end of the string, we're done here
	    if (nlpos + 1 >= data.length()) {
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

    /**
     * Polls the next message. If messages are empty returns null.
     * 
     * @return next message or null
     */
    public Message nextMessage() {
	if (this.messages.isEmpty()) {
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
