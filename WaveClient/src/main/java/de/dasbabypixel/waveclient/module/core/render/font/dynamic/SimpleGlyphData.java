package de.dasbabypixel.waveclient.module.core.render.font.dynamic;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;

public class SimpleGlyphData implements GlyphData {

	public static final Graphics2D dummyGraphics = GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getDefaultScreenDevice()
			.getDefaultConfiguration()
			.createCompatibleImage(1, 1, Transparency.TRANSLUCENT)
			.createGraphics();

	private final int size;
	private final int ascent;
	private final int maxWidth;
	private final int maxHeight;
	private final int width;
	private final int height;
	private final Font font;

	public SimpleGlyphData(final DynamicWaveFontRenderer renderer, final char c, final int size) {
		this.font = renderer.getFont().getFont().deriveFont(0, 4.0F * size);
		final FontMetrics metrics = dummyGraphics.getFontMetrics(font);
		final Rectangle2D bounds = metrics.getMaxCharBounds(dummyGraphics);
		this.size = size;
		this.maxWidth = bounds.getBounds().width;
		this.maxHeight = bounds.getBounds().height;
		this.ascent = metrics.getMaxAscent();
		this.width = metrics.charWidth(c);
		this.height = metrics.getHeight();
	}

	public Font getFont() {
		return font;
	}

	@Override
	public int getAscent() {
		return ascent;
	}

	@Override
	public int getMaxHeight() {
		return maxHeight;
	}

	@Override
	public int getMaxWidth() {
		return maxWidth;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getSize() {
		return size;
	}

}
