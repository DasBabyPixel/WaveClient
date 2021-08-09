package de.dasbabypixel.waveclient.module.core.render.font.dynamic;

import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import de.dasbabypixel.waveclient.module.core.resource.texture.AbstractTexture;
import de.dasbabypixel.waveclient.module.core.resource.texture.SimpleTexture;

public class SimpleBitMap implements BitMap {
	private final Map<Character, Glyph> glyphs = new HashMap<>();
	private final Map<Character, Rectangle2D> glyphCoords = new HashMap<>();
	private final int fontSize;
	private final SimpleTexture texture = new SimpleTexture();
	private final BufferedImage image;
	private final DynamicWaveFontRenderer renderer;

	SimpleBitMap(final DynamicWaveFontRenderer renderer, final int fontSize, final char startChar) {
		this.fontSize = fontSize;
		this.renderer = renderer;
		final int size = 256;
		for (int i = 0; i < size; i++) {
			char c = (char) (startChar + i);
			glyphs.put(Character.valueOf(c), SimpleGlyph.createGlyph(renderer, c, fontSize));
		}
		final int width = 16;
		final int height = size / width;
		final int glyphWidth = glyphs.get(startChar).getMaxWidth();
		final int glyphHeight = glyphs.get(startChar).getMaxHeight();
		final int imagePixelWidth = width * glyphWidth;
		final int imagePixelHeight = height * glyphHeight;
		this.image = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice()
				.getDefaultConfiguration()
				.createCompatibleImage(imagePixelWidth, imagePixelHeight, Transparency.TRANSLUCENT);
		final Graphics2D graphics = image.createGraphics();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				char c = (char) (startChar + (y * height + x));
				Glyph glyph = glyphs.get(c);
				graphics.drawImage(glyph.getImage(), null, x * glyphWidth, y * glyphHeight);
				final double tx = (double) x / (double) width;
				final double ty = (double) y / (double) height;
				final double tw = 1.0 / width;
				final double th = 1.0 / height;
				glyphCoords.put(c, new Rectangle2D.Double(tx, ty, tw, th));
			}
		}
	}

	public DynamicWaveFontRenderer getRenderer() {
		return renderer;
	}

	@Override
	public Glyph getAnyGlyph() {
		return glyphs.values().stream().findAny().orElse(null);
	}

	@Override
	public boolean hasGlyph(char c) {
		return glyphs.containsKey(c);
	}

	@Override
	public Rectangle2D getGlyphCoords(char c) {
		return glyphCoords.get(c);
	}

	@Override
	public Glyph getGlyph(char c) {
		return glyphs.get(c);
	}

	@Override
	public AbstractTexture texture() {
		return texture;
	}

	@Override
	public BufferedImage image() {
		return image;
	}

	public int getFontSize() {
		return fontSize;
	}
}
