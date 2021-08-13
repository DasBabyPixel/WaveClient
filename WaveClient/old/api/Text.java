package de.dasbabypixel.waveclient.module.core.gui.api;

import de.dasbabypixel.waveclient.wrappedloader.api.impl.all.render.font.minecraft.MinecraftWaveFont;
import de.dasbabypixel.waveclient.wrappedloader.api.render.font.WaveFontRenderer;
import de.dasbabypixel.waveclient.wrappedloader.api.util.Color;

public class Text {

	private final WaveFontRenderer font;
	private final String content;
	private final Color color;

	public Text(String content) {
		this(content, new Color(0, 0, 0, 255));
	}

	public Text(String content, Color color) {
		this(MinecraftWaveFont.FONT.getRenderer(), content, color);
//		this(Minecraft.getMinecraft().fontRendererObj, content, color);
	}

	public Text(WaveFontRenderer font, String content, Color color) {
		this.font = font;
		this.content = content;
		this.color = color;
	}

	public WaveFontRenderer getFont() {
		return font;
	}

	public Color getColor() {
		return color;
	}

	public String getContent() {
		return content;
	}

}
