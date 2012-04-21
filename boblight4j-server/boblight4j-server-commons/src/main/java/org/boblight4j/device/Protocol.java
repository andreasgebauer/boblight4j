package org.boblight4j.device;

public class Protocol {

	private int endFlag;
	private int escapeFlag;
	private int startFlag;

	/**
	 * throws IllegalStateException when values do not differ
	 */
	public void checkValid() {
		if (this.getStartFlag() == this.getEndFlag()
				|| this.getStartFlag() == this.getEscapeFlag()
				|| this.getEscapeFlag() == this.getEndFlag())
		{
			throw new IllegalStateException(
					"StartFlag, EndFlag and EscapeFlag must be different values.");
		}
	}

	public int getEndFlag() {
		return this.endFlag;
	}

	public int getEscapeFlag() {
		return this.escapeFlag;
	}

	public int getStartFlag() {
		return this.startFlag;
	}

	public Protocol setEndFlag(final int end) {
		this.endFlag = end;
		return this;
	}

	public Protocol setEscapeFlag(final int escapeFlag) {
		this.escapeFlag = escapeFlag;
		return this;
	}

	public Protocol setStartFlag(final int sf) {
		this.startFlag = sf;
		return this;
	}
}
