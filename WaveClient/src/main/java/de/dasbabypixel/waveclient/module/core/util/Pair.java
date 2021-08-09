package de.dasbabypixel.waveclient.module.core.util;

public class Pair<K, V> {

	private K first;
	private V second;

	public Pair(K first, V second) {
		this.first = first;
		this.second = second;
	}

	public K getFirst() {
		return first;
	}

	public V getSecond() {
		return second;
	}

	public void setFirst(K first) {
		this.first = first;
	}

	public void setSecond(V second) {
		this.second = second;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) || (obj != null && obj instanceof Pair && first.equals(((Pair<?, ?>) obj).getFirst())
				&& second.equals(((Pair<?, ?>) obj).getSecond()));
	}

	@Override
	public int hashCode() {
		return first.hashCode() * 168457 + second.hashCode();
	}
}
