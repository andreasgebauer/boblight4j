package org.boblight4j.client;

import java.util.List;

import org.boblight4j.cli.BooleanOptionHandler;
import org.boblight4j.cli.BoblightOptionHandler;
import org.boblight4j.cli.DefaultArgs;
import org.boblight4j.cli.PriorityOptionHandler;
import org.boblight4j.cli.ServerOptionHandler;
import org.boblight4j.exception.BoblightRuntimeException;
import org.kohsuke.args4j.Option;

public class CommandLineArgs {

	public static class Server {

		private String address = "localhost";
		private int port = -1;

		public Server(String string) {
			parse(string);
		}

		private void parse(String server) {
			if (server != null && server.indexOf(':') != -1) {
				this.address = server.substring(0, server.indexOf(':'));
				server = server.substring(server.indexOf(':') + 1);
				try {
					this.port = Integer.valueOf(server);
				} catch (final NumberFormatException nfe) {
					throw new BoblightRuntimeException("Wrong option " + server
							+ " for argument -s");
				}

				if (this.port != -1 && this.port < 0 || this.port > 65535) {
					throw new BoblightRuntimeException("Wrong option " + server
							+ " for argument -s");
				}
			}
		}

		public String getAddress() {
			return this.address;
		}

		public int getPort() {
			return this.port;
		}

	}

	@Option(name = "-l", usage = "list libboblight options")
	private boolean printBoblightOptions;

	@Option(name = "-o", handler = BoblightOptionHandler.class, usage = "add libboblight option, syntax: [light:]option=value")
	private List<String> options;

	@Option(name = "-p", metaVar = "priority", usage = "from 0 to 255, default is 128", handler = PriorityOptionHandler.class)
	private int priority = DefaultArgs.PRIORITY;

	@Option(name = "-s", metaVar = "address:[port]", usage = "set the address and optional port to connect to", handler = ServerOptionHandler.class)
	private Server server = new Server("localhost:19333");

	@Option(name = "-y", handler = BooleanOptionHandler.class, metaVar = "sync", usage = "set the sync mode, default is on, valid options are \"on\" and \"off\"")
	private boolean sync;

	@Option(name = "-h", metaVar = "help", usage = "print this help message")
	private boolean printHelp;

	public boolean isPrintBoblightOptions() {
		return printBoblightOptions;
	}

	public void setPrintBoblightOptions(boolean printBoblightOptions) {
		this.printBoblightOptions = printBoblightOptions;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Server getServer() {
		return server;
	}

	public boolean isSync() {
		return sync;
	}

	public void setSync(boolean sync) {
		this.sync = sync;
	}

	public boolean isPrintHelp() {
		return printHelp;
	}

	public void setPrintHelp(boolean printHelp) {
		this.printHelp = printHelp;
	}

}