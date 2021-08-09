package de.dasbabypixel.waveclient.module.core.render.image;

import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.util.ResourceLocation;

public enum Images {
	wavelogo(new ResourceLocation("waveclient", "core/textures/wavelogo.png")),
	background(new ResourceLocation("waveclient", "core/textures/background.png")),
	defaultFont(new ResourceLocation("waveclient", "core/textures/customfont/ascii.png")),
	button(new ResourceLocation("waveclient", "core/textures/button/button.png")),
	button_hover(new ResourceLocation("waveclient", "core/textures/button/button_hover.png"));

	private final ResourceLocation location;
	private final AtomicReference<Image> image = new AtomicReference<>();

	private Images(ResourceLocation location) {
		this.location = location;
	}

	private void load() {
		this.image.set(new Image(location));
	}

	public ResourceLocation getLocation() {
		return location;
	}

	public Image getImage() {
		return image.get();
	}

	public static void loadImages() {
		for (Images image : Images.values()) {
			image.load();
		}
	}
}
