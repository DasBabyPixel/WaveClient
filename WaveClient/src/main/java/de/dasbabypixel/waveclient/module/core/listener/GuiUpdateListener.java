package de.dasbabypixel.waveclient.module.core.listener;

import de.dasbabypixel.waveclient.module.core.gui.util.BackgroundGuiRainbow;
import de.dasbabypixel.waveclient.wrappedloader.api.event.EventHandler;
import de.dasbabypixel.waveclient.wrappedloader.api.event.events.tick.TickEvent;

public class GuiUpdateListener extends AbstractListener {
	@EventHandler
	public void handle(TickEvent.Client event) {
		BackgroundGuiRainbow.update();
	}
}
