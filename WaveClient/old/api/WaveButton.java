package de.dasbabypixel.waveclient.module.core.gui.api;

import de.dasbabypixel.waveclient.module.core.render.Images;
import de.dasbabypixel.waveclient.module.core.render.WaveFonts;
import de.dasbabypixel.waveclient.wrappedloader.api.render.DrawHelper;
import de.dasbabypixel.waveclient.wrappedloader.api.resource.Image;
import de.dasbabypixel.waveclient.wrappedloader.api.util.Color;
import net.minecraft.client.renderer.GlStateManager;

public class WaveButton extends AbstractGui {

	private Image image;
	private Image hoverImage;
//	private TextImage textImage;
	private String text;
	private Color color;
	private Color hoverColor;
	private int size;
	private int textIndentX = 0;
	private int textIndentY = 0;

	public WaveButton() {
		image = Images.button;
		hoverImage = Images.button_hover;
		this.size = 11;
		this.text = "No Text";
		this.color = new Color(200, 200, 200, 255);
		this.hoverColor = new Color(255, 255, 255, 255);
//		updateTextImage();
	}

	public WaveButton setTextIndent(int indent) {
		return this.setTextIndentX(indent).setTextIndentY(indent);
	}

	public WaveButton setTextIndentX(int indentX) {
		this.textIndentX = indentX;
		return this;
	}

	public WaveButton setTextIndentY(int indentY) {
		this.textIndentY = indentY;
		return this;
	}

	public WaveButton setText(String text) {
		this.text = text;
		return this;
	}

	public WaveButton setSize(int size) {
		this.size = size;
		return this;
	}
//
//	public WaveButton updateTextImage() {
//		if (this.textImage != null) {
//			textImage.close();
//		}
//		if (this.text != null) {
//			this.textImage = WaveFont.CINZEL_REGULAR.getTextImageRenderer().allocateImage(text, precision, color);
//		}
//		return this;
//	}

	@Override
	public void draw(double mouseX, double mouseY) {
		final double x = getX();
		final double y = getY();
		final double width = getWidth();
		final double height = getHeight();
		if (this.isHovering(mouseX, mouseY) && hoverImage != null) {
			DrawHelper.drawImageToArea(hoverImage, x, y, width, height);
		} else if (image != null) {
			DrawHelper.drawImageToArea(image, x, y, width, height);
		}
		if (text != null) {
			double tx = x + textIndentX;
			double ty = y + textIndentY;
			double tw = width - textIndentX * 2;
			double th = height - textIndentY * 2;
			GlStateManager.pushMatrix();
//			DrawHelper.drawRect(tx, ty, tw, th, Color.white.toARGB());
			if (isHovering(mouseX, mouseY)) {
				GlStateManager.color(hoverColor.getRedFloat(), hoverColor.getGreenFloat(), hoverColor.getBlueFloat(),
						hoverColor.getAlphaFloat());
			} else {
				GlStateManager.color(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(),
						color.getAlphaFloat());
			}
//			textImage.draw(tx, ty, tw, th);
			WaveFonts.cinzel_regular.getRenderer().drawCenteredString(text, tx, ty, tw, th, size);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public void onClick(double mouseX, double mouseY, int mouseButton) {

	}

	@Override
	public final void onClose() {
//		if (this.textImage != null)
//			this.textImage.close();
		this.onClose0();
	}

	protected void onClose0() {
	}
}
