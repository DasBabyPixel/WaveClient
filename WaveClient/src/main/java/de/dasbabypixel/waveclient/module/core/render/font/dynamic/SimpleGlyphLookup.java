package de.dasbabypixel.waveclient.module.core.render.font.dynamic;

import java.awt.geom.Rectangle2D;

public class SimpleGlyphLookup implements GlyphLookup {

	private final BitMap bitmap;
	private final BitMap fallback;

	public SimpleGlyphLookup(BitMap bitmap, BitMap fallback) {
		this.bitmap = bitmap;
		this.fallback = fallback;
	}

	@Override
	public Glyph getGlyph(char c) {
		return bitmap.hasGlyph(c) ? bitmap.getGlyph(c) : fallback.getGlyph(c);
	}

	@Override
	public Rectangle2D getGlyphTexCoords(char c) {
		return bitmap.hasGlyph(c) ? bitmap.getGlyphCoords(c) : fallback.getGlyphCoords(c);
	}

	@Override
	public void bindGlyph(char c) {
//		System.out.println("" + (bitmap instanceof EmptyBitMap));
		if (bitmap.hasGlyph(c)) {
			bitmap.texture().bind();
		} else {
			fallback.texture().bind();
		}
	}

	@Override
	public int getGlyphSize() {
		return bitmap.getAnyGlyph().getSize();
	}
}
