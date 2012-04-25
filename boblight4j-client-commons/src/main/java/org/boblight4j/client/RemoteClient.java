package org.boblight4j.client;

import org.boblight4j.exception.BoblightException;
import org.boblight4j.utils.Message;

public interface RemoteClient extends Client {

	void connect(String address, int port, int i) throws BoblightException;

	Message nextMessage() throws BoblightException;

	void sendPriority(int priority) throws BoblightException;

	void destroy();

	void sendOption(Light light, String option) throws BoblightException;

}
