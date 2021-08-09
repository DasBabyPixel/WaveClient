package de.dasbabypixel.waveclient.module.core.keybind;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.lwjgl.input.Keyboard;

import de.dasbabypixel.waveclient.module.Module;
import de.dasbabypixel.waveclient.module.core.CoreModule;
import de.dasbabypixel.waveclient.module.core.util.Handler;

public class Keybind {

	public static final Map<Module, Set<Keybind>> keybinds = new HashMap<>();

	private final Module owner;
	private int keycode;
	private int defaultKeyCode;
	private String key;
	private int pressTime = 0;
	private boolean pressed = false;
	private boolean unpressed = false;
	private Collection<Handler<Keybind>> handlers = new ArrayList<>();

	public Keybind(final Module owner, final String key, final int defaultKeycode) {
		this.owner = owner;
		this.key = key;
		this.defaultKeyCode = defaultKeycode;
		this.keycode = defaultKeycode;
		updateKey();

		register();
	}

	public void updateKey() {
		if (KeyFile.has(key)) {
			keycode = KeyFile.get(key);
		}
	}

	public int getDefaultKeyCode() {
		return defaultKeyCode;
	}

	public String getKey() {
		return key;
	}

	public int getKeyCode() {
		return keycode == 0 ? -1 : keycode;
	}

	public void registerHandler(Handler<Keybind> handler) {
		handlers.add(handler);
	}

	public void unregisterHandler(Handler<Keybind> handler) {
		handlers.remove(handler);
	}

	public void register() {
		getBinds(owner).add(this);
	}

	public void unregister() {
		getBinds(owner).remove(this);
	}

	public static void tick() {
		keybinds.values().forEach(l -> l.forEach(Keybind::tickBind));
	}

	private static Set<Keybind> getBinds(Module module) {
		if (!keybinds.containsKey(module)) {
			keybinds.put(module, new HashSet<>());
		}
		return keybinds.get(module);
	}

	private void tickBind() {
		if (check()) {
			pressTime++;
		}
		handlers.forEach(h -> h.handle(this));
	}

	private boolean check() {
		int code = getKeyCode();
		if (code < 0 || code >= Keyboard.KEYBOARD_SIZE) {
			return false;
		}
		if (Keyboard.isKeyDown(code)) {
			if (!pressed) {
				pressed = true;
			}
			return true;
		}
		if (pressed) {
			pressed = false;
			pressTime = 0;
			unpressed = true;
		} else {
			unpressed = false;
		}
		return false;
	}

	public int getPressTime() {
		return pressTime;
	}

	public void setKeyCode(int keyCode) {
		this.keycode = keyCode;
		KeyFile.set(key, keyCode);
		check();
	}

	public boolean isKeyDown() {
		return pressed;
	}

	public boolean isKeyUp() {
		return !pressed;
	}

	public boolean isSingleReleased() {
		return unpressed;
	}

	public boolean isSinglePressed() {
		return isKeyDown() && pressTime == 1;
	}

	private static class KeyFile {

		private static Properties properties;
		private static final Path keys = CoreModule.getInstance().getModuleFolder().resolve("keys.properties");

		static {
			try {
				if (!Files.exists(keys)) {
					Files.createFile(keys);
				}
				properties = new Properties();
				BufferedReader reader = Files.newBufferedReader(keys, StandardCharsets.UTF_8);
				properties.load(reader);
				reader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		public static boolean has(String key) {
			return properties.containsKey(key);
		}

		public static void set(String key, int value) {
			properties.put(key, Integer.toString(value));
			save();
		}

		public static int get(String key) {
			String val = properties.getProperty(key);
			int v = 0;
			try {
				if (val != null) {
					v = Integer.parseInt(val);
				}
			} catch (Exception e) {
				set(key, v);
			}
			return v;
		}

		public static void save() {
			try {
				BufferedWriter writer = Files.newBufferedWriter(keys, StandardCharsets.UTF_8,
						StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
				properties.store(writer, "Keybinds");
				writer.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	public static class KeybindHandler extends Handler<Keybind> {

		public KeybindHandler(Consumer<Keybind> consumer, Type type) {
			super(type.getPredicate(), consumer);
		}

		public static enum Type {
			// @formatter:off
			ON_PRESS(Keybind::isSinglePressed), 
			WHILE_PRESSED(Keybind::isKeyDown),
			WHILE_RELEASED(Keybind::isKeyUp),
			ON_RELEASE(Keybind::isSingleReleased)
			// @formatter:on
			;

			private final Predicate<Keybind> predicate;

			private Type(Predicate<Keybind> predicate) {
				this.predicate = predicate;
			}

			public Predicate<Keybind> getPredicate() {
				return predicate;
			}
		}
	}

}
