package de.dasbabypixel.waveclient.module.core.gui.component;

import de.dasbabypixel.waveclient.module.core.render.Images;
import de.dasbabypixel.waveclient.module.core.render.WaveFonts;
import de.dasbabypixel.waveclient.wrappedloader.api.WaveClientAPI;
import de.dasbabypixel.waveclient.wrappedloader.api.gui.Gui;
import de.dasbabypixel.waveclient.wrappedloader.api.render.DrawHelper;
import de.dasbabypixel.waveclient.wrappedloader.api.resource.Image;
import de.dasbabypixel.waveclient.wrappedloader.api.util.Color;

public class WaveButton extends AbstractGui {

	private Image image;
	private Image hoverImage;
	private String text;
	private Color textColor;
	private Color textHoverColor;
	private int size;
	private int textIndentX = 0;
	private int textIndentY = 0;

	public WaveButton() {
		image = Images.button;
		hoverImage = Images.button_hover;
		this.size = 11;
		this.text = "No Text";
		this.textColor = new Color(200, 200, 200, 255);
		this.textHoverColor = new Color(255, 255, 255, 255);
	}

	@Override
	public void draw(double mouseX, double mouseY, float partialTicks) {
		final double x = getX();
		final double y = getY();
		final double w = getWidth();
		final double h = getHeight();
		if (Gui.isHovering(x, y, w, h, mouseX, mouseY) && hoverImage != null) {
			DrawHelper.drawImageToArea(hoverImage, x, y, w, h);
		} else if (image != null) {
			DrawHelper.drawImageToArea(image, x, y, w, h);
		}
		if (text != null) {
			double tx = x + textIndentX;
			double ty = y + textIndentY;
			double tw = w - textIndentX * 2;
			double th = h - textIndentY * 2;
			WaveClientAPI.getInstance().getRenderHelper().pushMatrix();
			if (Gui.isHovering(x, y, w, h, mouseX, mouseY)) {
				WaveClientAPI.getInstance()
						.getRenderHelper()
						.color(textHoverColor.getRedFloat(), textHoverColor.getGreenFloat(),
								textHoverColor.getBlueFloat(), textHoverColor.getAlphaFloat());
			} else {
				WaveClientAPI.getInstance()
						.getRenderHelper()
						.color(textColor.getRedFloat(), textColor.getGreenFloat(), textColor.getBlueFloat(),
								textColor.getAlphaFloat());
			}
			WaveFonts.cinzel_regular.getRenderer().drawCenteredString(text, tx, ty, tw, th, size);
			WaveClientAPI.getInstance().getRenderHelper().popMatrix();
		}
	}

	public WaveButton setTextIndent(int textIndent) {
		return this.setTextIndentX(textIndent).setTextIndentY(textIndent);
	}

	public WaveButton setTextIndentX(int textIndentX) {
		this.textIndentX = textIndentX;
		return this;
	}

	public WaveButton setTextIndentY(int textIndentY) {
		this.textIndentY = textIndentY;
		return this;
	}

	public int getTextIndentX() {
		return textIndentX;
	}

	public int getTextIndentY() {
		return textIndentY;
	}

	public String getText() {
		return text;
	}

	public WaveButton setSize(int size) {
		this.size = size;
		return this;
	}

	public int getSize() {
		return size;
	}

	public WaveButton setTextColor(Color color) {
		this.textColor = color;
		return this;
	}

	public WaveButton setTextHoverColor(Color color) {
		this.textHoverColor = color;
		return this;
	}

	public Image getImage() {
		return image;
	}

	public Image getHoverImage() {
		return hoverImage;
	}

	public WaveButton setHoverImage(Image hoverImage) {
		this.hoverImage = hoverImage;
		return this;
	}

	public WaveButton setImage(Image image) {
		this.image = image;
		return this;
	}

	public Color getTextColor() {
		return textColor;
	}

	public Color getTextHoverColor() {
		return textHoverColor;
	}

	public WaveButton setText(String text) {
		this.text = text;
		return this;
	}

}
