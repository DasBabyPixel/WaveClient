package de.dasbabypixel.waveclient.module.core.gui.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.dasbabypixel.waveclient.module.core.listener.BaseListener;
import de.dasbabypixel.waveclient.module.core.module.Module;
import de.dasbabypixel.waveclient.module.core.module.Module.RootModule;
import de.dasbabypixel.waveclient.module.core.module.ModuleConfigureEvent;
import de.dasbabypixel.waveclient.module.core.module.ModuleData;
import de.dasbabypixel.waveclient.module.core.module.ModuleDataObject;
import de.dasbabypixel.waveclient.module.core.render.Images;
import de.dasbabypixel.waveclient.wrappedloader.api.render.DrawHelper;
import de.dasbabypixel.waveclient.wrappedloader.api.util.Color;

public class ModuleGui extends AbstractMovableGui implements ModuleDataObject {

	public static final String DATA_KEY = "modulegui";
	public static final String SELECTED_DATA_KEY = "moduleguiselected";

	private boolean initialized = false;
	private double x = 10, y = 10;
	private boolean collapsed = true;

	public ModuleGui() {
		super(0, 0);
	}

	@Override
	public void draw(double mouseX, double mouseY) {
		DrawHelper.drawGradientRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(),
				new Color(0, 255, 0, 255), new Color(0, 255, 255, 255));
		DrawHelper.drawImageToArea(Images.wavelogo, getX(), getY(), getWidth(), getHeight());
	}

	@Override
	public JsonElement serialize() {
		JsonObject object = new JsonObject();
		object.addProperty("x", x);
		object.addProperty("y", y);
		object.addProperty("collapsed", collapsed);
		return object;
	}

	@Override
	public void deserialize(JsonElement element) {
		JsonObject object = element.getAsJsonObject();
		x = object.get("x").getAsDouble();
		y = object.get("y").getAsDouble();
		collapsed = object.get("collapsed").getAsBoolean();
		this.initialize();
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public void setX(double x) {
		this.x = x;
	}

	@Override
	public void setY(double y) {
		this.y = y;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void initialize() {
		initialized = true;
	}

	public static class Listener extends BaseListener<ModuleConfigureEvent> {

		public Listener() {
			super(ModuleConfigureEvent.class, event -> {
				if (event.getModule() instanceof RootModule) {
					ModuleData data = event.getModule().getData();
					if (!data.has(SELECTED_DATA_KEY)) {
						data.set(SELECTED_DATA_KEY, new Selected());
					}
					return;
				}
				Module module = event.getModule();
				ModuleData data = module.getData();
				if (!data.has(DATA_KEY)) {
					data.set(DATA_KEY, new ModuleGui());
				}
			});
		}
	}

	public static class Selected implements ModuleDataObject {

		public String selected = null;

		@Override
		public JsonElement serialize() {
			return selected == null ? JsonNull.INSTANCE : new JsonPrimitive(selected);
		}

		@Override
		public void deserialize(JsonElement json) {
			this.selected = (json == null || json.isJsonNull()) ? null : json.getAsString();
		}
	}
}
