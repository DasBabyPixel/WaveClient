package de.dasbabypixel.waveclient.module.core.listener;

import de.dasbabypixel.waveclient.wrappedloader.api.WaveClientAPI;

public abstract class AbstractListener {

	public void register() {
		WaveClientAPI.getInstance().getEventManager().registerListener(this);
	}

	public void unregister() {
		WaveClientAPI.getInstance().getEventManager().unregisterListener(this);
	}
}
