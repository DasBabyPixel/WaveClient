package de.dasbabypixel.waveclient.module.core.render.font.dynamic;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import de.dasbabypixel.waveclient.module.core.resource.texture.AbstractTexture;
import de.dasbabypixel.waveclient.module.core.resource.texture.SimpleTexture;

public class EmptyBitMap implements BitMap {

	private final BufferedImage image;
	private final AbstractTexture texture;
	private final Rectangle2D coords = new Rectangle2D.Float(0, 0, 1, 1);
	private final Glyph glyph;

	EmptyBitMap() {
		final GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice()
				.getDefaultConfiguration();
		this.image = config.createCompatibleImage(1, 1, Transparency.TRANSLUCENT);
		this.texture = new SimpleTexture();
		this.texture.uploadImage(image);
		this.glyph = new EmptyGlyph();
	}
	
	@Override
	public Glyph getAnyGlyph() {
		return glyph;
	}

	@Override
	public AbstractTexture texture() {
		return texture;
	}

	@Override
	public BufferedImage image() {
		return image;
	}

	@Override
	public boolean hasGlyph(char c) {
		return true;
	}

	@Override
	public Glyph getGlyph(char c) {
		return glyph;
	}

	@Override
	public Rectangle2D getGlyphCoords(char c) {
		return coords;
	}

}
