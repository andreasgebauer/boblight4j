package org.boblight4j.client;

public class FlagManagerSpectrumAnalyzer extends AbstractFlagManager<CommandLineArgs> {

    private CommandLineArgs commandLineArgs;

    public FlagManagerSpectrumAnalyzer(CommandLineArgs commandLineArgs) {
	super(commandLineArgs);
    }

    @Override
    protected CommandLineArgs getArgBean() {
	return this.commandLineArgs;
    }
}
