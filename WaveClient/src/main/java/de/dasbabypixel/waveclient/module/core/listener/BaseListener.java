package de.dasbabypixel.waveclient.module.core.listener;

import java.util.function.Consumer;

import de.dasbabypixel.waveclient.module.core.CoreModule;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BaseListener<T extends Event> extends AbstractListener {

	private final Consumer<T> consumer;
	private final EventPriority priority;
	private Object listener;
	private final Class<T> clazz;

	public BaseListener(Class<T> clazz, Consumer<T> consumer) {
		this(clazz, consumer, EventPriority.NORMAL);
	}

	public BaseListener(Class<T> clazz, Consumer<T> consumer, EventPriority priority) {
		this.clazz = clazz;
		this.consumer = consumer;
		this.priority = priority;
		switch (this.priority) {
		case HIGH:
			this.listener = new PrioHigh();
			break;
		case HIGHEST:
			this.listener = new PrioHighest();
			break;
		case LOW:
			this.listener = new PrioLow();
			break;
		case LOWEST:
			this.listener = new PrioLowest();
			break;
		case NORMAL:
			this.listener = new PrioNormal();
			break;
		default:
			break;
		}
	}

	@Override
	public void register() {
		CoreModule.getInstance().getEventManager().register(listener);
	}

	@Override
	public void unregister() {
		CoreModule.getInstance().getEventManager().unregister(listener);
	}

	public EventPriority getPriority() {
		return priority;
	}

	public void invoke(Event e) {
		try {
			if (clazz.isAssignableFrom(e.getClass())) {
				consumer.accept(clazz.cast(e));
			}
		} catch (Exception e2) {
		}
	}

	public class PrioLowest {
		@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
		public void handle(Event event) {
			invoke(event);
		}
	}

	public class PrioLow {
		@SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
		public void handle(Event event) {
			invoke(event);
		}
	}

	public class PrioNormal {
		@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
		public void handle(Event event) {
			invoke(event);
		}
	}

	public class PrioHigh {
		@SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
		public void handle(Event event) {
			invoke(event);
		}
	}

	public class PrioHighest {
		@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
		public void handle(Event event) {
			invoke(event);
		}
	}
}
