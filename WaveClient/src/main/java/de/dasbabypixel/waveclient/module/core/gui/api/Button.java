package de.dasbabypixel.waveclient.module.core.gui.api;

import de.dasbabypixel.waveclient.module.core.render.DrawHelper;
import de.dasbabypixel.waveclient.module.core.render.image.Image;
import de.dasbabypixel.waveclient.module.core.util.color.Color;

public class Button extends AbstractGui {

	private Color color;
	private Image image;
	private Text text;

	public Button(Image image, Text text, Color color) {
		super(0, 0);
		this.image = image;
		this.text = text;
	}

	@Override
	public void draw(double mouseX, double mouseY) {
		final double x = getX();
		final double y = getY();
		final double width = getWidth();
		final double height = getHeight();
		if (color != null) {
			DrawHelper.drawRect(x, y, width, height, color.toARGB());
		}
		if (image != null) {
			DrawHelper.drawImageToArea(x, y, width, height);
		}
		if (text != null) {
			DrawHelper.drawCenteredText(x, y, width, height, text);
		}
	}

	public Image getImage() {
		return image;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setText(Text text) {
		this.text = text;
	}

	public Text getText() {
		return text;
	}

	public void setImage(Image image) {
		this.image = image;
	}
}
