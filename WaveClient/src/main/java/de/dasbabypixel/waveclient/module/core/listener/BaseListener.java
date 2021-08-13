package de.dasbabypixel.waveclient.module.core.listener;

import java.util.function.Consumer;

import de.dasbabypixel.waveclient.wrappedloader.api.event.Event;
import de.dasbabypixel.waveclient.wrappedloader.api.event.EventListener;

public class BaseListener<T extends Event> extends AbstractListener implements EventListener {

	private final Consumer<T> consumer;
	private final int priority;
	private Object listener;
	private final Class<T> clazz;

	public BaseListener(Class<T> clazz, Consumer<T> consumer) {
		this(clazz, consumer, 0);
	}

	public BaseListener(Class<T> clazz, Consumer<T> consumer, int priority) {
		this.clazz = clazz;
		this.consumer = consumer;
		this.priority = priority;
	}

	@Override
	public void invoke(Event e) {
		if (clazz.isAssignableFrom(e.getClass())) {
			consumer.accept(clazz.cast(e));
		}
	}
	
	public int getPriority() {
		return priority;
	}
}
