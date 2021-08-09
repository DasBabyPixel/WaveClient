package de.dasbabypixel.waveclient.module.core.render.font.dynamic;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

public class SimpleGlyph extends SimpleGlyphData implements Glyph {

//	private static final Map<DynamicWaveFontRenderer, Map<Character, Map<Integer, Glyph>>> glyphs = new ConcurrentHashMap<>();

	private static final Color BACKGROUND_COLOR = new Color(255, 255, 255, 0);
//	private final int size;
//	private final int ascent;
//	private final int maxWidth;
//	private final int maxHeight;
//	private final int width;
//	private final int height;
	private final BufferedImage image;

	private SimpleGlyph(final DynamicWaveFontRenderer renderer, final char c, final int size) {
		super(renderer, c, size);
//		if (!glyphs.containsKey(renderer)) {
//			glyphs.put(renderer, new ConcurrentHashMap<>());
//		}
//		if (!glyphs.get(renderer).containsKey(c)) {
//			glyphs.get(renderer).put(c, new ConcurrentHashMap<>());
//		}
//		glyphs.get(renderer).get(c).put(size, this);

//		this.size = size;
//		final Font font = renderer.getFont().getFont().deriveFont(4.0F * size);
//		final Graphics2D dummyGraphics = GraphicsEnvironment.getLocalGraphicsEnvironment()
//				.getDefaultScreenDevice()
//				.getDefaultConfiguration()
//				.createCompatibleImage(1, 1, Transparency.TRANSLUCENT)
//				.createGraphics();
//		final FontMetrics metrics = dummyGraphics.getFontMetrics(font);
//		final Rectangle2D bounds = metrics.getMaxCharBounds(dummyGraphics);
//		this.maxWidth = bounds.getBounds().width;
//		this.maxHeight = bounds.getBounds().height;
//		this.ascent = metrics.getMaxAscent();
//		this.width = metrics.charWidth(c);
//		this.height = metrics.getHeight();
		this.image = dummyGraphics.getDeviceConfiguration()
				.createCompatibleImage(getMaxWidth(), getMaxHeight(), Transparency.TRANSLUCENT);
		final Graphics2D graphics = image.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		graphics.setColor(Color.WHITE);
		graphics.setBackground(BACKGROUND_COLOR);
		graphics.setFont(getFont());
		graphics.setColor(Color.WHITE);
		graphics.drawString(Character.toString(c), 0, getAscent());
	}

	public static Glyph createGlyph(DynamicWaveFontRenderer renderer, char c, int size) {
//		if (glyphs.containsKey(renderer) && glyphs.get(renderer).containsKey(c)
//				&& glyphs.get(renderer).get(c).containsKey(size)) {
//			return glyphs.get(renderer).get(c).get(size);
//		}
		return new SimpleGlyph(renderer, c, size);
	}

//	@Override
//	public int getAscent() {
//		return ascent;
//	}
//
//	@Override
//	public int getHeight() {
//		return height;
//	}
//
//	@Override
//	public int getMaxHeight() {
//		return maxHeight;
//	}
//
//	@Override
//	public int getMaxWidth() {
//		return maxWidth;
//	}
//
//	@Override
//	public int getSize() {
//		return size;
//	}
//
//	@Override
//	public int getWidth() {
//		return width;
//	}

	@Override
	public BufferedImage getImage() {
		return image;
	}
}