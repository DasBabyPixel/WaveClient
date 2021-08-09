package de.dasbabypixel.waveclient.module.core.gui.api;

import de.dasbabypixel.waveclient.module.core.util.color.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class Text {

	private final FontRenderer font;
	private final String content;
	private final Color color;

	public Text(String content) {
		this(content, new Color(0, 0, 0, 255));
	}

	public Text(String content, Color color) {
		this(Minecraft.getMinecraft().fontRendererObj, content, color);
	}

	public Text(FontRenderer font, String content, Color color) {
		this.font = font;
		this.content = content;
		this.color = color;
	}

	public FontRenderer getFont() {
		return font;
	}

	public Color getColor() {
		return color;
	}

	public String getContent() {
		return content;
	}

}
