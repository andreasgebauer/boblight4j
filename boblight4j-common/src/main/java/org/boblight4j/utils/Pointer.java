package org.boblight4j.utils;

/**
 * Pointer representation.
 * 
 * @param <T>
 *            the type to point to
 * 
 * @author agebauer
 */
public class Pointer<T> {

	/**
	 * Creates a Pointer for the given object.
	 * 
	 * @param object
	 *            the object to wrap
	 * @return a pointer to the given object
	 */
	public static <T> Pointer<T> of(final T object) {
		return new Pointer<T>(object);
	}

	private T value;

	public Pointer() {
		this(null);
	}

	public Pointer(final T value) {
		this.assign(value);
	}

	public final void assign(final T value) {
		this.value = value;
	}

	public final boolean eq(final T other) {
		return this.value.equals(other);
	}

	public final T get() {
		return this.value;
	}
}
