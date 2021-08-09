package de.dasbabypixel.waveclient.module.core.module;

import net.minecraftforge.fml.common.eventhandler.Event;

public class ModuleConfigureEvent extends Event {

	private final Module module;

	public ModuleConfigureEvent(Module module) {
		this.module = module;
	}

	public Module getModule() {
		return module;
	}

}
