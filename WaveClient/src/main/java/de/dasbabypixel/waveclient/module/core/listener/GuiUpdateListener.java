package de.dasbabypixel.waveclient.module.core.listener;

import de.dasbabypixel.waveclient.module.core.gui.api.BackgroundGuiRainbow;
import de.dasbabypixel.waveclient.module.core.gui.api.ScalingManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class GuiUpdateListener extends AbstractListener {

	@SubscribeEvent
	public void handle(ClientTickEvent event) {
		ScalingManager.updateScaleFactor();
		BackgroundGuiRainbow.update();
	}
}
