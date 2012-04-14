package org.boblight4j.server.config;

/**
 * JMX bean interface.
 * 
 * @author agebauer
 * 
 */
public interface ConfigAccessorMBean {

	/**
	 * Returns the lines of the global configuration.
	 * 
	 * @return the lines of the global configuration
	 */
	String[] getGlobalConfigLines();
}
