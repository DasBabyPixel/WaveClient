package de.dasbabypixel.waveclient.module.core.module;

import com.google.gson.JsonElement;

public interface ModuleDataObject {

	JsonElement serialize();
	
	void deserialize(JsonElement json);
	
}
