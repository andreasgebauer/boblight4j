package org.boblight4j.client;

import java.util.List;

import org.kohsuke.args4j.Option;

public class CommandLineArgs {
	@Option(name = "-l", usage = "list libboblight options")
	boolean printBoblightOptions;

	@Option(name = "-o", handler = CustomOptionHandler.class, usage = "add libboblight option, syntax: [light:]option=value")
	List<String> options;

	@Option(name = "-p", metaVar = "priority", usage = "from 0 to 255, default is 128")
	int priority = 128;

	@Option(name = "-s", metaVar = "address:[port]", usage = "set the address and optional port to connect to")
	String server = "localhost:19333";

	@Option(name = "-y", handler = BooleanOptionHandler.class, metaVar = "sync", usage = "set the sync mode, default is on, valid options are \"on\" and \"off\"")
	boolean sync;

	@Option(name = "-h", metaVar = "help", usage = "print this help message")
	boolean printHelp;
}