package de.dasbabypixel.waveclient.module.core.render.font.dynamic;

import java.awt.geom.Rectangle2D;

public interface GlyphLookup {

	Glyph getGlyph(char c);

	Rectangle2D getGlyphTexCoords(char c);

	void bindGlyph(char c);
	
	int getGlyphSize();

}
