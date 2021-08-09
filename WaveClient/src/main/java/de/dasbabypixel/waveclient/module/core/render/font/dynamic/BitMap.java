package de.dasbabypixel.waveclient.module.core.render.font.dynamic;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import de.dasbabypixel.waveclient.module.core.resource.texture.AbstractTexture;

public interface BitMap {
	
	AbstractTexture texture();

	BufferedImage image();

	boolean hasGlyph(char c);

	Glyph getGlyph(char c);
	
	Glyph getAnyGlyph();

	Rectangle2D getGlyphCoords(char c);

}
