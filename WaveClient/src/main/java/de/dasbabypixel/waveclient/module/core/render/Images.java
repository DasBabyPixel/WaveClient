package de.dasbabypixel.waveclient.module.core.render;

import de.dasbabypixel.waveclient.wrappedloader.api.resource.Image;
import de.dasbabypixel.waveclient.wrappedloader.api.resource.ResourcePath;

public class Images {

	public static final Image background = getImage("textures/background.png");
	public static final Image wavelogo = getImage("textures/wavelogo.png");
	public static final Image button = getImage("textures/button/button.png");
	public static final Image button_hover = getImage("textures/button/button_hover.png");

	private static Image getImage(String path) {
		return new Image(toPath(path));
	}

	private static ResourcePath toPath(String name) {
		return new ResourcePath("waveclient", "core/" + name);
	}
}
