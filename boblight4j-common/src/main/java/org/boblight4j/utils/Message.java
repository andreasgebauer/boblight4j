package org.boblight4j.utils;

/**
 * This class represents the message content of a packet sent via the Boblight4J
 * protocol.
 * 
 * @author agebauer
 * 
 */
public class Message {

	public StringBuilder message = new StringBuilder();
	public long time;

	public Message() {
	}

	public Message(final Message remainingdata) {
		this.message = remainingdata.message;
		this.time = remainingdata.time;
	}

}
