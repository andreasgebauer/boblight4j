package org.boblight4j.server;

import java.net.InetAddress;

import org.boblight4j.exception.BoblightException;
import org.boblight4j.server.config.Config;
import org.boblight4j.server.config.ConfigCreator;
import org.boblight4j.server.config.ConfigCreatorBase;
import org.boblight4j.server.config.ConfigLine;
import org.boblight4j.server.config.ConfigReader;
import org.boblight4j.server.config.TcpServerConfigImpl;
import org.boblight4j.utils.Misc;
import org.boblight4j.utils.Pointer;

public class TcpServerConfigCreator extends ConfigCreatorBase implements
		ConfigCreator {

	private final ConfigReader configReader2;

	public TcpServerConfigCreator(ConfigReader configReader) {
		super(configReader);
		configReader2 = configReader;
	}

	@Override
	public TcpServerConfigImpl loadConfig(ClientsHandler<?> clients,
			Config config) throws BoblightException {
		TcpServerConfigImpl loadConfig = new TcpServerConfigImpl(
				this.configReader2.getFileName());
		super.loadConfig(clients, loadConfig);
		return loadConfig;
	}

	@Override
	protected void buildClientsHandlerConfig(ClientsHandler<?> clients)
			throws Exception {
		// empty string means bind to *
		InetAddress ifc = null;
		int port = 19333; // default port
		for (ConfigLine line : this.configReader2.getGlobalConfigLines()) {
			Pointer<String> pntr = new Pointer<String>(line.line);
			String word = Misc.getWord(pntr);
			if (word.equals("interface")) {
				ifc = InetAddress.getByName(Misc.getWord(pntr));
			} else if (word.equals("port")) {
				word = Misc.getWord(pntr);
				port = Integer.valueOf(word);
				if (port < 0 || port > 65535) {
					throw new BoblightException(
							"Port must be integer within range 0 and 65535");
				}
			}
		}
		((SocketClientsHandlerImpl) clients).setInterface(ifc, port);
	}

}
