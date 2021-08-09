package de.dasbabypixel.waveclient.module.core.module;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class ModuleData {

	private Map<String, ModuleDataObject> data;

	public ModuleData(Map<String, ModuleDataObject> data) {
		this.data = data;
	}

	public boolean has(String key) {
		return data.containsKey(key);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends ModuleDataObject> T get(String key, T def) {
		return (T) data.getOrDefault(key, def);
	}

	@SuppressWarnings("unchecked")
	public <T extends ModuleDataObject> T get(String key) {
		return (T) data.get(key);
	}

	public <T extends ModuleDataObject> void set(String key, T value) {
		data.put(key, value);
	}

	public JsonArray serialize() {
		JsonArray array = new JsonArray();
		for (Map.Entry<String, ModuleDataObject> entry : data.entrySet()) {
			array.add(new Entry(entry.getKey(), entry.getValue().getClass(), entry.getValue().serialize()).serialize());
		}
		return array;
	}

	public static ModuleData deserialize(JsonArray array) {
		Map<String, ModuleDataObject> data = new HashMap<>();
		for (int i = 0; i < array.size(); i++) {
			try {
				Entry entry = Entry.deserialize(array.get(i).getAsJsonObject());
				String name = entry.name;
				ModuleDataObject object = (ModuleDataObject) entry.clazz.newInstance();
				object.deserialize(entry.data);
				data.put(name, object);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return new ModuleData(data);
	}

	private static class Entry {

		private final String name;
		private final Class<?> clazz;
		private final JsonElement data;

		public Entry(String name, Class<?> clazz, JsonElement data) {
			this.name = name;
			this.clazz = clazz;
			this.data = data;
		}

		public JsonObject serialize() {
			JsonObject object = new JsonObject();
			object.add("name", new JsonPrimitive(name));
			object.add("class", new JsonPrimitive(clazz.getName()));
			object.add("data", data);
			return object;
		}

		public static Entry deserialize(JsonObject object) {
			try {
				return new Entry(object.get("name").getAsString(), Class.forName(object.get("class").getAsString()),
						object.get("data"));
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			}
			return null;
		}
	}
}
