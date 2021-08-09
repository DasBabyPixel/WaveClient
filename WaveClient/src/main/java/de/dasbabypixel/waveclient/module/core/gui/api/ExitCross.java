package de.dasbabypixel.waveclient.module.core.gui.api;

import de.dasbabypixel.waveclient.module.core.render.DrawHelper;

public class ExitCross extends AbstractGui {

	private double rectX, rectY, rectWidth, rectHeight;
	private double crossX, crossY, crossWidth, crossHeight, lineWidth;
	private int crossColor, crossHoverColor, rectColor;

	public ExitCross() {
		super(0, 0);
	}

	public void setValues(double rectX, double rectY, double rectWidth, double rectHeight, int rectColor, double crossX,
			double crossY, double crossWidth, double crossHeight, double lineWidth, int crossColor,
			int crossHoverColor) {
		this.rectX = rectX;
		this.rectY = rectY;
		this.rectWidth = rectWidth;
		this.rectHeight = rectHeight;
		this.rectColor = rectColor;
		this.crossX = crossX;
		this.crossY = crossY;
		this.crossWidth = crossWidth;
		this.crossHeight = crossHeight;
		this.lineWidth = lineWidth;
		this.crossColor = crossColor;
		this.crossHoverColor = crossHoverColor;
		setX(rectX);
		setY(rectY);
		setWidth(rectWidth);
		setHeight(rectHeight);
	}

	@Override
	public void draw(double mouseX, double mouseY) {
		DrawHelper.drawRect(rectX, rectY, rectWidth, rectHeight, rectColor);
		DrawHelper.drawCross(crossX, crossY, crossWidth, crossHeight, lineWidth,
				isHovering(mouseX, mouseY) ? crossHoverColor : crossColor);
	}

}