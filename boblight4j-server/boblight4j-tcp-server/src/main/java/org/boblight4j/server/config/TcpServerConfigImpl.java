package org.boblight4j.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServerConfigImpl extends AbstractConfig {

    public static final Logger LOG = LoggerFactory.getLogger(TcpServerConfigImpl.class);

    public TcpServerConfigImpl(final String fileName) {
	super(fileName);
    }

}