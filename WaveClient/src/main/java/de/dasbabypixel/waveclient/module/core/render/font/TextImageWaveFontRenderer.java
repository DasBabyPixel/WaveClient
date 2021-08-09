package de.dasbabypixel.waveclient.module.core.render.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import de.dasbabypixel.waveclient.module.core.render.DrawHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;

public class TextImageWaveFontRenderer implements Closeable {

	private final WaveFont font;
	private final Map<String, TextImage> images = new ConcurrentHashMap<>();
	private static final Color BACK_COLOR = new Color(255, 255, 255, 0);

	public TextImageWaveFontRenderer(WaveFont font) {
		this.font = font;
	}

	public TextImage allocateImage(String text, int precision,
			de.dasbabypixel.waveclient.module.core.util.color.Color color) {
		if (images.containsKey(text)) {
			return images.get(text);
		}
		TextImage image = new TextImage(text, new DynamicTexture(createBufferedImage(text, precision, color)));
		images.put(text, image);
		return image;
	}

	@Override
	public void close() {
		images.values().forEach(t -> {
			t.close();
		});
	}

	public class TextImage implements Closeable {
		private final String text;
		private final DynamicTexture image;

		public TextImage(String text, DynamicTexture image) {
			this.text = text;
			this.image = image;
		}

		@Override
		public void close() {
			images.remove(text);
			image.deleteGlTexture();
		}
		
		public DynamicTexture getImage() {
			return image;
		}

		public void draw(double x, double y, double width, double height) {
			GlStateManager.bindTexture(image.getGlTextureId());
			DrawHelper.drawImageToArea(x, y, width, height);
		}
	}

	public BufferedImage createBufferedImage(String text, int precision,
			de.dasbabypixel.waveclient.module.core.util.color.Color color) {
		Color awtColor = new Color(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(),
				color.getAlphaFloat());
		final int multiplier = 8;
		final Font font = new Font(this.font.getFont().getFontName(), 0, multiplier * precision);
		final GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice()
				.getDefaultConfiguration();
		final Graphics2D dummyGraphics = config.createCompatibleImage(1, 1, Transparency.TRANSLUCENT).createGraphics();
		dummyGraphics.setFont(font);
		final FontMetrics metrics = dummyGraphics.getFontMetrics();
		final Rectangle2D bounds = metrics.getStringBounds(text, dummyGraphics);
		final int width = (int) Math.ceil(bounds.getWidth());
		final int height = (int) Math.ceil(bounds.getHeight());
		BufferedImage image = config.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
		Graphics2D graphics = image.createGraphics();
		graphics.setFont(font);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		graphics.setColor(awtColor);
		graphics.setBackground(BACK_COLOR);
		graphics.drawString(text, 0, metrics.getAscent());
		try {
			ImageIO.write(image, "png", new File("string.png"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return image;
	}

	public WaveFont getFont() {
		return font;
	}
}
