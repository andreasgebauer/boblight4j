package org.boblight4j.server.config;

import org.boblight4j.server.config.Channel;

public interface Device {

	/**
	 * Sets the name of the device.
	 * 
	 * @param name
	 *            the name to set
	 */
	void setName(String name);

	/**
	 * Returns the name of this device.
	 * 
	 * @return the name of the device
	 */
	String getName();

	/**
	 * Sets the output.
	 * 
	 * @param output
	 */
	void setOutput(String output);

	/**
	 * Sets the bit rate.
	 * 
	 * @param rate
	 *            the bit rate
	 */
	void setRate(int rate);

	/**
	 * Returns the interval.
	 * 
	 * @return the interval value
	 */
	long getInterval();

	/**
	 * Sets the interval.
	 * 
	 * @param interval
	 */
	void setInterval(long interval);

	/**
	 * Returns the delay in milliseconds after opening device.
	 * 
	 * @return the delay in milliseconds after opening device
	 */
	long getDelayAfterOpen();

	/**
	 * Sets the delay value after initialising.
	 * 
	 * @param delayafteropen
	 *            the delay in milliseconds
	 */
	void setDelayAfterOpen(long delayafteropen);

	/**
	 * Returns whether debug mode is enabled for this device or not.
	 * 
	 * @return true if debug mode is enabled, false otherwise
	 */
	boolean isDebug();

	/**
	 * Sets the debug flag.
	 * 
	 * @param debug
	 *            whether debugging should be turned on
	 */
	void setDebug(boolean debug);

	/**
	 * Adds a channel.
	 * 
	 * @param chnl
	 */
	void addChannel(Channel chnl);

	/**
	 * Returns the number of channels this device is connected to.
	 * 
	 * @return the number of channels this device manages
	 */
	int getNrChannels();

	/**
	 * Sets the number of channels this device handles.
	 * 
	 * @param nrchannels
	 *            the number of channels
	 */
	void setNrChannels(int nrchannels);

	/**
	 * Returns whether sync mode is allowed for this device.
	 * 
	 * @return if sync mode is allowed
	 */
	boolean isAllowSync();

	/**
	 * Sets whether the device is allowed to operate in sync mode.
	 * 
	 * @param allowsync
	 *            whether to allow sync mode
	 */
	void setAllowSync(boolean allowsync);

	/**
	 * Returns the single change value.
	 * 
	 * @return the single change value
	 */
	float getSingleChange();

	/**
	 * Sets the single change value.
	 * 
	 * @param singleChange
	 *            the single change value
	 */
	void setSingleChange(float singleChange);

	/**
	 * Should start the main loop.
	 */
	void startThread();

	void asyncStopThread();

	void stopThread();

	/**
	 * Synchronises with the server.
	 */
	void sync();

}
