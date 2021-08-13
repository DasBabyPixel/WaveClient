package de.dasbabypixel.waveclient.module.core.listener;

import de.dasbabypixel.waveclient.module.core.util.Keybind;
import de.dasbabypixel.waveclient.wrappedloader.api.event.EventHandler;
import de.dasbabypixel.waveclient.wrappedloader.api.event.events.tick.TickEvent;

public class KeybindUpdateListener extends AbstractListener {

	@EventHandler
	public void handle(TickEvent.Client event) {
		Keybind.tick();
	}
}
