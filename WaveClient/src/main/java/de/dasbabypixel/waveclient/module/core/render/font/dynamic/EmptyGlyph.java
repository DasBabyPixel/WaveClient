package de.dasbabypixel.waveclient.module.core.render.font.dynamic;

import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

public class EmptyGlyph implements Glyph {

	private final BufferedImage image;

	public EmptyGlyph() {
		this.image = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice()
				.getDefaultConfiguration()
				.createCompatibleImage(1, 1, Transparency.TRANSLUCENT);
	}

	@Override
	public BufferedImage getImage() {
		return image;
	}

	@Override
	public int getAscent() {
		return 0;
	}
	
	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public int getMaxHeight() {
		return 1;
	}

	@Override
	public int getMaxWidth() {
		return 1;
	}

	@Override
	public int getWidth() {
		return 1;
	}

	@Override
	public int getHeight() {
		return 1;
	}
}
