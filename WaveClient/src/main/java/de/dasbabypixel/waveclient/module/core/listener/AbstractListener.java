package de.dasbabypixel.waveclient.module.core.listener;

import de.dasbabypixel.waveclient.module.core.CoreModule;

public abstract class AbstractListener {

	public void register() {
		CoreModule.getInstance().getEventManager().register(this);
	}

	public void unregister() {
		CoreModule.getInstance().getEventManager().unregister(this);
	}
}
