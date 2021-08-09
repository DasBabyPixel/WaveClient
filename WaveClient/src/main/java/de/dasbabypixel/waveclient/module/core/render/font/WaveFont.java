package de.dasbabypixel.waveclient.module.core.render.font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.dasbabypixel.waveclient.module.core.render.font.dynamic.DynamicWaveFontRenderer;
import de.dasbabypixel.waveclient.module.core.resource.ResourceManager;
import net.minecraft.util.ResourceLocation;

public class WaveFont {

	private static final List<WaveFont> fonts = new ArrayList<>();
	public static WaveFont CINZEL_REGULAR;

	private final Font font;
	private final ResourceLocation ttfLocation;
	private final DynamicWaveFontRenderer dynamicRenderer;
	private final TextImageWaveFontRenderer textImageRenderer;

	public WaveFont(ResourceLocation ttfLocation) {
		this.ttfLocation = ttfLocation;

		Font thefont = null;
		try {
			InputStream fontIn = ResourceManager.getInstance().getResource(ttfLocation).getInputStream();
			boolean found = false;
			thefont = Font.createFont(Font.TRUETYPE_FONT, fontIn);
			fontIn.close();
			for (Font f : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
				if (f.getFontName().equals(thefont.getFontName())) {
					found = true;
					break;
				}
			}
			if (!found) {
				GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(thefont);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		this.font = thefont;
		this.dynamicRenderer = new DynamicWaveFontRenderer(this);
		this.textImageRenderer = new TextImageWaveFontRenderer(this);
		fonts.add(this);
	}

	public DynamicWaveFontRenderer getDynamicRenderer() {
		return dynamicRenderer;
	}

	public TextImageWaveFontRenderer getTextImageRenderer() {
		return textImageRenderer;
	}
//
//	public SimpleWaveFontRenderer getSimpleRenderer() {
//		return simpleRenderer;
//	}

	public ResourceLocation getTTFLocation() {
		return ttfLocation;
	}

	public Font getFont() {
		return font;
	}

	public void unload() {
		this.dynamicRenderer.close();
	}

	public static void unloadFonts() {
		fonts.forEach(font -> font.unload());
	}

	public static void loadFonts() {
		CINZEL_REGULAR = new WaveFont(new ResourceLocation("waveclient", "core/fonts/cinzel_regular.ttf"));
	}
}
