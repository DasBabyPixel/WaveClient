package de.dasbabypixel.waveclient.module.core.render.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import de.dasbabypixel.waveclient.module.core.render.image.DeprecatedImages;
import de.dasbabypixel.waveclient.module.core.resource.ResourceManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;

@Deprecated
public class DeprecatedWaveFontRenderer extends FontRenderer {
	private static final String CHARS = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\000\000\000\000\000\000\000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\000";
	private final WaveFont font;
	private int textureId;

	private static final Color BACK_COLOR = new Color(255, 255, 255, 0);

	public DeprecatedWaveFontRenderer(WaveFont font) {
		super(Minecraft.getMinecraft().gameSettings, DeprecatedImages.defaultFont.getLocation(),
				Minecraft.getMinecraft().getTextureManager(), false);
		this.font = font;
		this.textureId = Minecraft.getMinecraft().getTextureManager().getTexture(locationFontTexture).getGlTextureId();
		this.load(getResource(font.getTTFLocation()), 2);
	}

	public ResourceLocation getLocationFontTexture() {
		return this.locationFontTexture;
	}

	private static InputStream getResource(ResourceLocation location) {
		return ResourceManager.getInstance().getResource(location).getInputStream();
	}

	public void load(InputStream fontIn, int multiplier) {
		final int size = 8;
		Font font = new Font(this.font.getFont().getFontName(), 0, size * multiplier - 3);
//		Font font = new Font(fontName, 0, 8 * multiplier - 2);
		GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice()
				.getDefaultConfiguration();
		Graphics2D graphics = graphicsConfiguration.createCompatibleImage(1, 1, 3).createGraphics();
		graphics.setFont(font);
		BufferedImage bufferedImage = graphics.getDeviceConfiguration()
//				.createCompatibleImage(128 * multiplier, 128 * multiplier, 3);
				.createCompatibleImage(size * 16 * multiplier, size * 16 * multiplier, Transparency.TRANSLUCENT);
		Graphics2D imageGraphics = (Graphics2D) bufferedImage.getGraphics();
		imageGraphics.setFont(font);
		imageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		imageGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		imageGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		imageGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		imageGraphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		imageGraphics.setColor(Color.WHITE);
		BufferedImage glyphBufferedImage = graphics.getDeviceConfiguration()
				.createCompatibleImage(size * multiplier, size * multiplier, Transparency.TRANSLUCENT);
		Graphics2D glyphImageGraphics = (Graphics2D) glyphBufferedImage.getGraphics();
		glyphImageGraphics.setFont(font);
		glyphImageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		glyphImageGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		glyphImageGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		glyphImageGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		glyphImageGraphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		glyphImageGraphics.setColor(Color.WHITE);
		glyphImageGraphics.setBackground(BACK_COLOR);
		FontMetrics metrics = graphics.getFontMetrics();
		for (int index = 0; index < CHARS.length(); index++) {
			int x = index % 16 * size * multiplier;
			int y = index / 16 * size * multiplier;
			Character c = Character.valueOf(CHARS.charAt(index));
			if (glyphImageGraphics != null && metrics != null && c != null) {
				renderChar(glyphImageGraphics, metrics, c.charValue(), index, multiplier, size);
			}
			imageGraphics.drawImage(glyphBufferedImage, null, x, y + 1);
		}
//		try {
//			ImageIO.write(bufferedImage, "png", new File("font.png"));
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
		updateFontManager(bufferedImage);
	}

	private void renderChar(Graphics2D imageGraphics, FontMetrics fontMetrics, char character, int index,
			int multiplier, int size) {
		String string = Character.toString(character);
		double height = fontMetrics.getStringBounds(string, null).getMaxY();
		if (this.charWidth != null)
			this.charWidth[index] = (Math.round(fontMetrics.stringWidth(string) / multiplier) + 1);
		imageGraphics.clearRect(0, 0, size * multiplier, size * multiplier);
		imageGraphics.drawString(string, 0, (int) ((size * multiplier) - height));
	}

	private void updateFontManager(BufferedImage bufferedImage) {
		int[] dynamicTextureData = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];
		bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), dynamicTextureData, 0,
				bufferedImage.getWidth());
		TextureUtil.allocateTexture(this.textureId, bufferedImage.getWidth(), bufferedImage.getHeight());
		TextureUtil.uploadTexture(this.textureId, dynamicTextureData, bufferedImage.getWidth(),
				bufferedImage.getHeight());
	}
}
