package org.boblight4j.client;

import org.boblight4j.exception.BoblightConfigurationException;

/**
 * The <code>FlagManager</code> interface is used to access the parameters
 * passed to the program.
 * 
 * @author agebauer
 * 
 */
public interface FlagManager {

	/**
	 * Returns the address the client should connect to.
	 * 
	 * @return the network address
	 */
	String getAddress();

	/**
	 * Returns the port the client should use for connection.
	 * 
	 * @return the network port
	 */
	int getPort();

	/**
	 * Returns the priority the client should use.
	 * 
	 * @return the priority
	 */
	int getPriority();

	/**
	 * Should print help and exit?
	 * 
	 * @return whether we should print the help message or not
	 */
	boolean isPrintHelp();

	/**
	 * Should print options and exit?
	 * 
	 * @return whether we should print the options or not
	 */
	boolean isPrintOptions();

	/**
	 * Parses the flags for this flag manager implementation.
	 * 
	 * @param args
	 *            the program arguments
	 * @return
	 * @throws BoblightConfigurationException
	 */
	CommandLineArgs parseFlags(String[] args)
			throws BoblightConfigurationException;

	/**
	 * Prints the help message.
	 */
	void printHelpMessage();

	/**
	 * Prints all available options to stdout.
	 */
	void printOptions();

}
