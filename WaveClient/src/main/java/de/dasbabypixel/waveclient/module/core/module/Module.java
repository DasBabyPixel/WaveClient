package de.dasbabypixel.waveclient.module.core.module;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.dasbabypixel.waveclient.module.core.CoreModule;
import net.minecraftforge.common.MinecraftForge;

public class Module {

	public static RootModule root;

	private final String key;
	private final Collection<Module> childModules;
	private boolean enabled = false;
	private final ModuleData data;

	private final Collection<Module> childModulesView;

	public Module(final String key) {
		this(key, new HashSet<>());
	}

	public Module(final String key, Collection<Module> childModules) {
		this(key, childModules, new ModuleData(new HashMap<>()));
	}

	public Module(final String key, Collection<Module> childModules, ModuleData data) {
		this.key = key;
		this.childModules = new HashSet<>(childModules);
		this.childModulesView = Collections.unmodifiableCollection(this.childModules);
		this.data = data;

		MinecraftForge.EVENT_BUS.post(new ModuleConfigureEvent(this));
	}

	public String getKey() {
		return key;
	}

	public Module addChild(Module child) {
		this.childModules.add(child);
		return this;
	}

	public Module addChild(String key, Supplier<Module> child) {
		for (Module m : getChilds()) {
			if (m.key.equals(key)) {
				return this;
			}
		}
		return this.addChild(child.get());
	}

	public ModuleData getData() {
		return data;
	}

	public Collection<Module> getChilds() {
		return childModulesView;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public static RootModule root() {
		return root;
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}

	@Override
	public String toString() {
		return String.format("Module[%s]", key);
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	public static class RootModule extends Module {

		public RootModule() {
			super("root", new HashSet<>(), new ModuleData(new HashMap<>()));
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		@Override
		public void setEnabled(boolean enabled) {
		}
	}

	public static class ModuleFile {

		public static final Path file = CoreModule.getInstance().getModuleFolder().resolve("modules");
		private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

		public static void load() {
			root = new RootModule();
			try {
				if (!Files.exists(file)) {
					return;
				} 
				BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
				JsonArray modulesArray = gson.fromJson(reader, JsonArray.class);
				reader.close();
				if (modulesArray == null) {
					return;
				}
				Map<String, Module> modules = new HashMap<>();
				modules.put("root", root);
				Map<Module, Collection<String>> childs = new HashMap<>();
				for (JsonElement element : modulesArray) {
					JsonObject mdata = element.getAsJsonObject();
					JsonArray childKeys = mdata.get("childKeys").getAsJsonArray();
					ModuleData data = ModuleData.deserialize(mdata.get("data").getAsJsonArray());
					boolean enabled = mdata.get("enabled").getAsBoolean();
					String key = mdata.get("key").getAsString();
					Set<String> childsSet = new HashSet<>();
					childKeys.forEach(e -> childsSet.add(e.getAsString()));
					if (key.equals("root")) {
						childs.put(root, childsSet);
						continue;
					}
					Module module = new Module(key, new HashSet<>(), data);
					module.setEnabled(enabled);
					modules.put(key, module);
					childs.put(module, childsSet);
				}
				for (Module module : childs.keySet()) {
					for (String childName : childs.get(module)) {
						module.addChild(modules.get(childName));
					}
				}
//				for (Module module : childs.keySet()) {
//					CoreModule.getInstance()
//							.getLogger()
//							.info("Found module " + module.key + " with childs\n"
//									+ module.getChilds().stream().map(Module::getKey).collect(Collectors.toList())
//									+ ", enabled=" + module.isEnabled() + " and data\n" + module.getData().serialize());
//				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		public static void save() {
			Set<Module> modules = new HashSet<>();
			Set<Module> toWork = new HashSet<>();
			toWork.add(root);
			while (!toWork.isEmpty()) {
				List<Module> t = new ArrayList<>();
				for (Module m : toWork) {
					modules.add(m);
					t.add(m);
				}
				toWork.clear();
				for (Module m : t) {
					toWork.addAll(m.getChilds());
				}
			}

			try {
				BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
						StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
				JsonArray modulesArray = new JsonArray();
				for (Module m : modules) {
					Collection<Module> childs = m.getChilds();
					JsonArray childKeys = new JsonArray();
					childs.stream().map(Module::getKey).map(JsonPrimitive::new).forEach(childKeys::add);
					JsonArray data = m.getData().serialize();
					JsonObject mdata = new JsonObject();
					mdata.add("childKeys", childKeys);
					mdata.add("data", data);
					mdata.add("key", new JsonPrimitive(m.getKey()));
					mdata.add("enabled", new JsonPrimitive(m.isEnabled()));
					modulesArray.add(mdata);
//					gson.toJson(mdata, writer);
				}
				gson.toJson(modulesArray, writer);
				writer.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
