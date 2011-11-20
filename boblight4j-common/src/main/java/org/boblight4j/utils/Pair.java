package org.boblight4j.utils;

public class Pair<T1, T2> {

	private final T1 key;
	private T2 value;

	public Pair(final T1 first, final T2 second) {
		this.key = first;
		this.value = second;
	}

	public final T1 getKey() {
		return this.key;
	}

	public final T2 getValue() {
		return this.value;
	}

	public final void setValue(final T2 value) {
		this.value = value;
	}

}
