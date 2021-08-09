package de.dasbabypixel.waveclient.module.core.util;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Handler<T> {

	private Predicate<T> predicate;
	private Consumer<T> consumer;

	public Handler(Consumer<T> consumer) {
		this(t -> true, consumer);
	}

	public Handler(Predicate<T> predicate, Consumer<T> consumer) {
		this.predicate = predicate;
		this.consumer = consumer;
	}

	public void handle(T t) {
		if (predicate.test(t)) {
			consumer.accept(t);
		}
	}
}
