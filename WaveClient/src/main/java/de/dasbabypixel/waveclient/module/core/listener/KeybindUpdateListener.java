package de.dasbabypixel.waveclient.module.core.listener;

import de.dasbabypixel.waveclient.module.core.keybind.Keybind;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class KeybindUpdateListener extends AbstractListener {

	@SubscribeEvent
	public void handle(ClientTickEvent event) {
		Keybind.tick();
	}
}
